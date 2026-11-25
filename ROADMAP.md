# 🚀 AstraKey — Advanced Smart Keyboard: गहरी शोध-आधारित विस्तृत रोडमैप

> **मिशन:** Gboard और SwiftKey की गहरी स्कैनिंग से सीखकर — एक **Advance, Smart, Attractive और High-Easy-to-Use** कीबोर्ड बनाना, जिसमें **Samsung जैसे Ctrl-कमांड फीचर्स** भी हों।
> **तकनीक:** Kotlin (Android Native) | **तारीख:** 25 नवंबर 2026 | **मालिक:** deepsilence10161-source

---

## 1️⃣ गहरी शोध के ठोस नतीजे (Evidence-Based Research)

### 1.1 APK गहरी स्कैनिंग की तुलना (औज़ार: androguard — मैनिफेस्ट + 2.89 लाख स्ट्रिंग्स माइनिंग)

| पहलू | **Gboard 18.6.3** | **SwiftKey 9.13.20.4** | हमारा फैसला (AstraKey) |
|---|---|---|---|
| पैकेज | `com.google.android.inputmethod.latin` | `com.touchtype.swiftkey` | `com.astrakey.app` |
| minSdk | **32** (Android 12L+) | **26** (Android 8.0) | **26** — 99% डिवाइस कवरेज ✅ |
| targetSdk | 37 (DEV) | 36 (Android 16) | **36** (Android 16) ✅ |
| dex/क्लास | 4 dex / 22,508 क्लास | 2 dex / 21,971 क्लास | छोटा-फीता: लक्ष्य < 300 क्लास MVP |
| IME सर्विस | `LatinIME` + SpellChecker | `com.touchtype.KeyboardService` | `AstraKeyIME` (एक केंद्रीय सर्विस) |
| भाषाएँ | 100 लोकेल | स्प्लिट-आधारित | MVP: English + हिंदी + हिंग्लिश |
| अनुमतियाँ | 20 (READ_CONTACTS आदि) | 22 (CAMERA, RECORD_AUDIO…) | **न्यूनतम 2** — VIBRATE, (बाद में RECORD_AUDIO वैकल्पिक) |

### 1.2 स्ट्रिंग-माइनिंग से पक्के हुए फीचर्स (सबूत: `apk_analysis/feature_strings.json` + `research_evidence.md`)

**Gboard (286,102 स्ट्रिंग्स स्कैन):** clipboard ×131, cursor ×191, trackpad ×40, text-editing ×10, glide ×45, toolbar ×70, one-handed ×11, number-row ×4, autocorrect ×16, translate ×157, voice/dictat ×448, theme ×276, haptic ×4, popup ×15, floating ×53, select-all ×4

**SwiftKey (3,099 स्ट्रिंग्स स्कैन):** clipboard ×98, toolbar ×63, theme ×108, flow(glide) ×14, prediction ×27, undo ×4, number-row ×5, resize ×20, one-handed ×1, double-space ×(auto-space) ×1, cursor ×5, shortcut ×18

👉 **निष्कर्ष:** दोनों के मूल-हड्डी (core) फीचर = क्लिपबोर्ड, कर्सर-कंट्रोल, टूलबार, थीम, प्रेडिक्शन/ऑटोकरेक्ट, नंबर-रो, वन-हैंडेड, हैप्टिक/साउंड। यही हमारा आधार-ग्रिड बनेगा।

### 1.3 Samsung का Ctrl-कमांड फीचर (वेब शोध — पुष्टि की गई)
Samsung Galaxy keyboard में **Ctrl बटन दबाकर रखने पर A, Z, X, C, V, Y कीज़ हाईलाइट** हो जाती हैं और टैप करने पर काम होते हैं:
- **Ctrl+A** = Select All | **Ctrl+Z** = Undo | **Ctrl+X** = Cut | **Ctrl+C** = Copy | **Ctrl+V** = Paste | **Ctrl+Y** = Redo
- साथ में टूलबार का **"चार-तीर (four-arrow)" टेक्स्ट-एडिटिंग पैनल** — तीरों से कर्सर हिलाना + सिलेक्ट/कॉपी/पेस्ट बटन
- टूलबार में **7 तक शॉर्टकट**, ड्रैग-एंड-ड्रॉप से कस्टमाइज़

