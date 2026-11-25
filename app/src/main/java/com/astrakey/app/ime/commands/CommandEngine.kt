package com.astrakey.app.ime.commands

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.KeyEvent
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection
import org.json.JSONArray

/**
 * Samsung-स्टाइल Ctrl-कमांड इंजन + कर्सर-नियंत्रण + क्लिपबोर्ड-इतिहास
 * Ctrl+A = Select All, Ctrl+C = Copy, Ctrl+X = Cut, Ctrl+V = Paste
 * (Ctrl+Z/Y — अनडू/रीडू M5 में इतिहास-बफर के साथ)
 */
class CommandEngine(private val appContext: Context) {

    private val clipboard: ClipboardManager =
        appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    private val prefs = appContext.getSharedPreferences("astrakey_prefs", Context.MODE_PRIVATE)
    private val undoManager = UndoManager()

    // ---------- Ctrl-कमांड ----------

    /** Ctrl+A — पूरा टेक्स्ट चुनें */
    fun selectAll(ic: InputConnection?): Boolean {
        if (ic == null) return false
        val et = ic.getExtractedText(ExtractedTextRequest(), 0) ?: return false
        val len = et.text?.length ?: 0
        if (len == 0) return false
        return ic.setSelection(0, len)
    }

    /** Ctrl+C — चयनित टेक्स्ट कॉपी; लौटाता है कॉपी हुआ टेक्स्ट */
    fun copy(ic: InputConnection?): CharSequence? {
        val selected = ic?.getSelectedText(0) ?: return null
        if (selected.isEmpty()) return null
        clipboard.setPrimaryClip(ClipData.newPlainText("astrakey", selected))
        recordClip(selected)
        return selected
    }

    /** Ctrl+X — कट (कॉपी + चयन हटाओ) */
    fun cut(ic: InputConnection?): CharSequence? {
        val copied = copy(ic) ?: return null
        ic?.commitText("", 1) // चयनित हिस्सा हट जाता है
        return copied
    }

    /** Ctrl+V — क्लिपबोर्ड से पेस्ट */
    fun paste(ic: InputConnection?): Boolean {
        val clip = clipboard.primaryClip ?: return false
        if (clip.itemCount == 0) return false
        val text = clip.getItemAt(0).coerceToText(appContext) ?: return false
        if (text.isEmpty()) return false
        ic?.commitText(text.toString(), 1)
        return true
    }

    // ---------- अनडू/रीडू (Ctrl+Z / Ctrl+Y) ----------

    /** KeyboardView से स्नैपशॉट-जोड़ी दर्ज करो */
    fun pushUndo(before: UndoManager.Snap, after: UndoManager.Snap, isTyping: Boolean) {
        undoManager.push(before, after, isTyping)
    }

    /** Ctrl+Z — पिछला स्नैपशॉट लागू */
    fun undo(ic: InputConnection?): Boolean {
        val target = undoManager.undoTarget() ?: return false
        if (applySnap(ic, target)) { undoManager.advanceUndo(); return true }
        return false
    }

    /** Ctrl+Y — अगला स्नैपशॉट लागू */
    fun redo(ic: InputConnection?): Boolean {
        val target = undoManager.redoTarget() ?: return false
        if (applySnap(ic, target)) { undoManager.advanceRedo(); return true }
        return false
    }

    /** स्नैपशॉट बहाल: पूरा टेक्स्ट + चयन (EditText पर विश्वसनीय) */
    private fun applySnap(ic: InputConnection?, snap: UndoManager.Snap): Boolean {
        if (ic == null) return false
        val et = ic.getExtractedText(ExtractedTextRequest(), 0) ?: return false
        val cur = et.text?.toString() ?: return false
        if (cur == snap.text) {
            ic.setSelection(snap.selStart, snap.selEnd)
            return true
        }
        val beforeCount = et.selectionStart
        val afterCount = cur.length - et.selectionEnd
        ic.beginBatchEdit()
        ic.deleteSurroundingText(beforeCount, afterCount)
        ic.commitText(snap.text, 1)
        ic.setSelection(snap.selStart, snap.selEnd)
        ic.endBatchEdit()
        return true
    }

    // ---------- कर्सर-नियंत्रण (तीर-पंक्ति) ----------

    /** तीर-की घटना भेजो — EditText में कर्सर खिसकता है */
    fun moveCursor(ic: InputConnection?, dx: Int): Boolean {
        if (ic == null) return false
        val code = if (dx < 0) KeyEvent.KEYCODE_DPAD_LEFT else KeyEvent.KEYCODE_DPAD_RIGHT
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, code))
        return true
    }

    fun moveCursorVertical(ic: InputConnection?, up: Boolean): Boolean {
        if (ic == null) return false
        val code = if (up) KeyEvent.KEYCODE_DPAD_UP else KeyEvent.KEYCODE_DPAD_DOWN
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, code))
        ic.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, code))
        return true
    }

    // ---------- क्लिपबोर्ड-इतिहास (क्लिपबोर्ड पैनल) ----------

    /** इतिहास: नवीनतम पहले, डुप्लिकेट रहित, अधिकतम 12, prefs में सुरक्षित */
    fun clipHistory(): List<String> {
        val raw = prefs.getString("clip_history", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            (0 until arr.length()).mapNotNull { i -> arr.optString(i).takeIf { it.isNotEmpty() } }
        } catch (_: Exception) { emptyList() }
    }

    fun recordClip(text: CharSequence?) {
        val t = text?.toString() ?: return
        if (t.isBlank()) return
        val cur = clipHistory().toMutableList()
        cur.removeAll { it == t }
        cur.add(0, t)
        saveHistory(cur.take(12))
    }

    fun captureSystemClipboard() {
        val clip = clipboard.primaryClip ?: return
        if (clip.itemCount == 0) return
        val text = clip.getItemAt(0).coerceToText(appContext) ?: return
        recordClip(text)
    }

    fun clearClipHistory() { saveHistory(emptyList()) }

    private fun saveHistory(items: List<String>) {
        val arr = JSONArray()
        items.forEach { arr.put(it) }
        prefs.edit().putString("clip_history", arr.toString()).apply()
    }

    /** वर्तमान क्लिपबोर्ड टेक्स्ट (पैनल-सहायक) */
    fun currentClipText(): CharSequence? {
        val clip = clipboard.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(appContext)
    }

    /** शुद्ध-लॉजिक सहायक (यूनिट-टेस्ट योग्य): चयन-सीमा गणना */
    fun computeSelectAllLength(text: CharSequence?): Int = text?.length ?: 0
}
