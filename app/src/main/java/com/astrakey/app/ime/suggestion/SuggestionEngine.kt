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
            "think" to 14,"type" to 45,"typing" to 30,"qwerty" to 60,"tree" to 45,"also" to 13,"back" to 12,"after" to 11,"use" to 10,"two" to 9,
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
            "old" to 55,"easy" to 60,"hard" to 55,"best" to 65,"better" to 65,"good" to 75,"water" to 55,"where" to 60,"which" to 58,"while" to 55,"white" to 50,"whole" to 45,"window" to 55,"winter" to 45,"wish" to 45,"woman" to 55,"women" to 52,"wonder" to 45,"work" to 62,"worker" to 42,"world" to 60,"write" to 58,"writer" to 42,"wrong" to 45,"yellow" to 40,"yesterday" to 50,"young" to 48,"your" to 70,"yourself" to 40,"baby" to 45,"back" to 60,"bad" to 50,"bag" to 42,"ball" to 45,"bank" to 50,"bath" to 40,"because" to 55,"become" to 50,"bed" to 45,"before" to 58,"begin" to 48,"behind" to 42,"believe" to 45,"bell" to 35,"below" to 40,"beside" to 42,"best" to 60,"bird" to 40,"black" to 48,"block" to 42,"blood" to 40,"blue" to 46,"body" to 50,"book" to 58,"born" to 42,"both" to 55,"bottle" to 38,"box" to 42,"boy" to 48,"bread" to 38,"break" to 45,"bring" to 48,"brother" to 50,"build" to 45,"burn" to 38,"bus" to 45,"business" to 50,"buy" to 50,"call" to 58,"calm" to 38,"camera" to 48,"camp" to 35,"capital" to 42,"care" to 52,"carry" to 45,"case" to 45,"cat" to 42,"catch" to 42,"cause" to 45,"cell" to 42,"center" to 45,"central" to 40,"century" to 38,"certain" to 45,"chair" to 42,"chance" to 45,"change" to 52,"character" to 40,"charge" to 42,"chat" to 48,"check" to 50,"child" to 48,"choice" to 45,"choose" to 45,"church" to 38,"circle" to 38,"class" to 46,"clean" to 44,"clear" to 46,"climate" to 36,"clock" to 40,"close" to 50,"cloth" to 35,"cloud" to 42,"colour" to 44,"come" to 58,"common" to 45,"company" to 50,"complete" to 44,"computer" to 55,"concern" to 38,"condition" to 42,"continue" to 48,"control" to 45,"cook" to 40,"cool" to 44,"copy" to 45,"corn" to 32,"corner" to 40,"correct" to 42,"cost" to 45,"country" to 50,"course" to 45,"cover" to 44,"create" to 46,"crop" to 34,"cross" to 40,"cry" to 38,"cup" to 40,"cut" to 46,"dance" to 42,"danger" to 38,"dark" to 44,"data" to 55,"date" to 46,"daughter" to 42,"day" to 58,"dead" to 36,"deal" to 44,"dear" to 42,"death" to 40,"decide" to 45,"deep" to 42,"degree" to 40,"delhi" to 62,"demand" to 42,"department" to 40,"describe" to 40,"desk" to 40,"detail" to 42,"develop" to 45,"die" to 40,"different" to 46,"difficult" to 45,"dinner" to 42,"direct" to 42,"direction" to 40,"distance" to 40,"divide" to 38,"doctor" to 50,"dog" to 42,"dollar" to 40,"door" to 46,"double" to 42,"down" to 52,"draw" to 42,"dream" to 44,"dress" to 40,"drink" to 42,"drive" to 46,"drop" to 40,"dry" to 38,"during" to 45,"each" to 48,"ear" to 36,"early" to 46,"east" to 38,"easy" to 50,"eat" to 48,"economy" to 40,"edge" to 38,"education" to 48,"effect" to 44,"effort" to 42,"egg" to 36,"eight" to 40,"either" to 45,"election" to 42,"else" to 44,"end" to 50,"english" to 55,"enjoy" to 48,"enough" to 46,"enter" to 44,"entire" to 42,"equal" to 40,"error" to 42,"even" to 48,"evening" to 46,"event" to 44,"ever" to 46,"every" to 50,"everyone" to 44,"everything" to 44,"example" to 45,"except" to 40,"excited" to 44,"exercise" to 40,"expect" to 44,"experience" to 42,"explain" to 44,"express" to 40,"eye" to 44,"face" to 46,"fact" to 45,"fail" to 42,"fall" to 44,"false" to 38,"famous" to 44,"far" to 45,"farm" to 40,"fast" to 46,"father" to 52,"fear" to 40,"feel" to 48,"feeling" to 44,"few" to 46,"field" to 42,"fight" to 42,"figure" to 40,"fill" to 40,"film" to 44,"final" to 44,"find" to 50,"fine" to 46,"finger" to 38,"finish" to 44,"fire" to 44,"first" to 55,"fish" to 40,"five" to 42,"floor" to 40,"flower" to 40,"follow" to 45,"food" to 52,"foot" to 40,"for" to 60,"force" to 42,"forest" to 38,"forget" to 46,"form" to 45,"fortune" to 36,"forward" to 42,"four" to 42,"free" to 48,"fresh" to 44,"friend" to 55,"from" to 58,"front" to 42,"fruit" to 40,"full" to 46,"fun" to 46,"future" to 46,"game" to 48,"garden" to 42,"gas" to 36,"general" to 42,"generation" to 38,"gentle" to 36,"get" to 55,"girl" to 46,"give" to 52,"glass" to 40,"go" to 55,"goal" to 44,"god" to 48,"gold" to 42,"gone" to 40,"ground" to 42,"group" to 45,"grow" to 44,"guard" to 38,"guess" to 42,"gun" to 36,"hair" to 40,"half" to 44,"hand" to 50,"happen" to 45,"happy" to 50,"hard" to 46,"harm" to 38,"head" to 46,"health" to 46,"hear" to 44,"heart" to 44,"heat" to 40,"heavy" to 40,"height" to 38,"help" to 55,"here" to 50,"herself" to 36,"hide" to 40,"high" to 48,"hill" to 38,"history" to 44,"hit" to 42,"hold" to 45,"hole" to 36,"holiday" to 40,"honey" to 38,"hope" to 48,"horse" to 40,"hospital" to 44,"hot" to 44,"hotel" to 42,"hour" to 45,"house" to 50,"how" to 52,"however" to 44,"human" to 45,"hundred" to 40,"hungry" to 40,"hurry" to 36,"idea" to 48,"identify" to 38,"if" to 50,"important" to 48,"impossible" to 38,"improve" to 44,"in" to 58,"inch" to 34,"include" to 42,"increase" to 42,"indeed" to 38,"industry" to 42,"information" to 46,"inside" to 44,"instead" to 42,"interest" to 44,"internet" to 52,"interview" to 42,"into" to 50,"introduce" to 40,"invest" to 38,"invite" to 40,"island" to 36,"issue" to 44,"item" to 42,"itself" to 38,"job" to 48,"join" to 42,"joke" to 38,"journey" to 40,"joy" to 40,"judge" to 40,"juice" to 40,"jump" to 40,"just" to 52,"keep" to 48,"key" to 46,"kick" to 36,"kill" to 38,"kind" to 45,"king" to 42,"kitchen" to 40,"knee" to 34,"knife" to 36,"knock" to 36,"know" to 55,"knowledge" to 44,"labour" to 38,"lack" to 40,"lady" to 40,"lake" to 38,"land" to 44,"language" to 46,"large" to 45,"last" to 48,"late" to 46,"laugh" to 42,"launch" to 40,"law" to 44,"lead" to 44,"leader" to 44,"learn" to 48,"least" to 44,"leave" to 46,"left" to 46,"leg" to 40,"legal" to 40,"lend" to 36,"less" to 44,"lesson" to 40,"let" to 46,"letter" to 45,"level" to 45,"library" to 42,"lie" to 40,"life" to 55,"lift" to 38,"light" to 46,"like" to 55,"line" to 46,"lion" to 36,"list" to 44,"listen" to 46,"little" to 46,"live" to 50,"local" to 44,"long" to 48,"look" to 52,"lose" to 44,"lot" to 46,"loud" to 38,"love" to 55,"low" to 44,"luck" to 40,"lunch" to 42,"machine" to 44,"main" to 45,"major" to 44,"make" to 52,"man" to 50,"manage" to 44,"many" to 48,"map" to 40,"mark" to 42,"market" to 50,"marry" to 40,"match" to 42,"material" to 40,"matter" to 45,"may" to 48,"maybe" to 44,"meal" to 38,"mean" to 45,"measure" to 40,"meat" to 38,"medical" to 44,"meet" to 50,"memory" to 42,"mention" to 40,"message" to 50,"metal" to 38,"method" to 42,"middle" to 42,"might" to 45,"mile" to 38,"mind" to 46,"mine" to 40,"minute" to 44,"miss" to 42,"mistake" to 42,"mix" to 40,"model" to 42,"modern" to 42,"moment" to 45,"money" to 52,"monkey" to 34,"month" to 45,"moon" to 40,"more" to 50,"morning" to 50,"most" to 48,"mother" to 52,"motor" to 38,"mountain" to 40,"mouse" to 40,"mouth" to 38,"move" to 45,"movie" to 46,"much" to 48,"music" to 50,"must" to 48,"myself" to 40,"nail" to 32,"name" to 52,"nation" to 44,"natural" to 42,"nature" to 45,"near" to 45,"nearly" to 40,"necessary" to 42,"neck" to 34,"need" to 52,"needle" to 32,"neighbour" to 38,"never" to 48,"news" to 48,"next" to 46,"nice" to 46,"night" to 48,"nine" to 38,"nobody" to 36,"noise" to 38,"none" to 40,"north" to 40,"nose" to 34,"not" to 52,"note" to 44,"nothing" to 45,"now" to 50,"number" to 46,"nurse" to 38,"object" to 40,"observe" to 38,"obvious" to 38,"occasion" to 36,"occur" to 40,"ocean" to 40,"o clock" to 30,"offer" to 44,"office" to 50,"often" to 45,"oil" to 38,"old" to 46,"on" to 55,"once" to 45,"one" to 52,"only" to 50,"open" to 50,"opinion" to 42,"opposite" to 40,"order" to 45,"other" to 50,"our" to 48,"out" to 50,"outside" to 42,"over" to 48,"own" to 45,"page" to 42,"pain" to 40,"paint" to 40,"pair" to 38,"paper" to 44,"paragraph" to 36,"pardon" to 34,"parent" to 44,"park" to 42,"part" to 46,"particular" to 38,"party" to 46,"pass" to 44,"past" to 42,"path" to 40,"patient" to 40,"pay" to 46,"peace" to 42,"pen" to 42,"people" to 55,"per" to 40,"perhaps" to 42,"period" to 42,"permit" to 38,"person" to 46,"phone" to 52,"photo" to 46,"pick" to 42,"picture" to 44,"piece" to 42,"place" to 48,"plan" to 46,"plane" to 40,"plant" to 42,"play" to 48,"player" to 42,"playground" to 34,"please" to 50,"pleasure" to 38,"point" to 45,"police" to 44,"policy" to 42,"polite" to 38,"pool" to 36,"poor" to 42,"pop" to 34,"position" to 42,"possible" to 45,"post" to 44,"potato" to 38,"power" to 45,"practice" to 42,"prepare" to 42,"present" to 42,"president" to 40,"press" to 42,"pressure" to 40,"pretend" to 36,"pretty" to 40,"prevent" to 40,"price" to 45,"pride" to 38,"primary" to 40,"print" to 40,"prison" to 36,"private" to 40,"prize" to 38,"probably" to 42,"problem" to 50,"produce" to 40,"program" to 44,"promise" to 42,"protect" to 40,"proud" to 40,"prove" to 40,"provide" to 44,"public" to 44,"pull" to 40,"punish" to 36,"pupil" to 34,"pure" to 36,"purple" to 36,"purpose" to 40,"push" to 42,"put" to 48,"quality" to 40,"quarter" to 38,"queen" to 38,"question" to 48,"quick" to 42,"quiet" to 40,"quite" to 42,"race" to 38,"radio" to 44,"rail" to 36,"railway" to 38,"rain" to 42,"raise" to 42,"range" to 38,"rate" to 42,"rather" to 42,"reach" to 42,"read" to 52,"ready" to 46,"real" to 46,"reason" to 44,"receive" to 44,"recent" to 42,"recognize" to 38,"record" to 42,"red" to 44,"reduce" to 40,"refer" to 40,"refuse" to 40,"regular" to 40,"relation" to 40,"remain" to 40,"remember" to 48,"remind" to 42,"remove" to 42,"repeat" to 42,"reply" to 42,"report" to 44,"rest" to 44,"result" to 45,"return" to 44,"rich" to 42,"ride" to 40,"right" to 50,"ring" to 40,"rise" to 40,"river" to 40,"road" to 46,"rock" to 38,"role" to 40,"roll" to 38,"room" to 44,"rose" to 36,"round" to 42,"rule" to 44,"run" to 48,"rush" to 38,"sad" to 40,"safe" to 44,"sail" to 34,"salt" to 38,"same" to 48,"sand" to 38,"satisfy" to 38,"save" to 46,"say" to 52,"scale" to 36,"school" to 52,"science" to 44,"scientist" to 40,"score" to 40,"sea" to 40,"search" to 45,"season" to 42,"seat" to 40,"second" to 45,"secret" to 42,"see" to 52,"seem" to 45,"sell" to 44,"send" to 46,"sense" to 42,"sentence" to 40,"separate" to 40,"serious" to 42,"serve" to 42,"service" to 46,"set" to 46,"settle" to 40,"seven" to 40,"several" to 44,"shall" to 45,"shape" to 40,"share" to 46,"sharp" to 38,"she" to 50,"sheep" to 34,"sheet" to 36,"shelf" to 34,"shine" to 36,"ship" to 40,"shirt" to 38,"shoe" to 38,"shop" to 46,"short" to 44,"should" to 50,"shout" to 38,"show" to 48,"shut" to 40,"sick" to 40,"side" to 46,"sign" to 44,"silence" to 38,"silk" to 34,"silver" to 36,"similar" to 40,"simple" to 44,"since" to 45,"sing" to 42,"single" to 42,"sir" to 44,"sister" to 48,"sit" to 46,"site" to 42,"situation" to 42,"size" to 44,"skin" to 36,"sky" to 40,"sleep" to 44,"slightly" to 36,"slow" to 42,"small" to 48,"smell" to 36,"smile" to 44,"smoke" to 38,"smooth" to 38,"snow" to 38,"so" to 52,"soccer" to 36,"social" to 44,"society" to 42,"sock" to 32,"soft" to 40,"soldier" to 38,"solution" to 44,"solve" to 42,"some" to 50,"someone" to 44,"something" to 46,"sometimes" to 44,"son" to 44,"song" to 42,"soon" to 44,"sorry" to 48,"sound" to 44,"south" to 40,"space" to 44,"speak" to 46,"special" to 45,"speech" to 40,"speed" to 42,"spend" to 44,"sport" to 44,"spread" to 38,"spring" to 40,"square" to 40,"stage" to 40,"stair" to 34,"stamp" to 34,"stand" to 44,"star" to 40,"start" to 50,"state" to 45,"station" to 42,"stay" to 45,"steal" to 36,"steam" to 36,"step" to 42,"stick" to 38,"still" to 46,"stone" to 40,"stop" to 48,"store" to 42,"storm" to 38,"story" to 46,"street" to 44,"strong" to 45,"student" to 50,"study" to 50,"stuff" to 40,"stupid" to 36,"succeed" to 38,"such" to 48,"sudden" to 38,"suffer" to 40,"sugar" to 40,"suggest" to 42,"suit" to 38,"summer" to 42,"sun" to 44,"Sunday" to 42,"support" to 44,"suppose" to 40,"sure" to 48,"surprise" to 42,"sweet" to 40,"swim" to 38,"system" to 46,"table" to 42,"tail" to 34,"take" to 52,"talk" to 48,"tall" to 40,"taste" to 40,"tax" to 38,"tea" to 44,"teach" to 46,"teacher" to 48,"team" to 46,"tear" to 36,"telephone" to 42,"television" to 40,"tell" to 48,"temperature" to 38,"ten" to 42,"term" to 42,"terrible" to 40,"test" to 46,"text" to 46,"than" to 46,"thank" to 50,"that" to 55,"the" to 58,"theatre" to 36,"their" to 50,"them" to 48,"then" to 48,"there" to 52,"these" to 48,"thin" to 38,"thing" to 48,"think" to 50,"third" to 40,"thirsty" to 34,"this" to 55,"those" to 46,"though" to 42,"thought" to 44,"thousand" to 40,"thread" to 34,"three" to 42,"throat" to 34,"through" to 45,"throw" to 42,"Thursday" to 38,"ticket" to 40,"tidy" to 32,"tie" to 36,"tight" to 38,"time" to 52,"timetable" to 32,"tired" to 42,"title" to 42,"today" to 50,"toe" to 32,"together" to 44,"tomato" to 36,"tomorrow" to 48,"tonight" to 42,"too" to 48,"tool" to 40,"tooth" to 36,"top" to 44,"total" to 42,"touch" to 44,"tour" to 40,"toward" to 40,"towel" to 34,"tower" to 38,"town" to 42,"toy" to 38,"trade" to 40,"traffic" to 42,"train" to 46,"translate" to 36,"transport" to 38,"travel" to 46,"treat" to 40,"tree" to 42,"trouble" to 40,"trousers" to 34,"truck" to 38,"true" to 45,"trust" to 42,"truth" to 42,"try" to 48,"Tuesday" to 38,"turn" to 45,"TV" to 44,"twelve" to 36,"twenty" to 40,"twice" to 38,"two" to 46,"type" to 46,"uncle" to 40,"under" to 45,"understand" to 48,"unit" to 38,"university" to 42,"unless" to 38,"until" to 42,"up" to 48,"upon" to 40,"upper" to 38,"upset" to 38,"us" to 46,"use" to 50,"useful" to 42,"usual" to 40,"usually" to 42,"vacation" to 40,"valley" to 36,"valuable" to 40,"value" to 44,"various" to 40,"vegetable" to 40,"very" to 50,"victory" to 40,"video" to 46,"view" to 44,"village" to 46,"visit" to 48,"voice" to 44,"wait" to 46,"wake" to 40,"walk" to 46,"wall" to 42,"want" to 52,"war" to 42,"warm" to 40,"wash" to 42,"waste" to 38,"watch" to 46,"water" to 52,"wave" to 38,"way" to 48,"weak" to 38,"wear" to 42,"weather" to 42,"wedding" to 40,"week" to 46,"weight" to 40,"welcome" to 44,"well" to 48,"west" to 38,"wet" to 38,"what" to 52,"wheel" to 38,"when" to 50,"whenever" to 38,"whether" to 42)
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

    /**
     * वर्तमान उपसर्ग (prefix) के लिए सुझाव।
     * hindiFirst=true (हिंग्लिश मोड): देवनागरी-ट्रांसलिट पहले, अंग्रेज़ी बाद।
     * hindiFirst=false (अंग्रेज़ी मोड): अंग्रेज़ी पहले, देवनागरी बाद।
     */
    fun suggest(prefix: String, max: Int = 3, hindiFirst: Boolean = false): List<String> {
        val p = prefix.trim().lowercase()
        if (p.isEmpty()) return emptyList()
        val out = LinkedHashSet<String>()

        val hiMatches = translit.entries
            .filter { it.key.startsWith(p) }
            .sortedWith(
                compareByDescending<Map.Entry<String, String>> { it.key == p }
                    .thenBy { it.key.length }
            )
            .take(max)
        val exactEnglish = english[p]?.let { listOf(p) } ?: emptyList()
        val enMatches = english.keys.filter { it.startsWith(p) && it != p }
            .sortedByDescending { english[it] ?: 0 }
            .take(max)

        if (hindiFirst) {
            hiMatches.forEach { out.add(it.value) }
            exactEnglish.forEach { out.add(it) }
            enMatches.forEach { out.add(it) }
        } else {
            exactEnglish.forEach { out.add(it) }
            enMatches.forEach { out.add(it) }
            hiMatches.forEach { out.add(it.value) }
        }

        return out.take(max)
    }

    /** ट्रांसलिटरेशन (पूरे शब्द का) — मौजूद नहीं तो null */
    fun transliterate(word: String): String? = translit[word.trim().lowercase()]

    /** जेस्चर-विघटन हेतु शब्दकोश (शब्द → आवृत्ति) */
    val wordsWithFreq: Map<String, Int> get() = english
}
