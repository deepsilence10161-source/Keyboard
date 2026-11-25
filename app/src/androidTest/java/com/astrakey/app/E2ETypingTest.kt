package com.astrakey.app

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * E2E लाइव टेस्ट — असली एमुलेटर पर AstraKey कीबोर्ड से टाइपिंग
 * CI इससे पहले adb shell ime set से AstraKey को डिफ़ॉल्ट IME बना देता है।
 */
@RunWith(AndroidJUnit4::class)
class E2ETypingTest {

    private lateinit var device: UiDevice

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        ActivityScenario.launch(MainActivity::class.java)
        device.waitForIdle(3000)
    }

    private fun tapKey(desc: String) {
        val key = device.wait(Until.findObject(By.desc(desc)), 4000)
        assertNotNull("की नहीं मिली: $desc", key)
        key.click()
    }

    @Test
    fun keyboardShowsAndTypesHello() {
        // EditText पर टैप → AstraKey कीबोर्ड खुले
        val field = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 6000)
        assertNotNull("demo EditText नहीं मिला", field)
        field.click()

        // कीबोर्ड दिखे (हमारी spacebar)
        val space = device.wait(Until.findObject(By.desc("spacebar")), 10000)
        assertNotNull("AstraKey कीबोर्ड नहीं खुला", space)

        // "hello" टाइप (ऑटो-कैप: 'h' → 'H')
        listOf("h", "e", "l", "l", "o").forEach { tapKey(it) }
        device.waitForIdle(800)
        val after = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 3000)
        assertNotNull(after)
        assertEquals("Hello", after.text)

        // बैकस्पेस → "Hell"
        tapKey("backspace")
        device.waitForIdle(800)
        val afterBk = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 3000)
        assertEquals("Hell", afterBk.text)

        // शिफ्ट + h → "HellH"
        tapKey("shift")
        tapKey("h")
        device.waitForIdle(800)
        val afterShift = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 3000)
        assertEquals("HellH", afterShift.text)
    }

    @Test
    fun numberRowToggleAndSpaceWorks() {
        val field = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 6000)
        assertNotNull(field)
        field.click()
        device.wait(Until.findObject(By.desc("spacebar")), 10000)

        // 123 टॉगल → नंबर-रो दिखे
        tapKey("123")
        device.waitForIdle(500)
        val five = device.wait(Until.findObject(By.desc("5")), 4000)
        assertNotNull("नंबर-रो नहीं खुली", five)
        five.click()

        // स्पेस
        tapKey("spacebar")
        device.waitForIdle(500)
        val after = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 3000)
        assertEquals("5 ", after.text)

        // बैकस्पेस ×2 → खाली
        tapKey("backspace")
        tapKey("backspace")
        device.waitForIdle(500)
        val afterDel = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 3000)
        assertEquals("", afterDel.text)
    }
}
