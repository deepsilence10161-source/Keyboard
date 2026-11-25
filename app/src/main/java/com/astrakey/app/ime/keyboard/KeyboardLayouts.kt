package com.astrakey.app.ime.keyboard

/** लेआउट भंडार — English QWERTY + Hindi देवनागरी (SwiftKey जैसी 4-पंक्ति संरचना) */
object KeyboardLayouts {

    // long-press वैकल्पिक (SwiftKey स्टाइल: ऊपरी पंक्तियों से अंक)
    private val enRow1 = listOf(
        KeyDef("q", alt = "1"), KeyDef("w", alt = "2"), KeyDef("e", alt = "3"),
        KeyDef("r", alt = "4"), KeyDef("t", alt = "5"), KeyDef("y", alt = "6"),
        KeyDef("u", alt = "7"), KeyDef("i", alt = "8"), KeyDef("o", alt = "9"),
        KeyDef("p", alt = "0")
    )
    private val enRow2 = listOf(
        KeyDef("a", alt = "@"), KeyDef("s", alt = "#"), KeyDef("d", alt = "\$"),
        KeyDef("f", alt = "%"), KeyDef("g", alt = "&"), KeyDef("h", alt = "-"),
        KeyDef("j", alt = "+"), KeyDef("k", alt = "("), KeyDef("l", alt = ")")
    )
    private val enRow3 = listOf(
        KeyDef("⇧", isSpecial = true, action = KeyAction.SHIFT, widthWeight = 1.5f),
        KeyDef("z", alt = "*"), KeyDef("x", alt = "!"), KeyDef("c", alt = ";"),
        KeyDef("v", alt = ":"), KeyDef("b", alt = "/"), KeyDef("n", alt = "?"),
        KeyDef("m", alt = "\""),
        KeyDef("⌫", isSpecial = true, action = KeyAction.BACKSPACE, widthWeight = 1.5f)
    )

    fun englishRows(numberRowOn: Boolean): List<List<KeyDef>> {
        val rows = mutableListOf<List<KeyDef>>()
        if (numberRowOn) {
            rows.add(listOf(
                KeyDef("1"), KeyDef("2"), KeyDef("3"), KeyDef("4"), KeyDef("5"),
                KeyDef("6"), KeyDef("7"), KeyDef("8"), KeyDef("9"), KeyDef("0")
            ))
        }
        rows.add(enRow1)
        rows.add(enRow2)
        rows.add(enRow3)
        return rows
    }

    private val hiRow1 = listOf(
        KeyDef("अ"), KeyDef("आ"), KeyDef("इ"), KeyDef("ई"), KeyDef("उ"),
        KeyDef("ऊ"), KeyDef("ए"), KeyDef("ऐ"), KeyDef("ओ"), KeyDef("औ")
    )
    private val hiRow2 = listOf(
        KeyDef("क", alt = "क़"), KeyDef("ख"), KeyDef("ग", alt = "ग़"), KeyDef("घ"),
        KeyDef("च"), KeyDef("छ"), KeyDef("ज", alt = "ज़"), KeyDef("झ"),
        KeyDef("ट"), KeyDef("ठ")
    )
    private val hiRow3 = listOf(
        KeyDef("त"), KeyDef("थ"), KeyDef("द"), KeyDef("ध"), KeyDef("न"),
        KeyDef("प"), KeyDef("फ"), KeyDef("ब"), KeyDef("भ"), KeyDef("म")
    )
    private val hiRow4 = listOf(
        KeyDef("⇧", isSpecial = true, action = KeyAction.SHIFT, widthWeight = 1.5f),
        KeyDef("य"), KeyDef("र"), KeyDef("ल"), KeyDef("व"), KeyDef("श", alt = "ष"),
        KeyDef("स"), KeyDef("ह"), KeyDef("ड़"),
        KeyDef("⌫", isSpecial = true, action = KeyAction.BACKSPACE, widthWeight = 1.5f)
    )

    fun hindiRows(): List<List<KeyDef>> = listOf(hiRow1, hiRow2, hiRow3, hiRow4)

    /** Ctrl-मोड में हाईलाइट होने वाली कीज़ (Samsung स्टाइल) */
    val ctrlTargetKeys = setOf("a", "c", "v", "x", "z", "y")

    /** इमोजी पैनल — लोकप्रिय इमोजी */
    val emojiSet = listOf(
        "😀","😂","🥲","😍","😎","🤔","😅","🙃","😭","😡",
        "👍","👎","🙏","👏","💪","🤝","🔥","❤️","💙","✨",
        "🎉","🎁","☕","🍕","🌙","☀️","🌟","💯","✅","❌"
    )
}
