package com.astrakey.app

import com.astrakey.app.ime.commands.CommandEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import androidx.test.core.app.ApplicationProvider

/**
 * शुद्ध-लॉजिक टेस्ट — CommandEngine की गणना-सहायक (Robolectric वातावरण)
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class CommandEngineLogicTest {

    private val engine = CommandEngine(ApplicationProvider.getApplicationContext())

    @Test fun `select all length computes`() {
        assertEquals(0, engine.computeSelectAllLength(null))
        assertEquals(5, engine.computeSelectAllLength("hello"))
        assertEquals(11, engine.computeSelectAllLength("hello world"))
        assertEquals(0, engine.computeSelectAllLength(""))
    }

    @Test fun `pinned clips survive clear`() {
        engine.clearClipHistory()
        engine.recordClip("सामान्य")
        engine.recordClip("पिन-मैं")
        engine.togglePin("पिन-मैं")
        engine.clearClipHistory()
        assertEquals(listOf("पिन-मैं"), engine.pinnedClips())
        assertTrue(engine.clipHistory().isEmpty())
        // टॉगल-हटाओ
        engine.togglePin("पिन-मैं")
        assertEquals(0, engine.pinnedClips().size)
        engine.clearClipHistory()
    }

    @Test fun `clip history records dedupes and caps at 12`() {
        engine.clearClipHistory()
        engine.recordClip("पहला")
        engine.recordClip("दूसरा")
        engine.recordClip("पहला") // डुप्लिकेट — नवीनतम स्थान पर जाए
        val h = engine.clipHistory()
        assertEquals(2, h.size)
        assertEquals("पहला", h[0])
        assertEquals("दूसरा", h[1])
        for (i in 1..20) engine.recordClip("क्लिप$i")
        assertEquals(12, engine.clipHistory().size)
        engine.clearClipHistory()
        assertEquals(0, engine.clipHistory().size)
    }
}
