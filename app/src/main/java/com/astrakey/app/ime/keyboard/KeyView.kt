package com.astrakey.app.ime.keyboard

import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView

/**
 * एक की — SwiftKey-स्टाइल: लेबल (टेक्स्ट) या वेक्टर-आइकन, दबाव/लंबा-दबाव/दोहराव सहित।
 * FrameLayout + आंतरिक TextView/ImageView — स्पर्श-तर्क KeyView पर ही रहता है।
 */
class KeyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    var key: KeyDef = KeyDef("")
    var onKeyTap: ((KeyDef) -> Unit)? = null
    var onKeyAlt: ((KeyDef) -> Unit)? = null
    var onKeyRepeatStart: ((KeyDef) -> Unit)? = null
    var onKeyRepeatTick: ((KeyDef) -> Unit)? = null
    var onKeyRepeatStop: ((KeyDef) -> Unit)? = null
    var hapticEnabled = true

    private val labelView = AppCompatTextView(context)
    private val iconView = AppCompatImageView(context)
    private var useIcon = false

    // साझा Handler — removeCallbacks() केवल उसी इंस्टेंस के पोस्ट किए रननेबल हटाता है
    private val handler = android.os.Handler(android.os.Looper.getMainLooper())
    private var downAt = 0L
    private var longFired = false
    private val longPressMs = 450L
    private var repeatRunnable: Runnable? = null
    private var altRunnable: Runnable? = null

    // शैली-स्मृति (Ctrl-हाइलाइट बहाली के लिए)
    private var normalTextColor = 0xFF202124.toInt()
    private var normalIconTint = 0xFF202124.toInt()

    init {
        isClickable = true
        isLongClickable = true
        val lp = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER
        )
        labelView.layoutParams = lp
        labelView.gravity = Gravity.CENTER
        labelView.includeFontPadding = false
        labelView.setPadding(0, 0, 0, 0)
        labelView.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        addView(labelView)
        iconView.layoutParams = FrameLayout.LayoutParams(dp(24), dp(24), Gravity.CENTER)
        iconView.scaleType = ImageView.ScaleType.FIT_CENTER
        iconView.visibility = GONE
        addView(iconView)
    }

    private fun dp(v: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics
    ).toInt()

    fun bind(def: KeyDef) {
        key = def
        contentDescription = descFor(def)
        if (def.iconRes != null) {
            useIcon = true
            iconView.setImageResource(def.iconRes)
            iconView.visibility = VISIBLE
            labelView.visibility = GONE
        } else {
            useIcon = false
            labelView.text = def.label
            labelView.visibility = VISIBLE
            iconView.visibility = GONE
        }
    }

    private fun descFor(def: KeyDef): String = when (def.action) {
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
        KeyAction.MIC -> "mic"
        KeyAction.HIDE -> "hide"
        KeyAction.ARROW_UP -> "arrow_up"
        KeyAction.ARROW_DOWN -> "arrow_down"
        KeyAction.ARROW_LEFT -> "arrow_left"
        KeyAction.ARROW_RIGHT -> "arrow_right"
        KeyAction.EMOJI_CHAR -> def.label
        null -> def.label
    }

    /** लेबल-पाठ (शिफ्ट-डिस्प्ले in-place अपडेट के लिए) */
    var text: CharSequence
        get() = labelView.text
        set(value) { labelView.text = value }

    fun setKeyTextSizePx(px: Float) {
        labelView.textSize = px / resources.displayMetrics.scaledDensity
    }

    /** लेबल-रंग in-place बदलो (शिफ्ट-हाइलाइट) */
    fun setLabelTextColor(color: Int) {
        labelView.setTextColor(color)
    }

    fun setBold(bold: Boolean) {
        labelView.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    /** थीम-शैली लागू: फिल/दबाव/रंग (प्रोग्राम-निर्मित drawable) */
    fun applyStyle(fill: Int, pressed: Int, radiusPx: Float, textColor: Int, iconTint: Int, borderStrokePx: Int = 0, borderColor: Int = 0) {
        normalTextColor = textColor
        normalIconTint = iconTint
        labelView.setTextColor(textColor)
        iconView.setColorFilter(iconTint)
        background = if (borderStrokePx > 0) {
            KeyboardTheme.spaceBackground(fill, pressed, borderColor, borderStrokePx, radiusPx)
        } else {
            KeyboardTheme.keyBackground(fill, pressed, radiusPx)
        }
    }

    fun applyGhost(pressed: Int, radiusPx: Float, iconTint: Int, textColor: Int) {
        normalTextColor = textColor
        normalIconTint = iconTint
        labelView.setTextColor(textColor)
        iconView.setColorFilter(iconTint)
        background = KeyboardTheme.ghostBackground(pressed, radiusPx)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downAt = System.currentTimeMillis()
                longFired = false
                isPressed = true
                if (hapticEnabled) performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                if (key.action == KeyAction.BACKSPACE) {
                    startRepeat()
                } else if (key.alt != null || key.action == KeyAction.SPACE || key.action == KeyAction.NUMBER_TOGGLE) {
                    // लंबा-दबाव केवल-सार्थक-कीज़-पर अनुसूचित — alt-शून्य-कीज़ (अक्षर/सेवा) पर
                    // CI-लोग-धीमा-टैप = लंबा-दबाव-मिसफायर = टैप-निगलना-जाल (रन 45864657547 का मूल-कारण)
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

    /** लंबा-दबाव → वैकल्पिक अक्षर (अल्ट) — alt-शून्य हुए भी सेवा-हैंडलर तक (स्पेसबार-पैड आदि) */
    fun fireLongPress() {
        longFired = true
        onKeyAlt?.invoke(key)
    }

    private fun startRepeat() {
        val tick = object : Runnable {
            override fun run() {
                if (!longFired) { longFired = true; onKeyRepeatStart?.invoke(key) }
                onKeyRepeatTick?.invoke(key)
                handler.postDelayed(this, 55L)
            }
        }
        repeatRunnable = tick
        handler.postDelayed(tick, longPressMs)
    }

    private fun scheduleAlt() {
        val r = Runnable {
            if (!longFired) fireLongPress()
        }
        altRunnable = r
        handler.postDelayed(r, longPressMs)
    }

    private fun cancelAlt() {
        altRunnable?.let { handler.removeCallbacks(it) }
        altRunnable = null
    }

    private fun stopRepeat() {
        repeatRunnable?.let { handler.removeCallbacks(it) }
        repeatRunnable = null
        onKeyRepeatStop?.invoke(key)
    }

    /** जेस्चर-मोड में स्पर्श-समाप्ति — टैप/लंबा-दबाव दमन, टाइमर-सफ़ाई (बैकस्पेस-रिपीट रक्षा) */
    fun abortTouch() {
        longFired = true
        isPressed = false
        cancelAlt()
        repeatRunnable?.let { handler.removeCallbacks(it) }
        repeatRunnable = null
    }

    /** Ctrl-मोड हाइलाइट (Samsung स्टाइल) */
    fun setCtrlHighlight(on: Boolean, accentColor: Int) {
        if (on) {
            backgroundTintList = android.content.res.ColorStateList.valueOf(accentColor)
            labelView.setTextColor(0xFFFFFFFF.toInt())
            iconView.setColorFilter(0xFFFFFFFF.toInt())
        } else {
            backgroundTintList = null
            labelView.setTextColor(normalTextColor)
            iconView.setColorFilter(normalIconTint)
        }
    }
}
