package com.astrakey.app.ime.keyboard

import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatTextView

/**
 * एक की — SwiftKey-स्टाइल गोल बटन, दबाव/लंबा-दबाव/दोहराव सहित
 */
class KeyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    var key: KeyDef = KeyDef("")
    var onKeyTap: ((KeyDef) -> Unit)? = null
    var onKeyAlt: ((KeyDef) -> Unit)? = null
    var onKeyRepeatStart: ((KeyDef) -> Unit)? = null
    var onKeyRepeatTick: ((KeyDef) -> Unit)? = null
    var onKeyRepeatStop: ((KeyDef) -> Unit)? = null

    private var downAt = 0L
    private var longFired = false
    private val longPressMs = 450L
    private var repeatHandler: android.os.Handler? = null
    private var repeatRunnable: Runnable? = null

    fun bind(def: KeyDef) {
        key = def
        text = def.label
        contentDescription = when (def.action) {
            KeyAction.SHIFT -> "shift"
            KeyAction.BACKSPACE -> "backspace"
            KeyAction.ENTER -> "enter"
            KeyAction.SPACE -> "spacebar"
            KeyAction.CTRL -> "Ctrl"
            KeyAction.NUMBER_TOGGLE -> "123"
            KeyAction.LANGUAGE -> "language"
            KeyAction.SETTINGS -> "settings"
            KeyAction.EMOJI -> "emoji"
            KeyAction.CLIPBOARD -> "clipboard"
            KeyAction.EMOJI_CHAR -> def.label
            null -> def.label
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downAt = System.currentTimeMillis()
                longFired = false
                isPressed = true
                performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                if (key.action == KeyAction.BACKSPACE) {
                    startRepeat()
                } else {
                    scheduleAlt()
                }
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isPressed = false
                cancelAlt()
                stopRepeat()
                val held = System.currentTimeMillis() - downAt
                if (!longFired && event.actionMasked == MotionEvent.ACTION_UP) {
                    if (key.action == KeyAction.BACKSPACE && held > longPressMs) {
                        // रिपीट पहले ही काम कर चुका — अतिरिक्त हटाओ नहीं
                    } else {
                        onKeyTap?.invoke(key)
                    }
                }
            }
        }
        return true
    }

    /** लंबा-दबाव → वैकल्पिक अक्षर (अल्ट) */
    fun fireLongPress() {
        longFired = true
        if (key.alt != null) onKeyAlt?.invoke(key)
    }

    private fun startRepeat() {
        val h = android.os.Handler(android.os.Looper.getMainLooper())
        repeatHandler = h
        val tick = object : Runnable {
            override fun run() {
                if (!longFired) { longFired = true; onKeyRepeatStart?.invoke(key) }
                onKeyRepeatTick?.invoke(key)
                h.postDelayed(this, 55L)
            }
        }
        repeatRunnable = tick
        h.postDelayed(tick, longPressMs)
    }

    private var altRunnable: Runnable? = null

    private fun scheduleAlt() {
        val h = android.os.Handler(android.os.Looper.getMainLooper())
        val r = Runnable {
            if (!longFired && key.alt != null) fireLongPress()
        }
        altRunnable = r
        h.postDelayed(r, longPressMs)
    }

    private fun cancelAlt() {
        altRunnable?.let { android.os.Handler(android.os.Looper.getMainLooper()).removeCallbacks(it) }
        altRunnable = null
    }

    private fun stopRepeat() {
        repeatHandler?.let { h ->
            repeatRunnable?.let { h.removeCallbacks(it) }
        }
        repeatHandler = null
        repeatRunnable = null
        onKeyRepeatStop?.invoke(key)
    }

    /** Ctrl-मोड हाईलाइट (Samsung स्टाइल) */
    fun setCtrlHighlight(on: Boolean, accentColor: Int, normalText: Int) {
        if (on) {
            backgroundTintList = android.content.res.ColorStateList.valueOf(accentColor)
            setTextColor(0xFFFFFFFF.toInt())
        } else {
            backgroundTintList = null
            setTextColor(normalText)
        }
    }
}
