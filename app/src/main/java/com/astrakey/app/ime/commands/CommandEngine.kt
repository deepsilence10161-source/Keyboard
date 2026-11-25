package com.astrakey.app.ime.commands

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.inputmethod.ExtractedTextRequest
import android.view.inputmethod.InputConnection

/**
 * Samsung-स्टाइल Ctrl-कमांड इंजन
 * Ctrl+A = Select All, Ctrl+C = Copy, Ctrl+X = Cut, Ctrl+V = Paste
 * (Ctrl+Z/Y — अनडू/रीडू M5 में इतिहास-बफर के साथ)
 */
class CommandEngine(private val appContext: Context) {

    private val clipboard: ClipboardManager =
        appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

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

    /** वर्तमान क्लिपबोर्ड टेक्स्ट (क्लिपबोर्ड पैनल के लिए) */
    fun currentClipText(): CharSequence? {
        val clip = clipboard.primaryClip ?: return null
        if (clip.itemCount == 0) return null
        return clip.getItemAt(0).coerceToText(appContext)
    }

    /** शुद्ध-लॉजिक सहायक (यूनिट-टेस्ट योग्य): चयन-सीमा गणना */
    fun computeSelectAllLength(text: CharSequence?): Int = text?.length ?: 0
}