### 1.4 CI/एमुलेटर शोध (E2E लाइव टेस्टिंग की नींव)
- GitHub Actions `ubuntu-latest` पर **Android SDK पहले से इंस्टॉल** है
- KVM हार्डवेयर एक्सेलेरेशन उपलब्ध → `reactivecircus/android-emulator-runner` से **लाइव एमुलेटर** बूट होता है
- पाइपलाइन: build APK → emulator बूट (api-level 30, x86_64, `-no-window -gpu swiftshader_indirect`) → ऐप इंस्टॉल → IME enable → असली टाइपिंग E2E टेस्ट → रिपोर्ट + APK artifact
- **हर बग-फिक्स का नियम:** गहरी शोध → फिक्स → CI लाइव एमुलेटर टेस्ट हरी झंडी ✅ → तभी मर्ज

---

## 2️⃣ AstraKey की परिभाषा (Vision)

> **"पूरे देश का अपना कीबोर्ड — स्मार्ट जैसा Gboard, भरोसेमंद जैसा SwiftKey, कमांड जैसा Samsung, और सुंदर जैसा कुछ नया।"**

तीन स्तंभ:
1. **Advance** — हिंग्लिश ट्रांसलिटरेशन, स्मार्ट सजेशन, Ctrl-कमांड, क्लिपबोर्ड-इतिहास
2. **Attractive** — Material You डायनामिक थीम्स, सुंदर एनिमेशन, डार्क/लाइट
3. **Easy** — बड़ी कीज़, साफ लेआउट, भारतीय उपयोगकर्ता-केंद्रित डिज़ाइन

---

## 3️⃣ फीचर सेट (चरण-वार)

### 🟢 Phase A — MVP (काम करता है, पक्का)
- QWERTY लेआउट + **नंबर-रो टॉगल**, बैकस्पेस/शिफ्ट/एंटर/स्पेसबार
- Shift, CapsLock, लंबा दबाव (long-press) से वैकल्पिक अक्षर/चिन्ह
- हिंदी देवनागरी लेआउट + **हिंग्लिश ट्रांसलिटरेशन** (जैसे "namaste" → "नमस्ते" सजेशन)
- शब्द-सजेशन (auto-suggest strip) + ऑटोकैपिटलाइज़ + डबल-स्पेस = फुल-स्टॉप
- IME सर्विस + सेटअप गाइड स्क्रीन (एनेबल + चूज-इनपुट मेथड)
- हैप्टिक फीडबैक + की-प्रेस साउंड (टॉगल)

### 🟡 Phase B — Smart (समझदार)
- **Samsung-स्टाइल Ctrl-कमांड बार:** Ctrl बटन hold → A/Z/X/C/V/Y हाईलाइट → क्रिया ✨
- **टेक्स्ट-एडिटिंग पैनल:** चार-तीर कर्सर मूवमेंट + select/copy/paste/cut
- **कर्सर-कंट्रोल:** स्पेसबार पर उँगली सरकाकर कर्सर हिलाना (trackpad जैसा)
- **क्लिपबोर्ड मैनेजर:** पिन + इतिहास, एक-टैप पेस्ट
- ऑटोकरेक्ट इंजन (लोकल डिक्शनरी + संभावना-आधारित)
- Emoji पैनल + रिसेंट इमोजी

### 🔵 Phase C — Attractive (सुंदर)
- **Material You डायनामिक थीम्स** (वॉलपेपर रंग), 6+ बिल्ट-इन थीम, डार्क मोड
- की-प्रेस एनिमेशन + popup विज़ुअल
- **वन-हैंडेड मोड** (बाएँ/दाएँ खिसका), कीबोर्ड हाइट रिसाइज़
- टूलबार: क्लिपबोर्ड/थीम/सेटिंग्स/भाषा टॉगल (टैप-एंड-स्विच)

### 🔴 Phase D — Power (आगे की लहर)
- ग्लाइड टाइपिंग (swipe), वॉयस टाइपिंग, ट्रांसलेशन बार
- फ्लोटिंग कीबोर्ड, बहु-भाषा समानांतर सजेशन
- Personal dictionary + सीखने की क्षमता (privacy-first: लोकल स्टोरेज)

---

## 4️⃣ तकनीकी आर्किटेक्चर (Kotlin)

