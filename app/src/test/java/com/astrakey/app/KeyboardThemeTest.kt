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
 * थीम-पटल टेस्ट — उपयोगकर्ता-स्क्रीनशॉट से मापे गए SwiftKey रंग + नई 4-थीम प्रणाली
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class KeyboardThemeTest {

    private fun prefs() = ApplicationProvider.getApplicationContext<android.content.Context>()
        .getSharedPreferences("astrakey_prefs", android.content.Context.MODE_PRIVATE)

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

    @Test fun `amoled is pure black`() {
        assertEquals(0xFF000000.toInt(), KeyboardTheme.AMOLED.bg)
        assertTrue(KeyboardTheme.AMOLED.keyFill != KeyboardTheme.AMOLED.bg)
    }

    @Test fun `ocean palette distinct`() {
        assertEquals(0xFF0D1B2A.toInt(), KeyboardTheme.OCEAN.bg)
        assertTrue(KeyboardTheme.OCEAN.keyFill != KeyboardTheme.DARK.keyFill)
    }

    @Test fun `dark is the default theme name`() {
        prefs().edit().clear().commit()
        assertEquals(KeyboardTheme.DARK_NAME, KeyboardTheme.currentName(prefs()))
    }

    @Test fun `theme names resolve palettes`() {
        assertEquals(KeyboardTheme.DARK, KeyboardTheme.palette(KeyboardTheme.DARK_NAME))
        assertEquals(KeyboardTheme.LIGHT, KeyboardTheme.palette(KeyboardTheme.LIGHT_NAME))
        assertEquals(KeyboardTheme.AMOLED, KeyboardTheme.palette(KeyboardTheme.AMOLED_NAME))
        assertEquals(KeyboardTheme.OCEAN, KeyboardTheme.palette(KeyboardTheme.OCEAN_NAME))
        assertEquals(KeyboardTheme.DARK, KeyboardTheme.palette("अज्ञात"))
    }

    @Test fun `legacy theme_dark bool still honored`() {
        prefs().edit().clear().commit()
        prefs().edit().putBoolean("theme_dark", false).commit()
        assertEquals(KeyboardTheme.LIGHT_NAME, KeyboardTheme.currentName(prefs()))
        prefs().edit().putString("theme_name", KeyboardTheme.AMOLED_NAME).commit()
        assertEquals(KeyboardTheme.AMOLED_NAME, KeyboardTheme.currentName(prefs()))
    }

    @Test fun `all four palettes complete`() {
        listOf(KeyboardTheme.DARK, KeyboardTheme.LIGHT, KeyboardTheme.AMOLED, KeyboardTheme.OCEAN).forEach { p ->
            assertTrue(p.pressedFill != p.keyFill)
            assertTrue(p.suggMain != 0)
            assertTrue(p.keyRadiusDp > 0f)
        }
    }

    @Test fun `four new v080 palettes complete and resolve`() {
        listOf(KeyboardTheme.SUNSET, KeyboardTheme.FOREST, KeyboardTheme.LAVENDER, KeyboardTheme.ROSE).forEach { p ->
            assertTrue(p.pressedFill != p.keyFill)
            assertTrue(p.suggMain != 0)
            assertTrue(p.keyRadiusDp > 0f)
        }
        assertEquals(KeyboardTheme.SUNSET, KeyboardTheme.palette("sunset"))
        assertEquals(KeyboardTheme.FOREST, KeyboardTheme.palette("forest"))
        assertEquals(KeyboardTheme.LAVENDER, KeyboardTheme.palette("lavender"))
        assertEquals(KeyboardTheme.ROSE, KeyboardTheme.palette("rose"))
        assertEquals(8, KeyboardTheme.ALL_NAMES.size)
    }
}
