package com.astrakey.app.ime.commands

/**
 * अनडू/रीडू मैनेजर — स्नैपशॉट-स्टैक (Ctrl+Z / Ctrl+Y)।
 * लगातार अक्षर-टाइपिंग एक प्रविष्टि में जुड़ती है (शब्द-समूह — Gboard/SwiftKey जैसा)।
 * शुद्ध-लॉजिक: InputConnection रहित यूनिट-टेस्ट योग्य।
 */
class UndoManager(private val max: Int = 50) {

    data class Snap(val text: String, val selStart: Int, val selEnd: Int)
    data class Entry(val before: Snap, val after: Snap, val isTyping: Boolean)

    private val entries = mutableListOf<Entry>()
    private var pointer = -1 // entries[pointer] = अगला undo-लक्ष्य; entries[pointer+1] = अगला redo

    /** नया बदलाव दर्ज करो। isTyping=true और निरंतरता हो तो पिछली प्रविष्टि में विलय। */
    fun push(before: Snap, after: Snap, isTyping: Boolean) {
        if (before.text == after.text && before.selStart == after.selStart) return
        // नई टाइपिंग पर redo-पूँछ मिटाओ
        while (entries.size > pointer + 1) entries.removeAt(entries.size - 1)
        val last = entries.lastOrNull()
        if (last != null && isTyping && last.isTyping &&
            last.after.text == before.text &&
            last.after.selStart == before.selStart &&
            last.after.selEnd == before.selEnd &&
            last.after.text.lastOrNull()?.isLetterOrDigit() == true
        ) {
            entries[entries.size - 1] = last.copy(after = after)
        } else {
            entries.add(Entry(before, after, isTyping))
            if (entries.size > max) entries.removeAt(0)
        }
        pointer = entries.size - 1
    }

    fun canUndo(): Boolean = pointer >= 0
    fun canRedo(): Boolean = pointer + 1 < entries.size
    fun undoTarget(): Snap? = if (canUndo()) entries[pointer].before else null
    fun redoTarget(): Snap? = if (canRedo()) entries[pointer + 1].after else null
    fun advanceUndo() { if (canUndo()) pointer-- }
    fun advanceRedo() { if (canRedo()) pointer++ }
    fun size(): Int = entries.size
}