```
app (AstraKey)
├── ime/            InputMethodService — AstraKeyIME (मुख्य IME)
│   ├── keyboard/   कस्टम View-आधारित कीबोर्ड कैनवास (Gboard जैसा परफ़ॉर्मेंस)
│   ├── commands/   Ctrl-कमांड इंजन (A/Z/X/C/V/Y → InputConnection क्रियाएँ)
│   ├── suggestion/ सजेशन स्ट्रिप + ऑटोकरेक्ट इंजन
│   └── toolbar/    टूलबार + क्लिपबोर्ड + थीम-चयन
├── data/           उपयोगकर्ता डिक्शनरी (DataStore), क्लिपबोर्ड इतिहास
├── settings/       सेटअप गाइड + प्राथमिकताएँ (Jetpack Compose)
└── test/           यूनिट + इंस्ट्रूमेंटेड + E2E (Espresso/Uiautomator)
```

- **IME सर्विस का दिल:** `InputMethodService` + `InputConnection` (सारा टेक्स्ट काम इसी से)
- **Ctrl-कमांड इंप्लीमेंटेशन:** `InputConnection.getSelectedText()`, `commitText()`, `deleteSurroundingText()`, `performContextMenuAction(SelectAll/Copy/Paste/Cut)` — Android का आधिकारिक तरीका
- **UI:** क्लासिक View-आधारित कैनवास (Gboard-स्टाइल परफ़ॉर्मेंस) + सेटिंग्स UI में Compose
- **टर्गेट:** minSdk 26 / targetSdk 36 | AGP 8.x | Kotlin 2.x

---

## 5️⃣ E2E लाइव टेस्टिंग पाइपलाइन (GitHub Actions — हर कमिट पर)

```yaml
name: AstraKey CI - Build + Live Emulator E2E
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 17 }
      - run: ./gradlew assembleDebug testDebugUnitTest        # यूनिट टेस्ट
      - name: Enable KVM
        run: |
          echo 'KERNEL=="kvm", GROUP="kvm", MODE="0666", OPTIONS+="static_node=kvm"' | sudo tee /etc/udev/rules.d/99-kvm.rules
          sudo udevadm control --reload-rules && sudo udevadm trigger --name-match=kvm
      - uses: reactivecircus/android-emulator-runner@v2       # लाइव एमुलेटर
        with:
          api-level: 30
          arch: x86_64
          disable-animations: true
          emulator-options: -no-window -gpu swiftshader_indirect -noaudio -no-boot-anim
          script: |
            adb install -r app/build/outputs/apk/debug/app-debug.apk
            adb shell ime enable com.astrakey.app/.ime.AstraKeyIME
            adb shell ime set com.astrakey.app/.ime.AstraKeyIME
            ./gradlew connectedDebugAndroidTest               # असली E2E टाइपिंग टेस्ट
      - uses: actions/upload-artifact@v4                     # APK + रिपोर्ट artifact
        if: always()
        with: { name: astrakey-debug-apk, path: app/build/outputs/apk/debug/ }
```

**बग-फिक्स नियम (स्थायी):**
1. बग मिला → गहरी शोध (कोड + लॉग + शोध दोनों)
2. फिक्स कोड
3. GitHub Actions में लाइव एमुलेटर E2E हरी झंडी तक इंतज़ार ✅
4. हरी झंडी के बाद ही "पक्का हुआ" कहना — कभी अंधेरे में नहीं

---

## 6️⃣ माइलस्टोन रोडमैप

| # | चरण | डिलिवरेबल | स्वीकृति-मानदंड (E2E पुष्टि) |
|---|---|---|---|
| M0 | प्रोजेक्ट सेटअप | रिपो CI + पहला हेलो कीबोर्ड | CI हरा, एमुलेटर में कीबोर्ड दिखे |
| M1 | MVP टाइपिंग | लेटर्स/शिफ्ट/बैकस्पेस/स्पेस | एमुलेटर में टाइप करके टेक्स्ट पुष्टि |
| M2 | नंबर-रो + long-press | वैकल्पिक अक्षर | E2E टेस्ट: long-press क्रिया |
| M3 | हिंदी + हिंग्लिश | देवनागरी लेआउट + ट्रांसलिटरेशन | "namaste"→"नमस्ते" सजेशन टेस्ट |
| M4 | सजेशन/ऑटोकरेक्ट | शब्द-सूची इंजन | ऑटोकैप + डबल-स्पेस टेस्ट |
| M5 | **Ctrl-कमांड बार** | A/Z/X/C/V/Y क्रियाएँ | E2E: copy-paste की सच्ची पुष्टि |
| M6 | कर्सर/एडिट-पैनल | तीर-पैनल + स्पेसबार-ड्रैग | कर्सर मूव टेस्ट |
| M7 | क्लिपबोर्ड + टूलबार | इतिहास + पिन | पेस्ट-इतिहास टेस्ट |
| M8 | थीम्स + वन-हैंडेड | Material You | थीम-स्विच E2E |
| M9 | रिलीज़ | साइन्ड APK → Drive फ़ोल्डर | रिपोर्ट + APK डिलीवरी |

