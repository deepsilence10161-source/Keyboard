package com.astrakey.app.ime.suggestion

/**
 * सजेशन इंजन — अंग्रेज़ी शब्द-सूची (आवृत्ति-क्रम) + हिंग्लिश→हिंदी ट्रांसलिटरेशन
 * (SwiftKey स्टाइल: टाइप करते ही EN + HI सुझाव साथ)
 */
class SuggestionEngine {

    private val english: Map<String, Int> = buildEnglishDict()
    private val translit: Map<String, String> = buildTranslitMap()

    private fun buildEnglishDict(): Map<String, Int> {
        val words = listOf(
            "the" to 100,"be" to 95,"to" to 94,"of" to 93,"and" to 92,"a" to 91,"in" to 90,
            "that" to 88,"have" to 85,"it" to 84,"for" to 83,"not" to 82,"on" to 81,"with" to 80,
            "he" to 78,"as" to 77,"you" to 76,"do" to 75,"at" to 74,"this" to 73,"but" to 72,
            "his" to 70,"by" to 69,"from" to 68,"they" to 67,"we" to 66,"say" to 65,"her" to 64,
            "she" to 63,"or" to 62,"an" to 61,"will" to 60,"my" to 59,"one" to 58,"all" to 57,
            "would" to 56,"there" to 55,"their" to 54,"what" to 53,"so" to 52,"up" to 51,
            "out" to 50,"if" to 49,"about" to 48,"who" to 47,"get" to 46,"which" to 45,
            "go" to 44,"me" to 43,"when" to 42,"make" to 41,"can" to 40,"like" to 39,
            "time" to 38,"no" to 37,"just" to 36,"him" to 35,"know" to 34,"take" to 33,
            "people" to 32,"into" to 31,"year" to 30,"your" to 29,"good" to 28,"some" to 27,
            "could" to 26,"them" to 25,"see" to 24,"other" to 23,"than" to 22,"then" to 21,
            "now" to 20,"look" to 19,"only" to 18,"come" to 17,"its" to 16,"over" to 15,
            "think" to 14,"also" to 13,"back" to 12,"after" to 11,"use" to 10,"two" to 9,
            "how" to 8,"our" to 7,"work" to 6,"first" to 5,"well" to 5,"way" to 5,"even" to 4,
            "new" to 4,"want" to 4,"because" to 4,"any" to 4,"these" to 4,"give" to 4,
            "day" to 4,"most" to 4,"us" to 3,"hello" to 90,"thanks" to 85,"thank" to 84,
            "please" to 80,"sorry" to 75,"ok" to 90,"okay" to 70,"yes" to 85,"love" to 78,
            "happy" to 70,"morning" to 65,"night" to 60,"today" to 72,"tomorrow" to 65,
            "yesterday" to 50,"friend" to 60,"family" to 62,"home" to 65,"house" to 55,
            "water" to 50,"food" to 55,"phone" to 60,"call" to 58,"message" to 62,
            "meet" to 55,"meeting" to 58,"office" to 52,"school" to 55,"college" to 50,
            "teacher" to 45,"student" to 50,"study" to 52,"exam" to 48,"book" to 55,
            "read" to 55,"write" to 58,"word" to 50,"letter" to 45,"email" to 60,
            "money" to 58,"price" to 45,"shop" to 50,"market" to 50,"city" to 55,
            "village" to 45,"road" to 45,"car" to 55,"bus" to 50,"train" to 50,
            "travel" to 48,"visit" to 50,"week" to 55,"month" to 55,"year" to 58,
            "life" to 65,"world" to 60,"country" to 58,"india" to 80,"bharat" to 60,
            "namaste" to 70,"brother" to 50,"sister" to 50,"mother" to 55,"father" to 55,
            "name" to 65,"question" to 55,"answer" to 55,"problem" to 60,"solution" to 50,
            "help" to 70,"need" to 70,"want" to 68,"know" to 72,"understand" to 55,
            "remember" to 50,"forget" to 48,"start" to 60,"stop" to 60,"open" to 55,
            "close" to 55,"fast" to 50,"slow" to 45,"big" to 55,"small" to 55,"new" to 62,
            "old" to 55,"easy" to 60,"hard" to 55,"best" to 65,"better" to 65,"good" to 75
        )
        return words.toMap()
    }

    private fun buildTranslitMap(): Map<String, String> = mapOf(
        "namaste" to "नमस्ते","namaskar" to "नमस्कार","kaise" to "कैसे","ho" to "हो",
        "aap" to "आप","kya" to "क्या","hai" to "है","hain" to "हैं","main" to "मैं",
        "tum" to "तुम","pyar" to "प्यार","pyaar" to "प्यार","dhanyavad" to "धन्यवाद",
        "shukriya" to "शुक्रिया","accha" to "अच्छा","achha" to "अच्छा","thik" to "ठीक",
        "kaha" to "कहाँ","kahan" to "कहाँ","kab" to "कब","kaun" to "कौन","kyon" to "क्यों",
        "kyun" to "क्यों","mein" to "में","me" to "में","aur" to "और","nahi" to "नहीं",
        "nahin" to "नहीं","haan" to "हाँ","bhai" to "भाई","dost" to "दोस्त",
        "paani" to "पानी","pani" to "पानी","khana" to "खाना","ghar" to "घर",
        "kaam" to "काम","dil" to "दिल","duniya" to "दुनिया","raat" to "रात",
        "din" to "दिन","subah" to "सुबह","kal" to "कल","aaj" to "आज","karna" to "करना",
        "krna" to "करना","hoga" to "होगा","hogi" to "होगी","raha" to "रहा","rahi" to "रही",
        "liye" to "लिए","wala" to "वाला","bahut" to "बहुत","zyada" to "ज़्यादा",
        "kam" to "कम","bada" to "बड़ा","chota" to "छोटा","naya" to "नया",
        "purana" to "पुराना","bharat" to "भारत","india" to "इंडिया","khush" to "खुश",
        "sapna" to "सपना","zindagi" to "ज़िंदगी","dosti" to "दोस्ती","maa" to "माँ",
        "papa" to "पापा","beta" to "बेटा","beti" to "बेटी","shaadi" to "शादी",
        "padhai" to "पढ़ाई","school" to "स्कूल","paisa" to "पैसा","bazar" to "बाज़ार"
    )

    /** वर्तमान उपसर्ग (prefix) के लिए सुझाव: EN शब्द + हिंग्लिश→हिंदी */
    fun suggest(prefix: String, max: Int = 3): List<String> {
        val p = prefix.trim().lowercase()
        if (p.isEmpty()) return emptyList()
        val out = LinkedHashSet<String>()

        // 1) हिंग्लिश ट्रांसलिट (सटीक या उपसर्ग)
        val hiMatches = translit.entries
            .filter { it.key.startsWith(p) }
            .sortedByDescending { it.key.length }
            .take(max)
        hiMatches.forEach { out.add(it.value) }

        // 2) अंग्रेज़ी (सटीक पहले, फिर आवृत्ति-क्रम उपसर्ग)
        english[p]?.let { out.add(p) }
        val enMatches = english.keys.filter { it.startsWith(p) && it != p }
            .sortedByDescending { english[it] ?: 0 }
            .take(max)
        enMatches.forEach { out.add(it) }

        return out.take(max)
    }

    /** ट्रांसलिटरेशन (पूरे शब्द का) — मौजूद नहीं तो null */
    fun transliterate(word: String): String? = translit[word.trim().lowercase()]
}
