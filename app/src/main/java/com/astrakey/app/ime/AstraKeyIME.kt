package com.astrakey.app.ime

import android.inputmethodservice.InputMethodService
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.astrakey.app.ime.keyboard.KeyboardView

/**
 * AstraKey का मुख्य InputMethodService — Android का आधिकारिक IME द्वार
 *
 * स्थिति-मोड (v0.8.0): full / float / left / right —
 * LatinIME-पैटर्न: onComputeInsets में contentTopInsets + TOUCHABLE_INSETS_REGION —
 * कीबोर्ड-बाह्य स्पर्श ऐप तक जाते हैं (फ्लोटिंग-कीबोर्ड का आधिकारिक तरीका)।
 */
class AstraKeyIME : InputMethodService() {

    private var keyboardView: KeyboardView? = null
    private var host: FrameLayout? = null
    private var uiMode = "full"
    private var dragStartTx = 0f
    private var dragStartTop = 0

    override fun onCreateInputView(): View {
        val kv = KeyboardView(
            this,
            icProvider = { currentInputConnection },
            editorInfoProvider = { currentInputEditorInfo },
            hideRequester = { requestHideSelf(0) },
            uiController = { action, dx, dy -> handleUi(action, dx, dy) }
        )
        keyboardView = kv
        val h = FrameLayout(this)
        h.addView(kv)
        host = h
        return h
    }

    private fun prefs() = getSharedPreferences("astrakey_prefs", MODE_PRIVATE)

    /** KeyboardView की फ्लोट-पट्टी से आदेश */
    private fun handleUi(action: String, dx: Float, dy: Float) {
        val kv = keyboardView ?: return
        when (action) {
            "expand" -> {
                prefs().edit().putString("ui_mode", "full").apply()
                applyUiMode()
            }
            "drag_start" -> {
                dragStartTx = kv.translationX
                dragStartTop = (kv.layoutParams as? FrameLayout.LayoutParams)?.topMargin ?: 0
            }
            "drag_move" -> moveFloat(kv, dx, dy, persist = false)
            "drag_end" -> moveFloat(kv, dx, dy, persist = true)
        }
    }

    /** फ्लोट-घसीटना: dx = दाएँ-धन, dy = ऊपर-धन (raw-स्क्रीन अंतर) */
    private fun moveFloat(kv: View, dx: Float, dy: Float, persist: Boolean) {
        val sw = resources.displayMetrics.widthPixels
        val maxX = sw * 0.12f
        val maxTop = (resources.displayMetrics.heightPixels * 0.28f).toInt()
        val lp = kv.layoutParams as? FrameLayout.LayoutParams ?: return
        if (uiMode == "float") {
            kv.translationX = (dragStartTx + dx).coerceIn(-maxX, maxX)
        } else if (persist && (uiMode == "left" || uiMode == "right")) {
            // एक-हाथ-मोड: आधा-स्क्रीन पार घसीटने पर पक्ष-बदल
            val left = uiMode == "left"
            val swap = if (left) dx > sw * 0.22f else dx < -sw * 0.22f
            if (swap) {
                prefs().edit().putString("ui_mode", if (left) "right" else "left").apply()
                applyUiMode()
                return
            }
        }
        lp.topMargin = (dragStartTop + dy).toInt().coerceIn(0, maxTop)
        host?.requestLayout()
        if (persist) {
            prefs().edit()
                .putInt("float_top", lp.topMargin)
                .putInt("float_tx", kv.translationX.toInt())
                .apply()
        }
    }

    /** वर्तमान-स्थिति-मोड लागू (प्रत्येक इनपुट-शुरुआत + आदेश पर) */
    private fun applyUiMode() {
        val kv = keyboardView ?: return
        val h = host ?: return
        uiMode = prefs().getString("ui_mode", "full") ?: "full"
        if (uiMode !in listOf("full", "float", "left", "right")) uiMode = "full"
        val sw = resources.displayMetrics.widthPixels
        val maxTop = (resources.displayMetrics.heightPixels * 0.28f).toInt()
        val narrow = uiMode != "full"
        val lp = FrameLayout.LayoutParams(
            if (narrow) (sw * 0.78f).toInt() else ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        when (uiMode) {
            "float" -> {
                lp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                lp.topMargin = prefs().getInt("float_top", 0).coerceIn(0, maxTop)
                kv.translationX = prefs().getInt("float_tx", 0).toFloat().coerceIn(-sw * 0.12f, sw * 0.12f)
            }
            "left" -> {
                lp.gravity = Gravity.BOTTOM or Gravity.LEFT
                lp.topMargin = prefs().getInt("float_top", 0).coerceIn(0, maxTop)
                kv.translationX = 0f
            }
            "right" -> {
                lp.gravity = Gravity.BOTTOM or Gravity.RIGHT
                lp.topMargin = prefs().getInt("float_top", 0).coerceIn(0, maxTop)
                kv.translationX = 0f
            }
            else -> kv.translationX = 0f
        }
        kv.layoutParams = lp
        kv.setFloatBarVisible(narrow)
        h.requestLayout()
    }

    /**
     * LatinIME-पैटर्न: संकरे/उठे-कीबोर्ड में केवल कीबोर्ड-क्षेत्र छू-योग्य —
     * बाकी स्पर्श ऐप तक जाते हैं; contentTopInsets से ऐप-विंडो सही सिकुड़ती है।
     */
    override fun onComputeInsets(outInsets: InputMethodService.Insets) {
        super.onComputeInsets(outInsets)
        val kv = keyboardView ?: return
        val h = host ?: return
        if (uiMode == "full" || kv.height == 0 || h.height == 0 || !kv.isShown) return
        val loc = IntArray(2)
        kv.getLocationInWindow(loc)
        val top = loc[1]
        outInsets.touchableInsets = InputMethodService.Insets.TOUCHABLE_INSETS_REGION
        outInsets.touchableRegion.set(loc[0], top, loc[0] + kv.width, h.height)
        outInsets.contentTopInsets = top
        outInsets.visibleTopInsets = top
    }

    override fun onStartInputView(editorInfo: android.view.inputmethod.EditorInfo?, restarting: Boolean) {
        super.onStartInputView(editorInfo, restarting)
        applyUiMode()
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
