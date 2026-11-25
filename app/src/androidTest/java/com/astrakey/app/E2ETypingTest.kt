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

    /**
     * कीबोर्ड-खुलने की पक्की पुष्टि — पहली IME-खुलने की रेस का बचाव:
     * spacebar दिखे तक दोबारा EditText-क्लिक (रिट्राई लूप), फिर ही आगे।
     */
    private fun ensureKeyboardOpen() {
        var attempts = 0
        while (attempts < 5) {
            attempts++
            if (device.wait(Until.findObject(By.desc("spacebar")), 6000) != null) return
            try {
                val f = device.wait(Until.findObject(By.clazz("android.widget.EditText")), 3000)
                f?.click()
            } catch (e: androidx.test.uiautomator.StaleObjectException) {
                Thread.sleep(300)
            }
        }
        org.junit.Assert.fail("कीबोर्ड नहीं खुला (spacebar नहीं दिखा)")
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

        // कीबोर्ड दिखे (हमारी spacebar) — ensureKeyboardOpen खुद पुष्टि करता है
        ensureKeyboardOpen()

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
        ensureKeyboardOpen()

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

    @Test
    fun arrowKeysMoveCursor() {
        tapEditText()
        ensureKeyboardOpen()

        // "abc" टाइप (ऑटो-कैप: 'a' → 'A')
        listOf("a", "b", "c").forEach { tapKey(it) }
        device.waitForIdle(800)
        assertEquals("Abc", readFieldText())

        // बाएँ तीर → कर्सर 'c' से पहले
        tapKey("arrow_left")
        device.waitForIdle(800)

        // 'x' टाइप → 'Ab' + 'x' + 'c'
        tapKey("x")
        device.waitForIdle(800)
        assertEquals("Abxc", readFieldText())
    }

    @Test
    fun hinglishTransliterationCommitsHindi() {
        tapEditText()
        ensureKeyboardOpen()

        // aअ टॉगल → हिंग्लिश (हिंदी-प्राथमिक सुझाव) मोड
        tapKey("language")
        device.waitForIdle(600)

        // "namaste" टाइप (क्वर्टी ही)
        listOf("n", "a", "m", "a", "s", "t", "e").forEach { tapKey(it) }
        device.waitForIdle(800)

        // देवनागरी सुझाव दिखे और टैप पर क्षेत्र में बदले
        val sugg = device.wait(Until.findObject(By.desc("नमस्ते")), 5000)
        assertNotNull("नमस्ते सुझाव नहीं मिला", sugg)
        sugg.click()
        device.waitForIdle(800)
        assertEquals("नमस्ते ", readFieldText())
    }

    @Test
    fun ctrlUndoRedoCopyPasteAllReal() {
        tapEditText()
        ensureKeyboardOpen()

        // "hello" टाइप → "Hello" (ऑटो-कैप)
        listOf("h", "e", "l", "l", "o").forEach { tapKey(it) }
        device.waitForIdle(800)
        assertEquals("Hello", readFieldText())

        // Ctrl+A (सब चुनो) → Ctrl+C (कॉपी) → तीर-दाएँ (कर्सर अंत) → Ctrl+V (जोड़ो)
        tapKey("Ctrl"); tapKey("a")
        tapKey("Ctrl"); tapKey("c")
        tapKey("arrow_right")
        tapKey("Ctrl"); tapKey("v")
        device.waitForIdle(1000)
        assertEquals("HelloHello", readFieldText())

        // Ctrl+Z → "Hello" (पेस्ट अनडू)
        tapKey("Ctrl"); tapKey("z")
        device.waitForIdle(1000)
        assertEquals("Hello", readFieldText())

        // Ctrl+Y → "HelloHello" (रीडू)
        tapKey("Ctrl"); tapKey("y")
        device.waitForIdle(1000)
        assertEquals("HelloHello", readFieldText())

        // Ctrl+A → Ctrl+X (कट) → खाली
        tapKey("Ctrl"); tapKey("a")
        tapKey("Ctrl"); tapKey("x")
        device.waitForIdle(1000)
        val hint = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.demo_hint)
        val afterCut = readFieldText()
        assertTrue("कट के बाद खाली होना चाहिए, मिला: '$afterCut'", afterCut == "" || afterCut == hint)

        // Ctrl+V → कटा हुआ वापस
        tapKey("Ctrl"); tapKey("v")
        device.waitForIdle(1000)
        assertEquals("HelloHello", readFieldText())
    }

    @Test
    fun keyboardSitsAboveNavBar() {
        tapEditText()
        ensureKeyboardOpen()
        val ctx = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        val rid = ctx.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        val nav = if (rid > 0) ctx.resources.getDimensionPixelSize(rid) else 0
        var bottom = -1
        var attempts = 0
        while (attempts < 6) {
            attempts++
            try {
                val a = device.wait(Until.findObject(By.desc("arrow_right")), 5000) ?: continue
                bottom = a.visibleBounds.bottom
                break
            } catch (e: androidx.test.uiautomator.StaleObjectException) {
                Thread.sleep(300)
            }
        }
        assertTrue("तीर-की नहीं मिली", bottom >= 0)
        val limit = device.displayHeight - nav + 12
        assertTrue("कीबोर्ड नेविगेशन-बार में घुसा: bottom=$bottom सीमा=$limit", bottom <= limit)
    }

    @Test
    fun toolbarIconsAndArrowRowPresent() {
        tapEditText()
        ensureKeyboardOpen()

        // टूलबार आइकन + तीर-पंक्ति + मुख्य कीज़ — सब दिखें
        listOf(
            "clipboard", "Ctrl", "123", "language", "settings", "emoji",
            "shift", "backspace", "enter", "spacebar",
            "arrow_up", "arrow_down", "arrow_left", "arrow_right"
        ).forEach { desc ->
            assertNotNull("गायब: $desc", device.wait(Until.findObject(By.desc(desc)), 4000))
        }
    }
}
