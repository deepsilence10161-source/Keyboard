package com.astrakey.app.ime.keyboard

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.TextView
import com.astrakey.app.ime.commands.CommandEngine
import com.astrakey.app.ime.suggestion.SuggestionEngine
import com.astrakey.app.R

/**
 * AstraKey का मुख्य कीबोर्ड दृश्य — SwiftKey-स्टाइल:
 * ऊपर टूलबार+सजेशन स्ट्रिप, नीचे 4 पंक्ति कीज़, Samsung-स्टाइल Ctrl-मोड
 */
class KeyboardView(
    context: Context,
    private val icProvider: () -> InputConnection?,
    private val editorInfoProvider: () -> EditorInfo?
) : LinearLayout(context) {

    enum class Panel { SUGGEST, EMOJI, CLIP }

    private val engine = SuggestionEngine()
    private val commands = CommandEngine(context.applicationContext)
    private val prefs = context.getSharedPreferences("astrakey_prefs", Context.MODE_PRIVATE)

    private var shift = ShiftState.OFF
    private var ctrlMode = false
    private var langEnglish = prefs.getBoolean("lang_english", true)
    private var numberRowOn = prefs.getBoolean("number_row", false)
    private var panel = Panel.SUGGEST
    private var lastWordLen = 0

    private val strip = LinearLayout(context)
    private val rowsBox = LinearLayout(context)

    private val accent = 0xFF1A8FE3.toInt()
    private val textDark = 0xFF202124.toInt()

    init {
        orientation = VERTICAL
        setBackgroundColor(0xFFF0F1F5.toInt())
        strip.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp(46))
        strip.orientation = HORIZONTAL
        strip.setPadding(dp(4), dp(2), dp(4), dp(2))
        rowsBox.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        rowsBox.orientation = VERTICAL
        addView(strip)
        addView(rowsBox)
        rebuildAll()
    }

    // ---------- निर्माण ----------

    private fun rebuildAll() {
        rebuildStrip()
        rebuildRows()
    }

    private fun rebuildStrip() {
        strip.removeAllViews()
        when (panel) {
            Panel.SUGGEST -> {
                strip.addView(toolbarBtn("📋", "clipboard", KeyAction.CLIPBOARD))
                strip.addView(toolbarBtn("Ctrl", "Ctrl", KeyAction.CTRL, bold = true))
                strip.addView(toolbarBtn("123", "123", KeyAction.NUMBER_TOGGLE))
                strip.addView(toolbarBtn(if (langEnglish) "अ" else "EN", "language", KeyAction.LANGUAGE))
                strip.addView(toolbarBtn("⚙", "settings", KeyAction.SETTINGS))
                if (lastWordLen == 0) {
                    strip.addView(hintView(if (langEnglish) "सुझाव…" else "सुझाव…"))
                } else {
                    engine.suggest(currentWord()).take(3).forEach { s ->
                        strip.addView(suggestView(s))
                    }
                }
            }
            Panel.EMOJI -> {
                strip.addView(toolbarBtn("←", "back", null))
                strip.addView(emojiScroll())
            }
            Panel.CLIP -> {
                strip.addView(toolbarBtn("←", "back", null))
                val clip = commands.currentClipText()?.toString()
                val v = suggestView(if (clip.isNullOrEmpty()) "क्लिपबोर्ड खाली" else clip.take(60))
                if (!clip.isNullOrEmpty()) v.setOnClickListener { doPaste() }
                strip.addView(v)
            }
        }
    }

    private fun rebuildRows() {
        rowsBox.removeAllViews()
        val rows = if (langEnglish) KeyboardLayouts.englishRows(numberRowOn)
                   else KeyboardLayouts.hindiRows()
        rows.forEach { rowDef -> rowsBox.addView(buildRow(rowDef)) }
        rowsBox.addView(buildBottomRow())
    }

    private fun buildRow(defs: List<KeyDef>): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        row.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp(56))
        row.setPadding(dp(3), dp(3), dp(3), dp(1))
        defs.forEach { def -> row.addView(buildKey(def)) }
        return row
    }

    private fun buildBottomRow(): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        row.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp(56))
        row.setPadding(dp(3), dp(3), dp(3), dp(3))
        row.addView(buildKey(KeyDef("😊", isSpecial = true, action = KeyAction.EMOJI, widthWeight = 1f)))
        row.addView(buildKey(KeyDef(",", alt = ";"), specialStyle = false, weight = 1f))
        row.addView(buildKey(KeyDef(spaceLabel(), output = " ", isSpecial = true, action = KeyAction.SPACE, widthWeight = 5f)))
        row.addView(buildKey(KeyDef(".", alt = ":!"), specialStyle = false, weight = 1f))
        row.addView(buildKey(KeyDef("↵", isSpecial = true, action = KeyAction.ENTER, widthWeight = 1.5f)))
        return row
    }

    private fun spaceLabel(): String = if (langEnglish) "English" else "हिंदी"

    private fun buildKey(def: KeyDef, specialStyle: Boolean = def.isSpecial, weight: Float = def.widthWeight, emojiPanel: Boolean = false): KeyView {
        val kv = KeyView(context)
        val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, weight)
        lp.setMargins(dp(2), 0, dp(2), 0)
        kv.layoutParams = lp
        kv.gravity = Gravity.CENTER
        kv.includeFontPadding = false
        kv.setPadding(0, 0, 0, 0)
        kv.textSize = if (def.action == KeyAction.CTRL) 13f else 19f
        kv.setTextColor(textDark)
        kv.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        kv.setBackgroundResource(if (specialStyle) R.drawable.key_special_bg else R.drawable.key_bg)
        if (specialStyle) kv.elevation = dp(1).toFloat()
        kv.bind(def)
        // शिफ्ट सक्रिय हो तो अक्षर बड़े दिखें
        if (langEnglish && shift != ShiftState.OFF && def.action == null && def.label.length == 1) {
            kv.text = def.label.uppercase()
        }
        // Samsung-स्टाइल Ctrl-मोड हाईलाइट
        if (ctrlMode && langEnglish && def.action == null &&
            def.label.lowercase() in KeyboardLayouts.ctrlTargetKeys) {
            kv.setCtrlHighlight(true, accent, textDark)
        }

        val isEmojiChar = emojiPanel || def.action == KeyAction.EMOJI_CHAR
        kv.onKeyTap = { k ->
            when {
                k.action == KeyAction.EMOJI && !isEmojiChar -> { panel = Panel.EMOJI; rebuildStrip() }
                isEmojiChar -> { commit(k.output); panel = Panel.SUGGEST; rebuildAll() }
                else -> handleKey(k)
            }
        }
        kv.onKeyAlt = { k -> k.alt?.let { commit(it) } }
        kv.onKeyRepeatStart = { _ -> deleteOne() }
        kv.onKeyRepeatTick = { _ -> deleteOne() }
        kv.onKeyRepeatStop = { _ -> updateSuggestions() }
        return kv
    }

    private fun toolbarBtn(label: String, desc: String, action: KeyAction?, bold: Boolean = false): TextView {
        val tv = TextView(context)
        val lp = LinearLayout.LayoutParams(dp(if (bold) 52 else 44), LayoutParams.MATCH_PARENT)
        lp.setMargins(dp(2), 0, dp(2), 0)
        tv.layoutParams = lp
        tv.gravity = Gravity.CENTER
        tv.text = label
        tv.contentDescription = desc
        tv.textSize = 15f
        tv.setTextColor(if (desc == "Ctrl" && ctrlMode) 0xFFFFFFFF.toInt() else textDark)
        tv.typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.SANS_SERIF
        tv.setBackgroundResource(R.drawable.strip_btn_bg)
        if (desc == "Ctrl" && ctrlMode) {
            tv.backgroundTintList = android.content.res.ColorStateList.valueOf(accent)
        }
        tv.setOnClickListener {
            when (action) {
                KeyAction.CLIPBOARD -> { panel = Panel.CLIP; rebuildStrip() }
                KeyAction.CTRL -> { toggleCtrl() }
                KeyAction.NUMBER_TOGGLE -> { numberRowOn = !numberRowOn; prefs.edit().putBoolean("number_row", numberRowOn).apply(); rebuildRows() }
                KeyAction.LANGUAGE -> { langEnglish = !langEnglish; prefs.edit().putBoolean("lang_english", langEnglish).apply(); rebuildAll() }
                KeyAction.SETTINGS -> { openSettings() }
                null -> { panel = Panel.SUGGEST; rebuildAll() }
            }
        }
        return tv
    }

    private fun hintView(text: String): TextView {
        val tv = TextView(context)
        val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 3f)
        lp.setMargins(dp(2), 0, dp(2), 0)
        tv.layoutParams = lp
        tv.gravity = Gravity.CENTER or Gravity.START
        tv.text = text
        tv.textSize = 14f
        tv.setTextColor(0xFF9AA0A6.toInt())
        tv.setPadding(dp(6), 0, dp(6), 0)
        return tv
    }

    private fun suggestView(word: String): TextView {
        val tv = TextView(context)
        val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
        lp.setMargins(dp(2), 0, dp(2), 0)
        tv.layoutParams = lp
        tv.gravity = Gravity.CENTER
        tv.text = word
        tv.contentDescription = word
        tv.textSize = 16f
        tv.setTextColor(0xFF174EA6.toInt())
        tv.setBackgroundResource(R.drawable.strip_btn_bg)
        tv.setOnClickListener { commitSuggestion(word) }
        return tv
    }

    private fun emojiScroll(): HorizontalScrollView {
        val hs = HorizontalScrollView(context)
        val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
        hs.layoutParams = lp
        val box = LinearLayout(context)
        box.orientation = HORIZONTAL
        KeyboardLayouts.emojiSet.forEach { e ->
            val tv = TextView(context)
            val elp = LinearLayout.LayoutParams(dp(38), LayoutParams.MATCH_PARENT)
            tv.layoutParams = elp
            tv.gravity = Gravity.CENTER
            tv.text = e
            tv.textSize = 20f
            tv.contentDescription = e
            tv.setOnClickListener { commit(e); panel = Panel.SUGGEST; rebuildAll() }
            box.addView(tv)
        }
        hs.addView(box)
        return hs
    }

    // ---------- क्रियाएँ ----------

    private fun handleKey(k: KeyDef) {
        val ic = icProvider()
        when (k.action) {
            KeyAction.SHIFT -> { cycleShift(); rebuildRows() }
            KeyAction.BACKSPACE -> { deleteOne(); updateSuggestions() }
            KeyAction.SPACE -> { commit(" "); updateSuggestionsAfterSpace() }
            KeyAction.ENTER -> { enter() }
            KeyAction.EMOJI_CHAR -> { commit(k.output) }
            KeyAction.CTRL, KeyAction.NUMBER_TOGGLE, KeyAction.LANGUAGE, KeyAction.SETTINGS, KeyAction.CLIPBOARD, KeyAction.EMOJI -> {}
            null -> {
                if (ctrlMode && langEnglish && k.label.lowercase() in KeyboardLayouts.ctrlTargetKeys) {
                    runCtrlCommand(k.label.lowercase())
                } else {
                    commitLetter(k)
                }
            }
        }
    }

    private fun commitLetter(k: KeyDef) {
        val ch = when (shift) {
            ShiftState.OFF -> k.output
            else -> k.output.uppercase()
        }
        commit(ch)
        if (shift == ShiftState.ON_ONCE) { shift = ShiftState.OFF; rebuildRows() }
        updateSuggestions()
    }

    private fun toggleCtrl() {
        ctrlMode = !ctrlMode
        rebuildAll()
    }

    /** Samsung-स्टाइल Ctrl+A/C/V/X/Z/Y */
    private fun runCtrlCommand(cmd: String) {
        val ic = icProvider()
        val ok = when (cmd) {
            "a" -> commands.selectAll(ic)
            "c" -> commands.copy(ic) != null
            "x" -> commands.cut(ic) != null
            "v" -> commands.paste(ic)
            "z" -> { toast(context, "Undo आ रहा है (M5)"); false }
            "y" -> { toast(context, "Redo आ रहा है (M5)"); false }
            else -> false
        }
        ctrlMode = false
        rebuildAll()
        updateSuggestions()
    }

    private fun doPaste() {
        commands.paste(icProvider())
        panel = Panel.SUGGEST
        rebuildAll()
    }

    private fun commit(text: String) {
        icProvider()?.commitText(text, 1)
    }

    private fun deleteOne() {
        icProvider()?.deleteSurroundingText(1, 0)
    }

    private fun enter() {
        val info = editorInfoProvider()
        val ic = icProvider()
        if (info != null && (info.imeOptions and (EditorInfo.IME_ACTION_DONE or EditorInfo.IME_ACTION_SEND or EditorInfo.IME_ACTION_SEARCH or EditorInfo.IME_ACTION_GO or EditorInfo.IME_ACTION_NEXT)) != 0) {
            ic?.performEditorAction(info.actionId)
        } else {
            ic?.commitText("\n", 1)
        }
        updateSuggestions()
    }

    private fun cycleShift() {
        shift = when (shift) {
            ShiftState.OFF -> ShiftState.ON_ONCE
            ShiftState.ON_ONCE -> ShiftState.LOCK
            ShiftState.LOCK -> ShiftState.OFF
        }
    }

    private fun openSettings() {
        try {
            val i = android.content.Intent(context, com.astrakey.app.MainActivity::class.java)
            i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(i)
        } catch (_: Exception) {}
    }

    private fun toast(c: Context, msg: String) {
        android.widget.Toast.makeText(c.applicationContext, msg, android.widget.Toast.LENGTH_SHORT).show()
    }

    // ---------- सजेशन ----------

    private fun currentWord(): String {
        val ic = icProvider() ?: return ""
        val before = ic.getTextBeforeCursor(48, 0) ?: return ""
        val sb = StringBuilder()
        for (i in before.length - 1 downTo 0) {
            val c = before[i]
            if (c.isLetterOrDigit() || c == '\'') sb.append(c) else break
        }
        return sb.reverse().toString()
    }

    fun updateSuggestions() {
        val w = currentWord()
        lastWordLen = w.length
        if (panel == Panel.SUGGEST) rebuildStrip()
    }

    fun updateSuggestionsAfterSpace() {
        lastWordLen = 0
        if (panel == Panel.SUGGEST) rebuildStrip()
    }

    private fun commitSuggestion(word: String) {
        val ic = icProvider()
        if (ic != null && lastWordLen > 0) ic.deleteSurroundingText(lastWordLen, 0)
        commit(word + " ")
        lastWordLen = 0
        if (panel == Panel.SUGGEST) rebuildStrip()
    }

    /** IME से: इनपुट शुरू — ऑटो-कैपिटलाइज़ + सजेशन ताज़ा */
    fun onInputStarted() {
        panel = Panel.SUGGEST
        lastWordLen = 0
        val info = editorInfoProvider()
        val ic = icProvider()
        val capOk = info != null && (info.inputType and android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) != 0
        if (capOk && ic != null) {
            val before = ic.getTextBeforeCursor(2, 0) ?: ""
            val emptyField = before.isEmpty()
            val sentenceEnd = before.endsWith(".") || before.endsWith("!") || before.endsWith("?") || before.endsWith("\n")
            if (emptyField || sentenceEnd) { shift = ShiftState.ON_ONCE }
        }
        rebuildAll()
    }

    private fun dp(v: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics
    ).toInt()
}

private fun toast(context: Context, msg: String) {
    android.widget.Toast.makeText(context.applicationContext, msg, android.widget.Toast.LENGTH_SHORT).show()
}
