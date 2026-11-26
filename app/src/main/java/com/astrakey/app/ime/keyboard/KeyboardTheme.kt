package com.astrakey.app.ime.keyboard

import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable

/**
 * AstraKey रंग-पटल — उपयोगकर्ता के स्क्रीनशॉट (SwiftKey) के पिक्सेल-मापन पर आधारित।
 * 4 थीम: डार्क (डिफ़ॉल्ट), लाइट, एमोलेड (शुद्ध-काला), ओशन (नौसेना-नीला)।
 */
object KeyboardTheme {
    const val DARK_NAME = "dark"
    const val LIGHT_NAME = "light"
    const val AMOLED_NAME = "amoled"
    const val OCEAN_NAME = "ocean"
    const val SUNSET_NAME = "sunset"
    const val FOREST_NAME = "forest"
    const val LAVENDER_NAME = "lavender"
    const val ROSE_NAME = "rose"
    val ALL_NAMES = listOf(DARK_NAME, LIGHT_NAME, AMOLED_NAME, OCEAN_NAME, SUNSET_NAME, FOREST_NAME, LAVENDER_NAME, ROSE_NAME)

    data class Palette(
        val bg: Int,
        val keyFill: Int,
        val specialFill: Int,
        val pressedFill: Int,
        val keyText: Int,
        val secondaryText: Int,
        val accent: Int,
        val spaceFill: Int,
        val spaceBorder: Int,
        val toolbarIcon: Int,
        val panelBg: Int,
        val panelCard: Int,
        val suggMain: Int,
        val suggSide: Int,
        val stripBg: Int,
        val keyRadiusDp: Float
    )

    /** डार्क — स्क्रीनशॉट-मापन (उपयोगकर्ता की पसंद: डिफ़ॉल्ट) */
    val DARK = Palette(
        bg = 0xFF1C1F28.toInt(),
        keyFill = 0xFF3A3D46.toInt(),
        specialFill = 0xFF2D2F38.toInt(),
        pressedFill = 0xFF4C4F58.toInt(),
        keyText = 0xFFFFFFFF.toInt(),
        secondaryText = 0xFF9AA0A6.toInt(),
        accent = 0xFF4C9FE8.toInt(),
        spaceFill = 0xFF3A3D46.toInt(),
        spaceBorder = 0xFF6E737C.toInt(),
        toolbarIcon = 0xFFE8EAED.toInt(),
        panelBg = 0xFF22252E.toInt(),
        panelCard = 0xFF2E313A.toInt(),
        suggMain = 0xFFFFFFFF.toInt(),
        suggSide = 0xFFB9BABF.toInt(),
        stripBg = 0xFF32343C.toInt(),
        keyRadiusDp = 10f
    )

    /** लाइट — वैकल्पिक (सेटिंग्स से) */
    val LIGHT = Palette(
        bg = 0xFFF0F1F5.toInt(),
        keyFill = 0xFFFFFFFF.toInt(),
        specialFill = 0xFFDCE0E8.toInt(),
        pressedFill = 0xFFD7E9FA.toInt(),
        keyText = 0xFF202124.toInt(),
        secondaryText = 0xFF5F6368.toInt(),
        accent = 0xFF1A8FE3.toInt(),
        spaceFill = 0xFFFFFFFF.toInt(),
        spaceBorder = 0xFFBDC1C6.toInt(),
        toolbarIcon = 0xFF3C4043.toInt(),
        panelBg = 0xFFE9EBF0.toInt(),
        panelCard = 0xFFFFFFFF.toInt(),
        suggMain = 0xFF174EA6.toInt(),
        suggSide = 0xFF3C4043.toInt(),
        stripBg = 0xFFE4E7ED.toInt(),
        keyRadiusDp = 10f
    )