---

## 7️⃣ सुरक्षा और नियम (स्थायी)

- 🔐 GitHub टोकन केवल `/home/user/.secrets/github_token` में (chmod 600), कभी कोड/लॉग/URL में नहीं; git push askpass-सहायक से
- 🔐 CI में टोकन कभी हार्डकोड नहीं — केवल `GITHUB_TOKEN` बिल्ट-इन सीक्रेट
- 🔐 ऐप न्यूनतम अनुमतियाँ: MVP में केवल VIBRATE (कोई INTERNET नहीं = ज़ीरो डेटा-लीक रिस्क)
- ✅ हर काम से पहले गहरी शोध → 100% पक्का → फिर आगे
- ✅ हमेशा शुद्ध हिंदी बातचीत

---

## 8️⃣ जोखिम और उपाय

| जोखिम | उपाय |
|---|---|
| एमुलेटर बूट CI में धीमा | KVM + `-no-boot-anim` + स्नैपशॉट; api-30 x86_64 सबसे स्थिर |
| हिंग्लिश ट्रांसलिटरेशन जटिलता | MVP में नियम-आधारित मैपिंग, बाद में मॉडल |
| IME विभिन्न ऐप्स में व्यवहार | E2E टेस्ट में कई ऐप्स (Messages/Notes) कवर |
| Android 16 (target 36) IME नियम | रिसर्च-फर्स्ट: हर API बदलाव पहले वेब-शोध से पुष्ट |

---

## 9️⃣ अगला कदम (तुरंत)
1. GitHub `Keyboard` रिपो (खाली) में **M0 पुश** — Gradle प्रोजेक्ट + CI वर्कफ़्लो + हेलो कीबोर्ड
2. CI हरी झंडी की पुष्टि → APK artifact
3. M1 से टाइपिंग शुरू — हर कदम E2E से पक्का

> 📎 शोध-सबूत: `apk_analysis/research_evidence.md`, `analysis_full.json`, `feature_strings.json`

---

## 📊 प्रगति-लॉग (लाइव अद्यतन)

### ✅ 25 नवंबर 2026 — M0+M1+M2 पूर्ण, पहली हरी झंडी!
- **CI रन #7 हरा (success)**: यूनिट टेस्ट ✅ + APK बिल्ड ✅ + **लाइव एमुलेटर E2E ✅** (2 टेस्ट पास)
- E2E सबूत: एमुलेटर में AstraKey डिफ़ॉल्ट IME बना → "Hello" टाइप (ऑटो-कैप) → बैकस्पेस "Hell" → शिफ्ट "HellH" → नंबर-रो "5 " → डिलीट — सब असली एमुलेटर पर सत्यापित
- APK: `releases/app-debug.apk` (5.6 MB) — Drive Keyboard फ़ोल्डर में भी अपलोड

### 🐛 बग-फिक्स यात्रा (हर एक: गहरी शोध → फिक्स → लाइव E2E हरी झंडी)
| # | बग (CI रन) | जड़ | फिक्स |
|---|---|---|---|
| 1 | Gradle DSL (R1) | `includeAndroidResources` नाम गलत | `isIncludeAndroidResources` |
| 2 | Kotlin when (R2) | nullable enum का when अपूर्ण | `else` शाखा |
| 3 | IME नहीं खुला (R3) | एमुलेटर का hard-keyboard मोड | `show_ime_with_hard_keyboard=1` |
| 4 | ime set विफल (R4) | इंस्टॉल के 200ms बाद IMS रजिस्ट्री तैयार नहीं | रिट्राई-लूप (attempt 3 पर सफल) |
| 5 | स्क्रिप्ट टूटा (R5) | emulator-runner स्क्रिप्ट line-by-line चलाता है | एक-लाइन लूप |
| 6 | **अतिरिक्त अक्षर "h-e3l)l)o9"** (R6) | KeyView में हर बार नया Handler — removeCallbacks विफल, long-press alt लीक | साझा Handler फील्ड |
| 7 | StaleObjectException (R6-R9) | हर कर्सर-बदलाव पर पूरी स्ट्रिप/पंक्तियाँ रीबिल्ड | स्लॉट-आधारित in-place अपडेट (सुझाव + शिफ्ट दोनों) |
| 8 | hint vs खाली (R7) | खाली EditText hint टेक्स्ट लौटाता है | hint-जागरूक असर्शन |

