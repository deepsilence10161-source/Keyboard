package com.astrakey.app

import com.astrakey.app.ime.suggestion.SuggestionEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SuggestionEngineTest {

    private val engine = SuggestionEngine()

    @Test fun `english prefix gives suggestions`() {
        val out = engine.suggest("th")
        assertTrue(out.isNotEmpty())
        assertTrue(out.all { it.startsWith("t") || it.all { c -> c.code > 0x0900 } })
    }

    @Test fun `exact english word included`() {
        val out = engine.suggest("hello")
        assertTrue(out.contains("hello"))
    }

    @Test fun `hinglish transliteration namaste`() {
        val out = engine.suggest("namas")
        assertTrue(out.contains("नमस्ते"))
    }

    @Test fun `hindi first ordering in hinglish mode`() {
        val out = engine.suggest("namas", hindiFirst = true)
        assertEquals("नमस्ते", out.first())
    }

    @Test fun `english first ordering in english mode`() {
        val out = engine.suggest("namas")
        assertEquals("namaste", out.first())
    }

    @Test fun `transliteration kaise`() {
        val out = engine.suggest("kais")
        assertTrue(out.contains("कैसे"))
    }

    @Test fun `empty prefix no suggestions`() {
        assertEquals(0, engine.suggest("").size)
    }

    @Test fun `max three suggestions`() {
        val out = engine.suggest("a")
        assertTrue(out.size <= 3)
    }

    @Test fun `transliterate direct`() {
        assertNotNull(engine.transliterate("dhanyavad"))
        assertEquals("धन्यवाद", engine.transliterate("dhanyavad"))
    }

    // ---------- ऑटो-सुधार (v0.11.0) ----------

    @Test fun `correction helo to hello`() {
        assertEquals("hello", engine.bestCorrection("helo"))
    }

    @Test fun `correction teh to the`() {
        assertEquals("the", engine.bestCorrection("teh"))
    }

    @Test fun `correction wrold to world`() {
        assertEquals("world", engine.bestCorrection("wrold"))
    }

    @Test fun `in dict word no correction`() {
        assertEquals(null, engine.bestCorrection("hello"))
        assertEquals(null, engine.bestCorrection("water"))
    }

    @Test fun `gibberish no confident correction`() {
        assertEquals(null, engine.bestCorrection("zqxj"))
        assertEquals(null, engine.bestCorrection("qqqqq"))
    }

    @Test fun `edit distance basic`() {
        assertEquals(0, engine.editDistance("same", "same"))
        assertEquals(1, engine.editDistance("helo", "hello"))
        assertEquals(2, engine.editDistance("thier", "their"))
    }
}