    /** एमोलेड — शुद्ध-काला (OLED ऊर्जा-बचत) */
    val AMOLED = Palette(
        bg = 0xFF000000.toInt(),
        keyFill = 0xFF1A1A1A.toInt(),
        specialFill = 0xFF101010.toInt(),
        pressedFill = 0xFF2E2E2E.toInt(),
        keyText = 0xFFE8EAED.toInt(),
        secondaryText = 0xFF8A8F98.toInt(),
        accent = 0xFF5FA8F0.toInt(),
        spaceFill = 0xFF1A1A1A.toInt(),
        spaceBorder = 0xFF55595F.toInt(),
        toolbarIcon = 0xFFE8EAED.toInt(),
        panelBg = 0xFF0A0A0A.toInt(),
        panelCard = 0xFF1E1E1E.toInt(),
        suggMain = 0xFFE8EAED.toInt(),
        suggSide = 0xFF9AA0A6.toInt(),
        stripBg = 0xFF141414.toInt(),
        keyRadiusDp = 10f
    )

    /** ओशन — नौसेना-नीला गहरा */
    val OCEAN = Palette(
        bg = 0xFF0D1B2A.toInt(),
        keyFill = 0xFF1B263B.toInt(),
        specialFill = 0xFF14202E.toInt(),
        pressedFill = 0xFF2C3E5D.toInt(),
        keyText = 0xFFE0E7EF.toInt(),
        secondaryText = 0xFF8FA3B8.toInt(),
        accent = 0xFF48CAE4.toInt(),
        spaceFill = 0xFF1B263B.toInt(),
        spaceBorder = 0xFF415A77.toInt(),
        toolbarIcon = 0xFFE0E7EF.toInt(),
        panelBg = 0xFF101B2C.toInt(),
        panelCard = 0xFF24344F.toInt(),
        suggMain = 0xFFE0E7EF.toInt(),
        suggSide = 0xFFA9B7C6.toInt(),
        stripBg = 0xFF152238.toInt(),
        keyRadiusDp = 10f
    )

    /** सूर्यास्त — गर्म-बैंगनी-गुलाबी (v0.8.0) */
    val SUNSET = Palette(
        bg = 0xFF2A1B26.toInt(),
        keyFill = 0xFF3E2A38.toInt(),
        specialFill = 0xFF33232C.toInt(),
        pressedFill = 0xFF55394A.toInt(),
        keyText = 0xFFF5E9EF.toInt(),
        secondaryText = 0xFFB39AAC.toInt(),
        accent = 0xFFFF8A65.toInt(),
        spaceFill = 0xFF3E2A38.toInt(),
        spaceBorder = 0xFF7A5566.toInt(),
        toolbarIcon = 0xFFF5E9EF.toInt(),
        panelBg = 0xFF241720.toInt(),
        panelCard = 0xFF3A2A33.toInt(),
        suggMain = 0xFFF5E9EF.toInt(),
        suggSide = 0xFFB39AAC.toInt(),
        stripBg = 0xFF342331.toInt(),
        keyRadiusDp = 10f
    )

    /** वन — गहरा-हरा (v0.8.0) */
    val FOREST = Palette(
        bg = 0xFF16241B.toInt(),
        keyFill = 0xFF2A3D31.toInt(),
        specialFill = 0xFF1F2F26.toInt(),
        pressedFill = 0xFF3D5747.toInt(),
        keyText = 0xFFE6F0EA.toInt(),
        secondaryText = 0xFF9DB8A8.toInt(),
        accent = 0xFF66BB6A.toInt(),
        spaceFill = 0xFF2A3D31.toInt(),
        spaceBorder = 0xFF5A7A66.toInt(),
        toolbarIcon = 0xFFE6F0EA.toInt(),
        panelBg = 0xFF1A2C21.toInt(),
        panelCard = 0xFF2E4536.toInt(),
        suggMain = 0xFFE6F0EA.toInt(),
        suggSide = 0xFF9DB8A8.toInt(),
        stripBg = 0xFF22362A.toInt(),
        keyRadiusDp = 10f
    )

