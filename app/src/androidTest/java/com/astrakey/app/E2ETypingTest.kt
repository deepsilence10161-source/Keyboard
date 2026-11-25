package com.astrakey.app

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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

    /** StaleObject सुरक्षा-जाल के साथ EditText पर टैप */
    private fun tapEditText() {
        var attempts = 0
        while (attempts < 6) {
            attempts++
            try {
                val f = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 4000)
                if (f != null) { f.click(); return }
            } catch (e: androidx.test.uiautomator.StaleObjectException) {
                Thread.sleep(300)
            }
        }
        org.junit.Assert.fail("EditText पर टैप नहीं हो सका")
    }

    /** StaleObject सुरक्षा-जाल के साथ EditText टेक्स्ट पढ़ो (हर बार ताज़ा खोज) */
    private fun readFieldText(): String {
        var attempts = 0
        while (attempts < 6) {
            attempts++
            try {
                val f = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 3000)
                    ?: continue
                return f.text
            } catch (e: androidx.test.uiautomator.StaleObjectException) {
                Thread.sleep(300)
            }
        }
        org.junit.Assert.fail("EditText टेक्स्ट पढ़ा नहीं जा सका")
        return ""
    }

    @Test
    fun keyboardShowsAndTypesHello() {
        // EditText पर टैप → AstraKey कीबोर्ड खुले
        tapEditText()

        // कीबोर्ड दिखे (हमारी spacebar)
        val space = device.wait(Until.findObject(By.desc("spacebar")), 10000)
        assertNotNull("AstraKey कीबोर्ड नहीं खुला", space)

        // "hello" टाइप (ऑटो-कैप: 'h' → 'H')
        listOf("h", "e", "l", "l", "o").forEach { tapKey(it) }
        device.waitForIdle(800)
        assertEquals("Hello", readFieldText())

        // बैकस्पेस → "Hell"
        tapKey("backspace")
        device.waitForIdle(800)
        assertEquals("Hell", readFieldText())

        // शिफ्ट + h → "HellH"
        tapKey("shift")
        tapKey("h")
        device.waitForIdle(800)
        assertEquals("HellH", readFieldText())
    }

    @Test
    fun numberRowToggleAndSpaceWorks() {
        tapEditText()
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
        assertEquals("5 ", readFieldText())

        // बैकस्पेस ×2 → खाली (खाली EditText hint टेक्स्ट लौटाता है)
        tapKey("backspace")
        tapKey("backspace")
        device.waitForIdle(500)
        val hint = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.demo_hint)
        val finalText = readFieldText()
        assertTrue("अपेक्षित खाली, मिला: '$finalText'", finalText == "" || finalText == hint)
    }
}
