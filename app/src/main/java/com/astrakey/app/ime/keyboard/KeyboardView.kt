package com.astrakey.app.ime.keyboard

import android.content.Context
import android.content.IntentFilter
import android.graphics.Typeface
import android.os.Build
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.astrakey.app.R
import com.astrakey.app.ime.commands.CommandEngine
import com.astrakey.app.ime.suggestion.SuggestionEngine
import org.json.JSONArray

/**
 * AstraKey मुख्य कीबोर्ड दृश्य — SwiftKey-स्टाइल (उपयोगकर्ता-स्क्रीनशॉट मापन पर आधारित):
 * टूलबार-आइकन पंक्ति → सजेशन-स्ट्रिप → (नंबर-रो) → 3 अक्षर-पंक्तियाँ → निचली पंक्ति → तीर-पंक्ति
 * पंक्ति-ऊँचाई = स्क्रीन-ऊँचाई का अनुपात (SwiftKey जैसा अनुकूलन)। डार्क थीम डिफ़ॉल्ट।
 */
class KeyboardView(
    context: Context,
    private val icProvider: () -> InputConnection?,
    private val editorInfoProvider: () -> EditorInfo?,
    private val hideRequester: () -> Unit
) : LinearLayout(context) {

    enum class Panel { SUGGEST, EMOJI, CLIP, SYMBOLS }

    private data class Metrics(
        val rowH: Int, val stripH: Int, val toolbarH: Int,
        val numRowH: Int, val arrowRowH: Int, val radiusPx: Float
    )

    private val engine = SuggestionEngine()
    private val commands = CommandEngine(context.applicationContext)
    private val prefs = context.getSharedPreferences("astrakey_prefs", Context.MODE_PRIVATE)

    private var shift = ShiftState.OFF
    private var ctrlMode = false
    private var langEnglish = prefs.getBoolean("lang_english", true)
    private var numberRowOn = prefs.getBoolean("number_row", false)
    private var arrowRowOn = prefs.getBoolean("arrow_row", true)
    private var hapticOn = prefs.getBoolean("haptic", true)
    private var gestureOn = prefs.getBoolean("gesture_on", true)
    private var themeName = KeyboardTheme.currentName(prefs)
    private var panel = Panel.SUGGEST
    private var lastWordLen = 0
    private var suggestionSlots: MutableList<TextView> = mutableListOf()
    private var hintSlot: TextView? = null
    private var lastSuggestionSignature: String? = null
    private val letterKeyViews: MutableList<KeyView> = mutableListOf()
    private val allKeyViews: MutableList<KeyView> = mutableListOf()
    private var shiftKeyView: KeyView? = null
    private var gesturePts: MutableList<Pair<Float, Float>> = mutableListOf()
    private var gestureCandidate = false
    private var gestureStartAt = 0L
    private val voiceReceiver = object : android.content.BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: android.content.Intent?) {
            val text = intent?.getStringExtra(com.astrakey.app.VoiceInputActivity.EXTRA_TEXT) ?: return
            if (text.isNotBlank()) { commit(text + " "); updateSuggestions() }
        }
    }

    private val toolbar = LinearLayout(context)
    private val strip = LinearLayout(context)
    private val rowsBox = LinearLayout(context)

    private val pal get() = KeyboardTheme.palette(themeName)

    init {
        orientation = VERTICAL
        setBackgroundColor(pal.bg)
        setupNavBarInsets()
        toolbar.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        toolbar.orientation = HORIZONTAL
        strip.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        strip.orientation = HORIZONTAL
        strip.setPadding(dp(4), dp(2), dp(4), dp(2))
        rowsBox.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        rowsBox.orientation = VERTICAL
        addView(toolbar)
        addView(strip)
        addView(rowsBox)
        ContextCompat.registerReceiver(
            context.applicationContext, voiceReceiver,
            IntentFilter(com.astrakey.app.VoiceInputActivity.ACTION_RESULT),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        rebuildAll()
    }

    override fun onDetachedFromWindow() {
        try { context.applicationContext.unregisterReceiver(voiceReceiver) } catch (_: Exception) {}
        super.onDetachedFromWindow()
    }

    // ---------- मापन (स्क्रीन-अनुपात — SwiftKey जैसा अनुकूलन) ----------

    private fun metrics(): Metrics {
        val h = resources.displayMetrics.heightPixels
        // Gboard-सिद्धांत: बजट = (स्क्रीन − नेविगेशन-बार); SwiftKey-मापन: कुल ≈ 45–48%
        val navEst = navBarHeightPx()
        val avail = (h - navEst).coerceAtLeast(dp(320))
        fun band(frac: Float, minDp: Int, maxDp: Int): Int =
            (avail * frac).toInt().coerceIn(dp(minDp), dp(maxDp))
        val toolbarH = band(0.040f, 34, 50)
        val stripH = band(0.052f, 40, 58)
        val arrowRowH = band(0.034f, 28, 44)
        val budget = (avail * 0.48f).toInt()
        val arrowPart = if (arrowRowOn) arrowRowH + dp(4) else 0
        val rowsN = (if (numberRowOn) 4 else 3) + 1
        val fixed = toolbarH + stripH + arrowPart + dp(16)
        val rowH = ((budget - fixed) / rowsN).coerceIn(dp(44), dp(72))
        return Metrics(
            rowH = rowH,
            stripH = stripH,
            toolbarH = toolbarH,
            numRowH = rowH * 82 / 100,
            arrowRowH = arrowRowH,
            radiusPx = dpf(pal.keyRadiusDp)
        )
    }

    /** नेविगेशन-बार ऊँचाई (संसाधन-आधारित अनुमान) */
    private fun navBarHeightPx(): Int {
        val rid = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (rid > 0) resources.getDimensionPixelSize(rid) else dp(48)
    }

    /**
     * Android 11+ पर IME-विंडो एज-टू-एज — कीबोर्ड स्वयं navigationBars-गद्दी लगाता है
     * (SwiftKey dex-प्रमाण: OnApplyWindowInsetsListener + WindowInsets$Type.navigationBars)
     */
    private fun setupNavBarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(this) { v, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.navigationBars() or WindowInsetsCompat.Type.displayCutout()
            )
            v.setPadding(0, 0, 0, bars.bottom)
            WindowInsetsCompat.CONSUMED
        }
        ViewCompat.requestApplyInsets(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // विफलता-जाल: कुछ ROM insets नहीं भेजते — API 30+ (एज-टू-एज युग) में संसाधन-गद्दी
        postDelayed({
            if (paddingBottom == 0 && Build.VERSION.SDK_INT >= 30) {
                val nav = navBarHeightPx()
                if (nav > 0) setPadding(0, 0, 0, nav)
            }
        }, 800)
    }

    // ---------- निर्माण ----------

    private fun rebuildAll() {
        val m = metrics()
        toolbar.layoutParams.height = m.toolbarH
        strip.layoutParams.height = m.stripH
        rebuildToolbar()
        rebuildStrip()
        rebuildRows(m)
    }

    private fun rebuildToolbar() {
        toolbar.removeAllViews()
        when (panel) {
            Panel.SUGGEST -> {
                toolbar.addView(tbKey(KeyDef("", action = KeyAction.CLIPBOARD, iconRes = R.drawable.ic_clipboard)))
                toolbar.addView(tbKey(KeyDef("Ctrl", action = KeyAction.CTRL)))
                toolbar.addView(tbKey(KeyDef(if (langEnglish) "अ" else "EN", action = KeyAction.LANGUAGE)))
                toolbar.addView(tbKey(KeyDef("", action = KeyAction.EMOJI, iconRes = R.drawable.ic_emoji)))
                toolbar.addView(tbKey(KeyDef("", action = KeyAction.MIC, iconRes = R.drawable.ic_mic)))
                toolbar.addView(tbKey(KeyDef("", action = KeyAction.SETTINGS, iconRes = R.drawable.ic_gear)))
            }
            Panel.EMOJI, Panel.CLIP, Panel.SYMBOLS -> {
                val back = tbKey(KeyDef("", iconRes = R.drawable.ic_back))
                back.contentDescription = "back"
                back.onKeyTap = { panel = Panel.SUGGEST; rebuildAll() }
                toolbar.addView(back)
                val title = TextView(context)
                val tlp = LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
                tlp.setMargins(dp(4), 0, dp(4), 0)
                title.layoutParams = tlp
                title.gravity = Gravity.CENTER_VERTICAL
                title.text = when (panel) {
                    Panel.EMOJI -> "इमोजी"
                    Panel.SYMBOLS -> "चिह्न व अंक"
                    else -> "क्लिपबोर्ड"
                }
                title.textSize = 16f
                title.setTextColor(pal.keyText)
                title.typeface = Typeface.DEFAULT_BOLD
                toolbar.addView(title)
                val trash = tbKey(KeyDef("", iconRes = R.drawable.ic_trash))
                trash.contentDescription = "clear"
                trash.onKeyTap = { commands.clearClipHistory(); rebuildAll() }
                if (panel == Panel.CLIP) toolbar.addView(trash)
            }
        }
    }

    private fun tbKey(def: KeyDef): KeyView {
        val kv = KeyView(context)
        val lp = LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
        lp.setMargins(dp(2), dp(2), dp(2), dp(2))
        kv.layoutParams = lp
        kv.applyGhost(pal.pressedFill, dpf(pal.keyRadiusDp), pal.toolbarIcon, pal.keyText)
        kv.setKeyTextSizePx(dpf(15f))
        kv.bind(def)
        kv.hapticEnabled = hapticOn
        allKeyViews.add(kv)
        if (def.action == KeyAction.CTRL && ctrlMode) kv.setCtrlHighlight(true, pal.accent)
        kv.onKeyTap = { k -> handleKey(k) }
        return kv
    }

    private fun rebuildStrip() {
        strip.removeAllViews()
        if (panel != Panel.SUGGEST) { strip.visibility = GONE; return }
        strip.visibility = VISIBLE
        strip.setBackgroundColor(pal.stripBg)
        // बाएँ ∨-घेरा (छिपाओ) — SwiftKey स्टाइल
        val chev = KeyView(context)
        val clp = LayoutParams(dp(38), LayoutParams.MATCH_PARENT)
        clp.setMargins(dp(2), 0, dp(2), 0)
        chev.layoutParams = clp
        chev.applyGhost(pal.pressedFill, dpf(pal.keyRadiusDp), pal.secondaryText, pal.secondaryText)
        chev.bind(KeyDef("", action = KeyAction.HIDE, iconRes = R.drawable.ic_chevron_down))
        chev.hapticEnabled = hapticOn
        chev.onKeyTap = { hideRequester() }
        strip.addView(chev)
        // स्थिर स्लॉट — टेक्स्ट in-place बदलता है, व्यू दोबारा नहीं बनते (StaleObject बचाव)
        suggestionSlots = mutableListOf()
        val hint = hintView()
        hintSlot = hint
        strip.addView(hint)
        repeat(3) {
            val tv = suggestView()
            tv.visibility = GONE
            suggestionSlots.add(tv)
            strip.addView(tv)
        }
        updateSuggestions()
    }

    private fun rebuildRows(m: Metrics) {
        rowsBox.removeAllViews()
        letterKeyViews.clear()
        allKeyViews.clear()
        shiftKeyView = null
        when (panel) {
            Panel.SUGGEST -> {
                // हिंग्लिश मोड: लेआउट क्वर्टी ही रहता है — सुझाव देवनागरी मिलते हैं
                val rows = KeyboardLayouts.englishRows(numberRowOn)
                rows.forEachIndexed { i, def ->
                    val h = if (numberRowOn && i == 0) m.numRowH else m.rowH
                    rowsBox.addView(buildRow(def, h))
                }
                rowsBox.addView(buildBottomRow(m.rowH))
                if (arrowRowOn) rowsBox.addView(buildArrowRow(m.arrowRowH))
            }
            Panel.EMOJI -> rowsBox.addView(buildEmojiPanel(m.rowH))
            Panel.CLIP -> rowsBox.addView(buildClipPanel())
            Panel.SYMBOLS -> rowsBox.addView(buildSymbolsPanel())
        }
    }

    private fun buildRow(defs: List<KeyDef>, rowH: Int): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, rowH)
        lp.setMargins(0, dp(2), 0, dp(2))
        row.layoutParams = lp
        row.setPadding(dp(4), 0, dp(4), 0)
        defs.forEach { def -> row.addView(buildKey(def, rowH)) }
        return row
    }

    private fun buildBottomRow(rowH: Int): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, rowH)
        lp.setMargins(0, dp(2), 0, dp(2))
        row.layoutParams = lp
        row.setPadding(dp(4), 0, dp(4), 0)
        row.addView(buildKey(KeyDef("123", alt = " ", action = KeyAction.NUMBER_TOGGLE, widthWeight = 1.2f), rowH))
        row.addView(buildKey(KeyDef("", action = KeyAction.EMOJI, iconRes = R.drawable.ic_emoji), rowH))
        row.addView(buildKey(KeyDef(",", alt = ";"), rowH))
        row.addView(buildKey(KeyDef(spaceLabel(), output = " ", isSpecial = true, action = KeyAction.SPACE, widthWeight = 4.6f), rowH))
        row.addView(buildKey(KeyDef(".", alt = ":!"), rowH))
        row.addView(buildKey(KeyDef("", isSpecial = true, action = KeyAction.ENTER, iconRes = R.drawable.ic_enter, widthWeight = 1.5f), rowH))
        return row
    }

    /** तीर-पंक्ति — SwiftKey-स्टाइल ^ v < > (कर्सर-नियंत्रण) */
    private fun buildArrowRow(rowH: Int): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, rowH)
        lp.setMargins(0, dp(2), 0, dp(2))
        row.layoutParams = lp
        row.setPadding(dp(4), 0, dp(4), 0)
        listOf(
            KeyDef("", action = KeyAction.ARROW_UP, iconRes = R.drawable.ic_arrow_up),
            KeyDef("", action = KeyAction.ARROW_DOWN, iconRes = R.drawable.ic_arrow_down),
            KeyDef("", action = KeyAction.ARROW_LEFT, iconRes = R.drawable.ic_arrow_left),
            KeyDef("", action = KeyAction.ARROW_RIGHT, iconRes = R.drawable.ic_arrow_right)
        ).forEach { def -> row.addView(arrowKey(def, rowH)) }
        return row
    }

    private fun arrowKey(def: KeyDef, rowH: Int): KeyView {
        val kv = KeyView(context)
        val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
        lp.setMargins(dp(2), 0, dp(2), 0)
        kv.layoutParams = lp
        kv.applyGhost(pal.pressedFill, dpf(pal.keyRadiusDp), pal.secondaryText, pal.secondaryText)
        kv.bind(def)
        kv.hapticEnabled = hapticOn
        allKeyViews.add(kv)
        kv.onKeyTap = { k -> handleKey(k) }
        return kv
    }

    private fun buildKey(def: KeyDef, rowH: Int): KeyView {
        val kv = KeyView(context)
        val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, def.widthWeight)
        lp.setMargins(dp(2), 0, dp(2), 0)
        kv.layoutParams = lp
        val p = pal
        val radius = dpf(p.keyRadiusDp)
        val isSpace = def.action == KeyAction.SPACE
        val special = def.isSpecial && !isSpace
        when {
            isSpace -> kv.applyStyle(p.spaceFill, p.pressedFill, radius, p.secondaryText, p.secondaryText, dp(1), p.spaceBorder)
            def.action == KeyAction.ENTER -> kv.applyGhost(p.pressedFill, radius, p.keyText, p.keyText)
            special -> kv.applyStyle(p.specialFill, p.pressedFill, radius, p.keyText, p.keyText)
            else -> kv.applyStyle(p.keyFill, p.pressedFill, radius, p.keyText, p.keyText)
        }
        when {
            def.action == KeyAction.SPACE -> kv.setKeyTextSizePx(rowH * 0.26f)
            def.iconRes == null && def.action == null -> kv.setKeyTextSizePx(rowH * 0.36f)
            def.iconRes == null -> kv.setKeyTextSizePx(rowH * 0.30f)
        }
        kv.bind(def)
        kv.hapticEnabled = hapticOn
        allKeyViews.add(kv)
        // शिफ्ट-डिस्प्ले in-place अपडेट के लिए पंजीकरण
        if (def.action == KeyAction.SHIFT) shiftKeyView = kv
        if (def.action == null && def.label.length == 1) letterKeyViews.add(kv)
        // शिफ्ट सक्रिय हो तो अक्षर बड़े दिखें
        if (shift != ShiftState.OFF && def.action == null && def.label.length == 1) {
            kv.text = def.label.uppercase()
        }
        // Samsung-स्टाइल Ctrl-मोड हाइलाइट
        if (ctrlMode && def.action == null &&
            def.label.lowercase() in KeyboardLayouts.ctrlTargetKeys) {
            kv.setCtrlHighlight(true, p.accent)
        }
        kv.onKeyTap = { k -> handleKey(k) }
        kv.onKeyAlt = { k ->
            if (k.action == KeyAction.NUMBER_TOGGLE) { panel = Panel.SYMBOLS; rebuildAll() }
            else k.alt?.let { commit(it) }
        }
        kv.onKeyRepeatStart = { _ -> deleteOne() }
        kv.onKeyRepeatTick = { _ -> deleteOne() }
        kv.onKeyRepeatStop = { _ -> updateSuggestions() }
        return kv
    }

    private fun spaceLabel(): String = if (langEnglish) "English" else "हिंदी"

    // ---------- पैनल (इमोजी / क्लिपबोर्ड) ----------

    private fun buildEmojiPanel(rowH: Int): ScrollView {
        val hs = ScrollView(context)
        hs.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        hs.setPadding(dp(6), dp(4), dp(6), dp(4))
        val box = LinearLayout(context)
        box.orientation = VERTICAL
        val recents = recentEmojis()
        if (recents.isNotEmpty()) {
            box.addView(sectionLabel("हाल के"))
            box.addView(emojiRow(recents.take(10), (rowH * 0.8f).toInt()))
        }
        box.addView(sectionLabel("सभी"))
        KeyboardLayouts.emojiSet.chunked(5).forEach { chunk ->
            box.addView(emojiRow(chunk, (rowH * 0.95f).toInt()))
        }
        hs.addView(box)
        return hs
    }

    private fun sectionLabel(t: String): TextView {
        val tv = TextView(context)
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(dp(4), dp(4), dp(4), dp(2))
        tv.layoutParams = lp
        tv.text = t
        tv.textSize = 13f
        tv.setTextColor(pal.secondaryText)
        return tv
    }

    private fun emojiRow(emojis: List<String>, cellH: Int): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        row.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        emojis.forEach { e ->
            val tv = TextView(context)
            val lp = LinearLayout.LayoutParams(0, cellH, 1f)
            lp.setMargins(dp(2), 0, dp(2), 0)
            tv.layoutParams = lp
            tv.gravity = Gravity.CENTER
            tv.text = e
            tv.textSize = 22f
            tv.contentDescription = e
            tv.background = KeyboardTheme.ghostBackground(pal.pressedFill, dpf(pal.keyRadiusDp))
            tv.setOnClickListener { commit(e); addRecentEmoji(e) }
            row.addView(tv)
        }
        return row
    }

    /** चिह्न-पैनल — अंक/गणित/मुद्रा/कोष्ठक (टैप = लिखो, पैनल खुला रहे) */
    private fun buildSymbolsPanel(): ScrollView {
        val hs = ScrollView(context)
        hs.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        hs.setPadding(dp(4), dp(4), dp(4), dp(6))
        val box = LinearLayout(context)
        box.orientation = VERTICAL
        val pages = listOf(
            listOf('1','2','3','4','5','6','7','8','9','0'),
            listOf('@','#','$','%','&','*','-','+','(',')'),
            listOf('!','"',\',':',';',',','?','~','^','|'),
            listOf('[',']','{','}','<','>','=','/','\\'),
            listOf('₹','€','£','¥','•','©','®','°','§','±')
        )
        pages.forEach { chars -> box.addView(symbolRow(chars)) }
        hs.addView(box)
        return hs
    }

    private fun symbolRow(chars: List<Char>): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        row.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, dp(52))
        lp(row, 2)
        chars.forEach { c ->
            val tv = TextView(context)
            val lp2 = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
            lp2.setMargins(dp(2), 0, dp(2), 0)
            tv.layoutParams = lp2
            tv.gravity = Gravity.CENTER
            tv.text = c.toString()
            tv.textSize = 19f
            tv.setTextColor(pal.keyText)
            tv.contentDescription = if (c == '\\') "backslash" else c.toString()
            tv.background = KeyboardTheme.keyBackground(pal.keyFill, pal.pressedFill, dpf(pal.keyRadiusDp))
            tv.setOnClickListener { commit(c.toString()); updateSuggestions() }
            row.addView(tv)
        }
        return row
    }

    private fun lp(row: LinearLayout, pad: Int) {
        row.setPadding(dp(pad), 0, dp(pad), 0)
    }

    private fun buildClipPanel(): ScrollView {
        val hs = ScrollView(context)
        hs.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        hs.setPadding(dp(8), dp(6), dp(8), dp(6))
        val box = LinearLayout(context)
        box.orientation = VERTICAL
        val pinned = commands.pinnedClips()
        val history = commands.clipHistory()
        pinned.forEach { entry -> box.addView(clipRow(entry, pinned = true)) }
        if (pinned.isEmpty() && history.isEmpty()) {
            val tv = TextView(context)
            tv.text = "क्लिपबोर्ड खाली — कुछ कॉपी करें या Ctrl दबाकर C चुनें"
            tv.textSize = 14f
            tv.setTextColor(pal.secondaryText)
            tv.setPadding(dp(4), dp(12), dp(4), dp(12))
            box.addView(tv)
        }
        history.forEach { entry -> box.addView(clipRow(entry, pinned = false)) }
        if (pinned.isNotEmpty()) {
            val lbl = TextView(context)
            lbl.text = "हाल के:"
            lbl.textSize = 12f
            lbl.setTextColor(pal.secondaryText)
            lbl.setPadding(dp(4), dp(6), dp(4), dp(2))
            box.addView(lbl)
        }
        if (pinned.isNotEmpty() && history.isEmpty()) {
            val tv = TextView(context)
            tv.text = "(कोई हाल की प्रविष्टि नहीं)"
            tv.textSize = 13f
            tv.setTextColor(pal.secondaryText)
            tv.setPadding(dp(4), dp(8), dp(4), dp(8))
            box.addView(tv)
        }
        hs.addView(box)
        return hs
    }

    /** एक क्लिप-पंक्ति — टैप = पेस्ट; लंबा-दबाव = पिन-टॉगल */
    private fun clipRow(entry: String, pinned: Boolean): LinearLayout {
        val row = LinearLayout(context)
        row.orientation = HORIZONTAL
        val lp = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        lp.setMargins(0, dp(3), 0, dp(3))
        row.layoutParams = lp
        row.setPadding(dp(10), dp(10), dp(10), dp(10))
        row.background = KeyboardTheme.keyBackground(pal.panelCard, pal.pressedFill, dpf(pal.keyRadiusDp))
        row.contentDescription = if (pinned) "clip_pinned" else "clip_entry"
        row.isClickable = true
        row.isLongClickable = true
        val tv = TextView(context)
        tv.layoutParams = LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        tv.text = if (pinned) "📌 $entry" else entry
        tv.textSize = 15f
        tv.setTextColor(pal.keyText)
        tv.maxLines = 2
        row.addView(tv)
        row.setOnClickListener { icProvider()?.commitText(entry, 1) }
        row.setOnLongClickListener {
            commands.togglePin(entry)
            rebuildAll()
            true
        }
        return row
    }

    private fun addRecentEmoji(e: String) {
        val arr = try { JSONArray(prefs.getString("recent_emoji", "[]") ?: "[]") } catch (_: Exception) { JSONArray() }
        val list = (0 until arr.length()).map { arr.optString(it) }.toMutableList()
        list.removeAll { it == e }
        list.add(0, e)
        val out = JSONArray()
        list.take(16).forEach { out.put(it) }
        prefs.edit().putString("recent_emoji", out.toString()).apply()
    }

    private fun recentEmojis(): List<String> = try {
        val a = JSONArray(prefs.getString("recent_emoji", "[]") ?: "[]")
        (0 until a.length()).mapNotNull { i -> a.optString(i).takeIf { it.isNotEmpty() } }
    } catch (_: Exception) { emptyList() }

    // ---------- क्रियाएँ ----------

    private fun handleKey(k: KeyDef) {
        when (k.action) {
            KeyAction.SHIFT -> { cycleShift(); refreshShiftDisplay() }
            KeyAction.BACKSPACE -> { deleteOne(); updateSuggestions() }
            KeyAction.SPACE -> { commit(" "); updateSuggestionsAfterSpace() }
            KeyAction.ENTER -> { enter() }
            KeyAction.EMOJI_CHAR -> { commit(k.output) }
            KeyAction.CTRL -> toggleCtrl()
            KeyAction.NUMBER_TOGGLE -> { numberRowOn = !numberRowOn; prefs.edit().putBoolean("number_row", numberRowOn).apply(); rebuildRows(metrics()) }
            KeyAction.LANGUAGE -> { langEnglish = !langEnglish; prefs.edit().putBoolean("lang_english", langEnglish).apply(); rebuildAll() }
            KeyAction.SETTINGS -> openSettings()
            KeyAction.CLIPBOARD -> openClip()
            KeyAction.EMOJI -> { panel = Panel.EMOJI; rebuildAll() }
            KeyAction.MIC -> startVoice()
            KeyAction.HIDE -> hideRequester()
            KeyAction.ARROW_UP -> commands.moveCursorVertical(icProvider(), true)
            KeyAction.ARROW_DOWN -> commands.moveCursorVertical(icProvider(), false)
            KeyAction.ARROW_LEFT -> commands.moveCursor(icProvider(), -1)
            KeyAction.ARROW_RIGHT -> commands.moveCursor(icProvider(), 1)
            null -> {
                if (ctrlMode && k.label.lowercase() in KeyboardLayouts.ctrlTargetKeys) {
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
        if (shift == ShiftState.ON_ONCE) { shift = ShiftState.OFF; refreshShiftDisplay() }
        updateSuggestions()
    }

    private fun toggleCtrl() {
        ctrlMode = !ctrlMode
        rebuildAll()
    }

    private fun openClip() {
        commands.captureSystemClipboard()
        panel = Panel.CLIP
        rebuildAll()
    }

    /** Samsung-स्टाइल Ctrl+A/Z/X/C/V/Y — सब वास्तविक */
    private fun runCtrlCommand(cmd: String) {
        val ic = icProvider()
        when (cmd) {
            "a" -> commands.selectAll(ic)
            "c" -> commands.copy(ic)
            "x" -> {
                val before = snap(ic)
                commands.cut(ic)
                val after = snap(ic)
                if (before != null && after != null) commands.pushUndo(before, after, false)
            }
            "v" -> {
                val before = snap(ic)
                commands.paste(ic)
                val after = snap(ic)
                if (before != null && after != null) commands.pushUndo(before, after, false)
            }
            "z" -> commands.undo(ic)
            "y" -> commands.redo(ic)
        }
        ctrlMode = false
        rebuildAll()
        updateSuggestions()
    }

    private fun commit(text: String) {
        val ic = icProvider() ?: return
        val before = snap(ic)
        ic.commitText(text, 1)
        val after = snap(ic)
        if (before != null && after != null) {
            commands.pushUndo(before, after, text.length == 1 && text[0].isLetterOrDigit())
        }
    }

    /** वर्तमान फील्ड-स्नैपशॉट (अनडू-स्टैक हेतु) */
    private fun snap(ic: InputConnection?): com.astrakey.app.ime.commands.UndoManager.Snap? {
        ic ?: return null
        val et = ic.getExtractedText(android.view.inputmethod.ExtractedTextRequest(), 0) ?: return null
        return com.astrakey.app.ime.commands.UndoManager.Snap(
            et.text?.toString() ?: "", et.selectionStart, et.selectionEnd
        )
    }

    private fun deleteOne() {
        val ic = icProvider() ?: return
        val before = snap(ic)
        ic.deleteSurroundingText(1, 0)
        val after = snap(ic)
        if (before != null && after != null) commands.pushUndo(before, after, false)
    }

    private fun enter() {
        val info = editorInfoProvider()
        val ic = icProvider()
        if (info != null && (info.imeOptions and (EditorInfo.IME_ACTION_DONE or EditorInfo.IME_ACTION_SEND or EditorInfo.IME_ACTION_SEARCH or EditorInfo.IME_ACTION_GO or EditorInfo.IME_ACTION_NEXT)) != 0) {
            ic?.performEditorAction(info.actionId)
        } else {
            commit("\n")
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

    private fun startVoice() {
        try {
            val i = android.content.Intent(context, com.astrakey.app.VoiceInputActivity::class.java)
            i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_NO_HISTORY)
            context.startActivity(i)
        } catch (_: Exception) {
            android.widget.Toast.makeText(context.applicationContext, "वाक्-पहचान आरंभ विफल", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    // ---------- जेस्चर-टाइपिंग (स्वाइप → शब्द) ----------

    /** पूरे कीबोर्ड पर स्पर्श-निरीक्षण — स्वाइप-पथ संग्रहित, टैप बाल-कीज़ तक जाते हैं */
    private fun logD(msg: String) { android.util.Log.d("AstraKeyUI", msg) }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!gestureOn || panel != Panel.SUGGEST || !langEnglish) {
            logD("gSkip on=${gestureOn} panel=$panel en=$langEnglish")
            return super.dispatchTouchEvent(ev)
        }
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                gesturePts.clear()
                gesturePts.add(ev.x to ev.y)
                gestureCandidate = false
                gestureStartAt = System.currentTimeMillis()
                logD("gDown ${ev.x},${ev.y}")
            }
            MotionEvent.ACTION_MOVE -> {
                if (!gestureCandidate) {
                    val last = gesturePts.last()
                    val d = kotlin.math.hypot((ev.x - last.first).toDouble(), (ev.y - last.second).toDouble())
                    if (d > 4) gesturePts.add(ev.x to ev.y)
                    if (gesturePts.size >= 2 && pathLength(gesturePts) > dp(70)) {
                        gestureCandidate = true
                        allKeyViews.forEach { it.abortTouch() } // बैकस्पेस-रिपीट/अल्ट-टाइमर रक्षा
                        logD("gCandidate pts=${gesturePts.size} len=${pathLength(gesturePts)}")
                    }
                }
                if (gestureCandidate) return true // बाल-कीज़ तक घटना नहीं जाए
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (gestureCandidate) {
                    val dur = System.currentTimeMillis() - gestureStartAt
                    // UP-स्थिति पथ में — तीव्र-स्वाइप में मूव सह-एकीकृत होकर पिछड़ जाते हैं
                    gesturePts.add(ev.x to ev.y)
                    val pts = gesturePts.toList()
                    gesturePts.clear()
                    gestureCandidate = false
                    if (dur >= 100 && pts.size >= 6) {
                        val word = decodeGesture(pts)
                        logD("gUp dur=$dur pts=${pts.size} decoded=$word")
                        if (word != null) { commit(word); updateSuggestions() }
                    } else {
                        logD("gUpShort dur=$dur pts=${pts.size}")
                    }
                    return true // टैप-दमन (की-टैप दोहराव न हो)
                }
                gesturePts.clear()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun pathLength(pts: List<Pair<Float, Float>>): Float {
        var l = 0f
        for (i in 1 until pts.size) {
            val a = pts[i - 1]; val b = pts[i]
            l += kotlin.math.hypot((b.first - a.first).toDouble(), (b.second - a.second).toDouble()).toFloat()
        }
        return l
    }

    /** स्वाइप-पथ को शब्दकोश-पथों से मिलाओ (रीसैंपल + औसत-दूरी) */
    private fun decodeGesture(pts: List<Pair<Float, Float>>): String? {
        val centers = letterCenters() ?: return null
        val swipe = resample(pts, 16)
        val first = pts.first(); val last = pts.last()
        var best: String? = null
        var bestScore = Float.MAX_VALUE
        for ((word, freq) in engine.wordsWithFreq) {
            if (word.length < 3 || word.length > 10) continue
            val w0 = centers[word[0]] ?: continue
            val w1 = centers[word[word.length - 1]] ?: continue
            if (kotlin.math.hypot((first.first - w0.first).toDouble(), (first.second - w0.second).toDouble()) > dp(46)) continue
            if (kotlin.math.hypot((last.first - w1.first).toDouble(), (last.second - w1.second).toDouble()) > dp(46)) continue
            val poly = mutableListOf<Pair<Float, Float>>()
            word.forEach { c -> centers[c]?.let { poly.add(it) } }
            if (poly.size < word.length) continue
            val rp = resample(poly, 16)
            var sum = 0f
            for (i in 0 until 16) {
                sum += kotlin.math.hypot(
                    (swipe[i].first - rp[i].first).toDouble(),
                    (swipe[i].second - rp[i].second).toDouble()
                ).toFloat()
            }
            val score = sum / 16f - freq * 0.02f // आवृत्ति-सहायता
            if (score < bestScore) { bestScore = score; best = word }
        }
        logD("gDecode centers=${centers.size} best=$best score=$bestScore")
        return if (bestScore < dp(26)) best else null
    }

    /** अक्षर-की केंद्र (इस-व्यू निर्देशांक) */
    private fun letterCenters(): Map<Char, Pair<Float, Float>>? {
        val root = IntArray(2); getLocationInWindow(root)
        val map = mutableMapOf<Char, Pair<Float, Float>>()
        for (kv in allKeyViews) {
            if (kv.key.action != null || kv.key.label.length != 1) continue
            if (!kv.isShown) continue
            val loc = IntArray(2); kv.getLocationInWindow(loc)
            val k = kv.key.label.lowercase()[0]
            if (!map.containsKey(k)) map[k] = ((loc[0] + kv.width / 2f) to (loc[1] + kv.height / 2f - root[1]))
        }
        return if (map.size >= 26) map else null
    }

    private fun resample(pts: List<Pair<Float, Float>>, n: Int): List<Pair<Float, Float>> {
        if (pts.size < 2) return pts
        val lens = FloatArray(pts.size)
        var total = 0f
        for (i in 1 until pts.size) {
            val a = pts[i - 1]; val b = pts[i]
            total += kotlin.math.hypot((b.first - a.first).toDouble(), (b.second - a.second).toDouble()).toFloat()
            lens[i] = total
        }
        if (total <= 0f) return List(n) { pts[0] }
        val out = mutableListOf<Pair<Float, Float>>()
        var j = 0
        for (i in 0 until n) {
            val t = total * i / (n - 1)
            while (j < pts.size - 2 && lens[j + 1] < t) j++
            val seg = (lens[j + 1] - lens[j]).takeIf { it > 0f } ?: 1f
            val f = (t - lens[j]) / seg
            val a = pts[j]; val b = pts[j + 1]
            out.add((a.first + (b.first - a.first) * f) to (a.second + (b.second - a.second) * f))
        }
        return out
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
        if (panel != Panel.SUGGEST) return
        val w = currentWord()
        lastWordLen = w.length
        val sug = if (lastWordLen > 0) engine.suggest(w, hindiFirst = !langEnglish).take(3) else emptyList()
        val sig = sug.joinToString("|")
        if (sig == lastSuggestionSignature) return
        lastSuggestionSignature = sig
        hintSlot?.visibility = if (sug.isEmpty()) VISIBLE else GONE
        suggestionSlots.forEachIndexed { i, tv ->
            if (i < sug.size) {
                tv.text = sug[i]
                tv.contentDescription = sug[i]
                tv.visibility = VISIBLE
                // SwiftKey-स्टाइल: मध्य सुझाव बोल्ड-सफ़ेद, पार्श्व ग्रे
                val center = i == 1 && sug.size == 3
                tv.setTextColor(if (center) pal.suggMain else pal.suggSide)
                tv.typeface = if (center) Typeface.DEFAULT_BOLD else Typeface.SANS_SERIF
            } else {
                tv.visibility = GONE
            }
        }
    }

    fun updateSuggestionsAfterSpace() {
        lastWordLen = 0
        updateSuggestions()
    }

    private fun commitSuggestion(word: String) {
        val ic = icProvider()
        if (ic != null && lastWordLen > 0) ic.deleteSurroundingText(lastWordLen, 0)
        commit(word + " ")
        lastWordLen = 0
        updateSuggestions()
    }

    /** IME से: इनपुट शुरू — थीम-ताज़ा + ऑटो-कैपिटलाइज़ + सजेशन ताज़ा */
    fun onInputStarted() {
        val newName = KeyboardTheme.currentName(prefs)
        if (newName != themeName) { themeName = newName; setBackgroundColor(pal.bg) }
        numberRowOn = prefs.getBoolean("number_row", false)
        langEnglish = prefs.getBoolean("lang_english", true)
        arrowRowOn = prefs.getBoolean("arrow_row", true)
        hapticOn = prefs.getBoolean("haptic", true)
        gestureOn = prefs.getBoolean("gesture_on", true)
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

    /** पूरा रीबिल्ड किए बिना शिफ्ट-स्थिति के अनुसार अक्षर बदलो (tree-स्थिरता) */
    private fun refreshShiftDisplay() {
        letterKeyViews.forEach { kv ->
            kv.text = if (shift == ShiftState.OFF) kv.key.label else kv.key.label.uppercase()
        }
        shiftKeyView?.setLabelTextColor(if (shift == ShiftState.OFF) pal.keyText else pal.accent)
    }

    private fun hintView(): TextView {
        val tv = TextView(context)
        val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 3f)
        lp.setMargins(dp(2), 0, dp(2), 0)
        tv.layoutParams = lp
        tv.gravity = Gravity.CENTER or Gravity.START
        tv.text = "सुझाव…"
        tv.textSize = 14f
        tv.setTextColor(pal.secondaryText)
        tv.setPadding(dp(6), 0, dp(6), 0)
        return tv
    }

    private fun suggestView(): TextView {
        val tv = TextView(context)
        val lp = LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1f)
        lp.setMargins(dp(2), 0, dp(2), 0)
        tv.layoutParams = lp
        tv.gravity = Gravity.CENTER
        tv.text = ""
        tv.textSize = 16f
        tv.setTextColor(pal.suggSide)
        tv.setOnClickListener { commitSuggestion(tv.text.toString()) }
        return tv
    }

    private fun dp(v: Int): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, v.toFloat(), resources.displayMetrics
    ).toInt()

    private fun dpf(v: Float): Float = v * resources.displayMetrics.density
}
