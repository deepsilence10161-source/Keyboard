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
}