    /** लैवेंडर — बैंगनी-आसमानी (v0.8.0) */
    val LAVENDER = Palette(
        bg = 0xFF232033.toInt(),
        keyFill = 0xFF393552.toInt(),
        specialFill = 0xFF2D2A42.toInt(),
        pressedFill = 0xFF4C4769.toInt(),
        keyText = 0xFFEFEDF8.toInt(),
        secondaryText = 0xFFABA6C6.toInt(),
        accent = 0xFFB39DDB.toInt(),
        spaceFill = 0xFF393552.toInt(),
        spaceBorder = 0xFF6A6490.toInt(),
        toolbarIcon = 0xFFEFEDF8.toInt(),
        panelBg = 0xFF262238.toInt(),
        panelCard = 0xFF3D3959.toInt(),
        suggMain = 0xFFEFEDF8.toInt(),
        suggSide = 0xFFABA6C6.toInt(),
        stripBg = 0xFF332F4A.toInt(),
        keyRadiusDp = 10f
    )

    /** गुलाब — गुलाबी-गोल्ड (v0.8.0) */
    val ROSE = Palette(
        bg = 0xFF2A1E20.toInt(),
        keyFill = 0xFF412F33.toInt(),
        specialFill = 0xFF352628.toInt(),
        pressedFill = 0xFF573F44.toInt(),
        keyText = 0xFFF8ECED.toInt(),
        secondaryText = 0xFFBCA2A6.toInt(),
        accent = 0xFFF48FB1.toInt(),
        spaceFill = 0xFF412F33.toInt(),
        spaceBorder = 0xFF7C5D63.toInt(),
        toolbarIcon = 0xFFF8ECED.toInt(),
        panelBg = 0xFF241A1C.toInt(),
        panelCard = 0xFF3D2D30.toInt(),
        suggMain = 0xFFF8ECED.toInt(),
        suggSide = 0xFFBCA2A6.toInt(),
        stripBg = 0xFF372A2D.toInt(),
        keyRadiusDp = 10f
    )

    fun palette(mode: String): Palette = when (mode) {
        LIGHT_NAME -> LIGHT
        AMOLED_NAME -> AMOLED
        OCEAN_NAME -> OCEAN
        SUNSET_NAME -> SUNSET
        FOREST_NAME -> FOREST
        LAVENDER_NAME -> LAVENDER
        ROSE_NAME -> ROSE
        else -> DARK
    }

    /** prefs से वर्तमान थीम-नाम (पुरानी theme_dark-संगतता सहित) */
    fun currentName(prefs: SharedPreferences): String {
        val name = prefs.getString("theme_name", null)
        if (name != null && name in ALL_NAMES) return name
        return if (prefs.getBoolean("theme_dark", true)) DARK_NAME else LIGHT_NAME
    }

    /** की-पृष्ठभूमि: सामान्य/दबा हुआ (प्रोग्राम-निर्मित — थीम-स्विच योग्य) */
    fun keyBackground(fill: Int, pressed: Int, radiusPx: Float): StateListDrawable {
        val normal = GradientDrawable()
        normal.cornerRadius = radiusPx
        normal.setColor(fill)
        val down = GradientDrawable()
        down.cornerRadius = radiusPx
        down.setColor(pressed)
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_pressed), down)
            addState(intArrayOf(), normal)
        }
    }

    /** स्पेसबार-पृष्ठभूमि: बॉर्डर-युक्त (SwiftKey स्टाइल) */
    fun spaceBackground(fill: Int, pressed: Int, border: Int, strokePx: Int, radiusPx: Float): StateListDrawable {
        val normal = GradientDrawable()
        normal.cornerRadius = radiusPx
        normal.setColor(fill)
        normal.setStroke(strokePx, border)
        val down = GradientDrawable()
        down.cornerRadius = radiusPx
        down.setColor(pressed)
        down.setStroke(strokePx, border)
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_pressed), down)
            addState(intArrayOf(), normal)
        }
    }

    /** पारदर्शी बटन (टूलबार/तीर) — दबाने पर हाइलाइट */
    fun ghostBackground(pressed: Int, radiusPx: Float): StateListDrawable {
        val normal = GradientDrawable()
        normal.cornerRadius = radiusPx
        normal.setColor(0x00000000)
        val down = GradientDrawable()
        down.cornerRadius = radiusPx
        down.setColor(pressed)
        return StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_pressed), down)
            addState(intArrayOf(), normal)
        }
    }
}
