package com.astrakey.app.ime

import android.inputmethodservice.InputMethodService
import android.view.View
import com.astrakey.app.ime.keyboard.KeyboardView

/**
 * AstraKey का मुख्य InputMethodService — Android का आधिकारिक IME द्वार
 */
class AstraKeyIME : InputMethodService() {

    private var keyboardView: KeyboardView? = null

    override fun onCreateInputView(): View {
        val kv = KeyboardView(
            this,
            icProvider = { currentInputConnection },
            editorInfoProvider = { currentInputEditorInfo },
            hideRequester = { requestHideSelf(0) }
        )
        keyboardView = kv
        return kv
    }

    override fun onStartInputView(editorInfo: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        keyboardView?.onInputStarted()
    }

    override fun onUpdateSelection(
        oldSelStart: Int, oldSelEnd: Int,
        newSelStart: Int, newSelEnd: Int,
        candidatesStart: Int, candidatesEnd: Int
    ) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        keyboardView?.updateSuggestions()
    }

    override fun onEvaluateFullscreenMode(): Boolean = false
}
