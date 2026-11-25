package com.astrakey.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * स्वचालित विज़ुअल-परीक्षण — हर UI-स्थिति का स्क्रीनशॉट लेता है;
 * CI इन्हें artifact बनाता है और एजेंट स्वयं देखकर सत्यापित करता है।
 * उपयोगकर्ता-निर्देश: "तुम स्वयं परीक्षण करो" — मैनुअल-परीक्षण शून्य।
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotTest {

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        ActivityScenario.launch(MainActivity::class.java)
        device.waitForIdle(3000)
    }

    private fun shotDir(): File = File(
        InstrumentationRegistry.getInstrumentation().targetContext.getExternalFilesDir(null),
        "screenshots"
    ).apply { mkdirs() }

    private fun shot(name: String) {
        device.waitForIdle(500)
        device.takeScreenshot(File(shotDir(), name), 1.0f, 90)
    }

    private fun tapKey(desc: String) {
        var attempts = 0
        while (attempts < 6) {
            attempts++
            try {
                val key = device.wait(Until.findObject(By.desc(desc)), 4000) ?: continue
                key.click()
                return
            } catch (e: androidx.test.uiautomator.StaleObjectException) {
                Thread.sleep(300)
            }
        }
        org.junit.Assert.fail("की नहीं मिली: $desc")
    }

    private fun openKb() {
        var attempts = 0
        while (attempts < 5) {
            attempts++
            if (device.wait(Until.findObject(By.desc("spacebar")), 6000) != null) return
            try {
                device.wait(Until.findObject(By.clazz("android.widget.EditText")), 3000)?.click()
            } catch (e: androidx.test.uiautomator.StaleObjectException) {
                Thread.sleep(300)
            }
        }
        org.junit.Assert.fail("कीबोर्ड नहीं खुला")
    }

    private fun closeKb() {
        device.pressBack()
        Thread.sleep(500)
    }

    private fun setPref(key: String, value: Boolean) {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        ctx.getSharedPreferences("astrakey_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean(key, value).commit()
    }

    @Test
    fun captureAllKeyboardStates() {
        // 01 — डार्क (डिफ़ॉल्ट)
        setPref("theme_dark", true)
        openKb()
        shot("01_dark_home.png")

        // 02 — नंबर-रो
        tapKey("123")
        shot("02_dark_numberrow.png")
        tapKey("123")

        // 03 — Ctrl-मोड (Samsung हाइलाइट)
        tapKey("Ctrl")
        shot("03_dark_ctrlmode.png")
        tapKey("Ctrl")

        // 04 — इमोजी पैनल
        tapKey("emoji")
        shot("04_emoji_panel.png")
        tapKey("back")

        // 05 — क्लिपबोर्ड पैनल
        val cm = InstrumentationRegistry.getInstrumentation().targetContext
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        cm.setPrimaryClip(ClipData.newPlainText("test", "AstraKey क्लिपबोर्ड-परीक्षण पाठ"))
        tapKey("clipboard")
        shot("05_clip_panel.png")
        tapKey("back")

        // 06 — पूर्ण डार्क (तीर-पंक्ति सहित)
        device.waitForIdle(400)
        shot("06_dark_full.png")

        // 07 — लाइट थीम
        closeKb()
        setPref("theme_dark", false)
        openKb()
        shot("07_light_home.png")

        // 08 — हिंग्लिश (देवनागरी सुझाव)
        tapKey("language")
        listOf("k", "a", "i", "s").forEach { tapKey(it) }
        shot("08_hinglish_suggestions.png")

        // बहाली — डार्क + अंग्रेज़ी
        closeKb()
        setPref("theme_dark", true)
        openKb()
        tapKey("language")
        assertNotNull(device.wait(Until.findObject(By.desc("spacebar")), 6000))
    }
}
