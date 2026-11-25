package com.astrakey.app

import androidx.test.core.app.ApplicationProvider
import com.astrakey.app.ime.keyboard.KeyboardTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * थीम-पटल टेस्ट — उपयोगकर्ता-स्क्रीनशॉट से मापे गए SwiftKey रंग बरकरार हैं
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class KeyboardThemeTest {

    @Test fun `dark palette matches measured swiftkey colors`() {
        assertEquals(0xFF1C1F28.toInt(), KeyboardTheme.DARK.bg)
        assertEquals(0xFF3A3D46.toInt(), KeyboardTheme.DARK.keyFill)
        assertEquals(0xFF2D2F38.toInt(), KeyboardTheme.DARK.specialFill)
        assertEquals(0xFFFFFFFF.toInt(), KeyboardTheme.DARK.keyText)
    }

    @Test fun `light palette preserved`() {
        assertEquals(0xFFF0F1F5.toInt(), KeyboardTheme.LIGHT.bg)
        assertEquals(0xFFFFFFFF.toInt(), KeyboardTheme.LIGHT.keyFill)
        assertEquals(0xFF1A8FE3.toInt(), KeyboardTheme.LIGHT.accent)
    }

    @Test fun `dark is the default when no pref set`() {
        val prefs = ApplicationProvider.getApplicationContext<android.content.Context>()
            .getSharedPreferences("astrakey_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
        assertEquals(KeyboardTheme.Mode.DARK, KeyboardTheme.currentMode(prefs))
    }

    @Test fun `light mode readable from prefs`() {
        val prefs = ApplicationProvider.getApplicationContext<android.content.Context>()
            .getSharedPreferences("astrakey_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("theme_dark", false).commit()
        assertEquals(KeyboardTheme.Mode.LIGHT, KeyboardTheme.currentMode(prefs))
        prefs.edit().putBoolean("theme_dark", true).commit()
        assertEquals(KeyboardTheme.Mode.DARK, KeyboardTheme.currentMode(prefs))
    }

    @Test fun `palettes complete`() {
        listOf(KeyboardTheme.DARK, KeyboardTheme.LIGHT).forEach { p ->
            assertTrue(p.pressedFill != p.keyFill)
            assertTrue(p.suggMain != 0)
            assertTrue(p.keyRadiusDp > 0f)
        }
    }
}