### 🎯 अगला: M3 (हिंदी+हिंग्लिश E2E) → M5 (Ctrl कमांड E2E: copy-paste की असली पुष्टि)

### 2026-11-26 — उपयोगकर्ता स्क्रीनशॉट-प्रतिक्रिया → UI-स्तर निर्धारण (शोध पूर्ण)
- उपयोगकर्ता ने AstraKey vs SwiftKey स्क्रीनशॉट दिए: APK आकार/फ़ीचर अंतर की चिंता; आदेश: सब पूर्ण और SwiftKey-स्तर।
- स्क्रीनशॉट पिक्सेल-विश्लेषण (apk_analysis/analyze_ui2.py): SwiftKey bg #1C1F28, की #3A3A42, विशेष #2D2F38, की 49dp/गैप 4dp, स्टैक-कीज़, बॉर्डर-स्पेसबार, तीर-पंक्ति ^v<>, सफ़ेद रेखा-टूलबार, बाएँ ∨-घेरा सुझाव।
- हमारी की औसत 20-23dp (लगभग आधी), कीबोर्ड 76% स्क्रीन से शुरू (SwiftKey 51%)।
- 10-कार्य योजना बनी: PLAN_UI_FIX.md — डार्क थीम, मेट्रिक्स, स्टैक, तीर, टूलबार, सुझाव-स्तर, M3 हिंदी, M5 Ctrl-पूर्ण, क्लिपबोर्ड/इमोजी पैनल, सेटिंग्स।
- APK आकार-स्पष्टीकरण: SwiftKey ~100MB (21,971 क्लास, 60+ भाषा-मॉडल, क्लाउड); AstraKey 5.6MB सही स्थिति — फ़ीचर बढ़ेंगे तो आकार भी।


### 2026-11-26 — SwiftKey-स्तर UI + M3 + M5 — सब E2E-सत्यापित (उपयोगकर्ता-स्क्रीनशॉट मापन पर)
- उपयोगकर्ता ने AstraKey vs SwiftKey स्क्रीनशॉट दिए → पिक्सेल-विश्लेषण (analyze_ui2.py): bg #1C1F28, की #3A3D46, विशेष #2D2F38, तीर-पंक्ति ^v<>, स्टैक-कीज़, बॉर्डर-स्पेसबार।
- **कमिट A (UI)**: डार्क-थीम-डिफ़ॉल्ट (KeyboardTheme), स्क्रीन-अनुपात मेट्रिक्स (पंक्ति = स्क्रीन का 6.2% — उपयोगकर्ता के बड़े डिवाइस पर भी SwiftKey जैसा आकार), 13 वेक्टर-आइकन, KeyView→FrameLayout (लेबल+आइकन), टूलबार [क्लिपबोर्ड Ctrl 123 अ इमोजी ⚙], सुझाव-स्ट्रिप ∨-घेरा + मध्य-बोल्ड, तीर-पंक्ति (कर्सर), इमोजी+क्लिपबोर्ड पैनल।
- **कमिट B (M3)**: हिंग्लिश मोड — क्वर्टी टाइपो, देवनागरी सुझाव (हिंदी-प्रथम क्रम, सटीक>छोटा); E2E "नमस्ते" पास।
- **कमिट C (M5)**: Ctrl+Z/Y वास्तविक (UndoManager — शब्द-समूहित स्नैपशॉट, 50-क्षमता); Ctrl+A/Z/X/C/V/Y सब वास्तविक; सेटिंग्स स्विच (थीम/तीर/हैप्टिक)। E2E copy→paste→undo→redo→cut→paste पास।
- **नए बग (4)**: (9) 13 वेक्टर-XML heredoc-क्षत — write_file से मरम्मत; (10) पहली IME-खुलने की रेस — ensureKeyboardOpen रीट्राई+CI वार्म-अप; (11) ट्रांसलिट-रैंकिंग (namaskar>namaste) — सटीक>छोटा क्रम; (12) Kotlin `to`-जोड़ी निष्कर्षण — स्पष्ट compareByDescending।
- रन: 45702873525 F, 45703512142 F, 45704639505 **S (A, 4/4)**, 45705438299 F, 45705950941 F, 45706164676 **S (B)**, 45706796391 **S (C, 6/6 E2E)** — v0.3.0 (code 4), APK 5.66MB Drive में।

