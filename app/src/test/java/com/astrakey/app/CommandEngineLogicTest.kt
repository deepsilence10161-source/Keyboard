package com.astrakey.app

import com.astrakey.app.ime.commands.CommandEngine
import org.junit.Assert.assertEquals
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
}
