package com.astrakey.app

import com.astrakey.app.ime.commands.UndoManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * अनडू/रीडू मैनेजर — शुद्ध-लॉजिक टेस्ट (शब्द-समूहन, रीडू-पूँछ, क्षमता-सीमा)
 */
class UndoManagerTest {

    private fun snap(t: String) = UndoManager.Snap(t, t.length, t.length)

    @Test fun `letters coalesce into word chunks`() {
        val m = UndoManager()
        m.push(snap(""), snap("H"), true)
        m.push(snap("H"), snap("He"), true)
        m.push(snap("He"), snap("Hel"), true)
        assertEquals(1, m.size())
        assertTrue(m.canUndo())
        assertEquals("", m.undoTarget()!!.text)
        m.advanceUndo()
        assertFalse(m.canUndo())
        assertTrue(m.canRedo())
        assertEquals("Hel", m.redoTarget()!!.text)
        m.advanceRedo()
        assertFalse(m.canRedo())
    }

    @Test fun `new word breaks coalescing`() {
        val m = UndoManager()
        m.push(snap(""), snap("Hi"), true)
        m.push(snap("Hi "), snap("Hi w"), true) // बीच में स्पेस — निरंतरता टूटी
        assertEquals(2, m.size())
        assertEquals("Hi ", m.undoTarget()!!.text) // अंतिम शब्द पहले उठेगा
    }

    @Test fun `redo tail cleared on new typing`() {
        val m = UndoManager()
        m.push(snap(""), snap("a"), true)
        m.advanceUndo()
        m.push(snap(""), snap("b"), true)
        assertFalse(m.canRedo())
        assertEquals(1, m.size())
    }

    @Test fun `non typing ops never coalesce`() {
        val m = UndoManager()
        m.push(snap("abc"), snap("abcabc"), false) // पेस्ट
        m.push(snap("abcabc"), snap("abcabcx"), true) // अक्षर
        assertEquals(2, m.size())
    }

    @Test fun `cap at fifty entries`() {
        val m = UndoManager()
        for (i in 1..60) m.push(snap("$i"), snap("${i}x"), false)
        assertEquals(50, m.size())
    }
}
