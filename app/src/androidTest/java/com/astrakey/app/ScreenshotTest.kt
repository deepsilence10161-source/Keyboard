package com.astrakey.app

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * स्वचालित विज़ुअल-परीक्षण — हर UI-स्थिति का स्क्रीनशॉट लेता है।
 * सहेजना: (1) ऐप-cache (क्षणिक) + (2) MediaStore Download/AstraKey —
 * क्योंकि Gradle परीक्षण-अंत में ऐप-अनइंस्टॉल से /Android/data मिट जाता है।
 * CI: adb pull /sdcard/Download/AstraKey → artifact → एजेंट स्वयं देखकर सत्यापित करता है।
 */
@RunWith(AndroidJUnit4::class)
class ScreenshotTest {

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        // शुद्ध-आधार-स्थिति — परीक्षण-क्रम के prefs-रिसाव से स्वतंत्र निश्चित स्क्रीनशॉट
        setPref("theme_dark", true)
        setPref("lang_english", true)
        setPref("number_row", false)
        setStr("ui_mode", "full")
        setStr("theme_name", "dark")
        setStr("lang_mode", "en")
        setPref("autocorrect", true)
        ActivityScenario.launch(MainActivity::class.java)
        device.waitForIdle(3000)
    }

    private fun shotDir(): File = File(
        InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
        "screenshots"
    ).apply { mkdirs() }

    private fun shot(name: String) {
        device.waitForIdle(500)
        val f = File(shotDir(), name)
        val ok = device.takeScreenshot(f, 1.0f, 90)
        assertTrue("स्क्रीनशॉट विफल: $name", ok && f.exists() && f.length() > 0)
        copyToGallery(f, name)
    }

    /** MediaStore Download/AstraKey — अनइंस्टॉल-प्रतिरोधी (API 29+) */
    private fun copyToGallery(src: File, name: String) {
        if (Build.VERSION.SDK_INT < 29) return
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Download/AstraKey")
        }
        val uri = ctx.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw AssertionError("MediaStore-सम्मिलन विफल: $name")
        ctx.contentResolver.openOutputStream(uri)?.use { os ->
            src.inputStream().use { it.copyTo(os) }
        } ?: throw AssertionError("आउटपुट-स्ट्रीम विफल: $name")
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
                val f = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 3000)
                if (f != null) f.click()
                else {
                    // न दिखे तो स्क्रॉल-पुनःप्रयास (छोटी-स्क्रीन CI)
                    device.swipe(device.displayWidth / 2, device.displayHeight * 3 / 4,
                        device.displayWidth / 2, device.displayHeight / 4, 10)
                    device.waitForIdle(400)
                }
            } catch (e: androidx.test.uiautomator.StaleObjectException) {
                Thread.sleep(300)
            }
        }
        org.junit.Assert.fail("कीबोर्ड नहीं खुला")
    }

    private fun longTapKey(desc: String) {
        var attempts = 0
        while (attempts < 6) {
            attempts++
            try {
                val key = device.wait(Until.findObject(By.desc(desc)), 4000) ?: continue
                key.longClick()
                return
            } catch (e: androidx.test.uiautomator.StaleObjectException) {
                Thread.sleep(300)
            }
        }
        org.junit.Assert.fail("लंबा-दबाव विफल: $desc")
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

    private fun setStr(key: String, value: String) {
        val ctx = InstrumentationRegistry.getInstrumentation().targetContext
        ctx.getSharedPreferences("astrakey_prefs", Context.MODE_PRIVATE)
            .edit().putString(key, value).commit()
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
            .getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        cm.setPrimaryClip(android.content.ClipData.newPlainText("test", "AstraKey क्लिपबोर्ड-परीक्षण पाठ"))
        tapKey("clipboard")
        shot("05_clip_panel.png")
        tapKey("back")

        // 06 — पूर्ण डार्क (तीर-पंक्ति सहित)
        device.waitForIdle(400)
        shot("06_dark_full.png")

        // 07 — लाइट थीम
        closeKb()
        setPref("theme_dark", false)
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("astrakey_prefs", android.content.Context.MODE_PRIVATE)
            .edit().putString("theme_name", "light").commit()
        openKb()
        shot("07_light_home.png")

        // 09 — एमोलेड थीम
        closeKb()
        InstrumentationRegistry.getInstrumentation().targetContext
            .getSharedPreferences("astrakey_prefs", android.content.Context.MODE_PRIVATE)
            .edit().putString("theme_name", "amoled").commit()
        openKb()
        shot("09_amoled_home.png")

        // 08 — हिंग्लिश (देवनागरी सुझाव)
        tapKey("language")
        listOf("k", "a", "i", "s").forEach { tapKey(it) }
        shot("08_hinglish_suggestions.png")

        // 10 — चिह्न-पैनल (लंबा-दबाव 123)
        longTapKey("123")
        shot("10_symbols.png")
        tapKey("back")

        // 11 — फ्लोटिंग मोड (संकरा कीबोर्ड + घसीटने-पट्टी)
        closeKb()
        setStr("ui_mode", "float")
        openKb()
        shot("11_floating.png")

        // 12 — एक-हाथ-बाएँ
        closeKb()
        setStr("ui_mode", "left")
        openKb()
        shot("12_onehand_left.png")

        // 13 — सूर्यास्त थीम (नई पार्श्व-पंक्ति थीम)
        closeKb()
        setStr("ui_mode", "full")
        setStr("theme_name", "sunset")
        openKb()
        shot("13_sunset_theme.png")

        // 14 — देवनागरी सीधी-क्वर्टी
        closeKb()
        setStr("theme_name", "dark")
        setStr("lang_mode", "deva")
        openKb()
        shot("14_devanagari.png")

        // 15 — क्लिप-लंबा-दबाव-मेनू (पिन/संपादित/मिटाओ)
        closeKb()
        setStr("lang_mode", "en")
        val cm2 = InstrumentationRegistry.getInstrumentation().targetContext
            .getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        cm2.setPrimaryClip(android.content.ClipData.newPlainText("test", "मेनू-परीक्षण पाठ"))
        openKb()
        tapKey("clipboard")
        val clipItem = device.wait(Until.findObject(By.desc("clip_entry")), 5000)
        if (clipItem != null) {
            clipItem.longClick()
            shot("15_clip_menu.png")
            val del = device.wait(Until.findObject(By.text("मिटाओ")), 3000)
            del?.click()
            device.waitForIdle(400)
        }
        tapKey("back")

        // 16 — पेस्ट-चिप (क्लिपबोर्ड-सामग्री सुझाव-पट्टी-में; खाली-शब्द-आवश्यकता)
        closeKb()
        cm2.setPrimaryClip(android.content.ClipData.newPlainText("test", "पेस्ट-चिप पाठ v12"))
        setStr("lang_mode", "en")
        // मुख्य-थ्रेड-सीधा-क्षेत्र-खाली (am start -S = instrumentation-स्वयं-मृत-जाल — कभी-नहीं)
        androidx.test.core.app.ActivityScenario.launch(com.astrakey.app.MainActivity::class.java).onActivity { act ->
            act.findViewById<android.widget.EditText>(com.astrakey.app.R.id.demoEdit).setText("")
        }
        device.waitForIdle(800)
        openKb()
        shot("16_paste_chip.png")

        // 17 — कैप्स-लॉक — निर्धारक: सफ़ाई+पुनर्खुलना (खाली-क्षेत्र=ऑटो-कैप ON_ONCE-गारंटीड) → 1-टैप=LOCK → "A"
        closeKb()
        androidx.test.core.app.ActivityScenario.launch(com.astrakey.app.MainActivity::class.java).onActivity { act ->
            act.findViewById<android.widget.EditText>(com.astrakey.app.R.id.demoEdit).setText("")
        }
        openKb()
        tapKey("shift") // ON_ONCE → LOCK
        tapKey("a")     // "A" कमिट — अपरकेस + चिप-गायब + shift-accent दृश्य
        device.waitForIdle(700)
        shot("17_caps_lock.png")
        tapKey("shift") // → OFF
        repeat(2) { tapKey("Ctrl"); tapKey("a"); tapKey("backspace") }

        // बहाली — डार्क + अंग्रेज़ी + पूर्ण-मोड
        closeKb()
        setPref("theme_dark", true)
        setStr("ui_mode", "full")
        setStr("theme_name", "dark")
        setStr("lang_mode", "en")
        openKb()
        tapKey("language")
        assertNotNull(device.wait(Until.findObject(By.desc("spacebar")), 6000))
    }
}
