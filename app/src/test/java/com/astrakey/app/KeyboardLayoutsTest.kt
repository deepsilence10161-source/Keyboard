package com.astrakey.app

import com.astrakey.app.ime.keyboard.KeyboardLayouts
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * लेआउट-भंडार टेस्ट — क्वर्टी + देवनागरी सीधी-क्वर्टी (v0.9.0)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class KeyboardLayoutsTest {

    @Test fun `devanagari rows complete`() {
        val rows = KeyboardLayouts.hindiRows()
        assertEquals(4, rows.size)
        assertTrue(rows[0].any { it.label == "अ" })
        assertTrue(rows[1].any { it.label == "क" })
        assertTrue(rows[2].any { it.label == "त" })
        assertTrue(rows[3].any { it.label == "ह" })
    }

    @Test fun `english rows preserve qwerty`() {
        val rows = KeyboardLayouts.englishRows(false)
        assertEquals(3, rows.size)
        assertTrue(rows[0].any { it.label == "q" })
        assertTrue(rows[1].any { it.label == "a" })
    }

    @Test fun `ctrl targets match samsung style`() {
        assertEquals(setOf("a", "c", "v", "x", "z", "y"), KeyboardLayouts.ctrlTargetKeys)
    }
}
