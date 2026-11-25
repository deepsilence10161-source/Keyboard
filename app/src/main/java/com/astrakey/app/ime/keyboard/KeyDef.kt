package com.astrakey.app.ime.keyboard

/** एक की की परिभाषा — SwiftKey-स्टाइल (आइकन-कीज़ सहित) */
data class KeyDef(
    val label: String,
    val output: String = label,
    val alt: String? = null,
    val isSpecial: Boolean = false,
    val action: KeyAction? = null,
    val widthWeight: Float = 1f,
    val iconRes: Int? = null
)

enum class KeyAction {
    SHIFT, BACKSPACE, ENTER, SPACE, CTRL, NUMBER_TOGGLE, LANGUAGE, SETTINGS, EMOJI, CLIPBOARD,
    HIDE, ARROW_UP, ARROW_DOWN, ARROW_LEFT, ARROW_RIGHT, EMOJI_CHAR
}

/** शिफ्ट की स्थिति */
enum class ShiftState { OFF, ON_ONCE, LOCK }
