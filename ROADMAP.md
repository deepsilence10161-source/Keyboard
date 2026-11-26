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


### 2026-11-26 — नेविगेशन-बार इनसेट शोध + समाधान (v0.4.0)
- उपयोगकर्ता-प्रतिक्रिया: UI मोबाइल के तीनों नेव-बटनों में घुस रही; Microsoft की परफेक्ट → दोनों APK फिर अधिकतम-गहराई शोध।
- आधिकारिक दस्तावेज़: Android 11+ से IME-विंडो एज-टू-एज; Android 15+ अनिवार्य; IME को navigationBars.bottom गद्दी स्वयं लगानी है।
- SwiftKey dex-प्रमाण: View$OnApplyWindowInsetsListener, WindowInsets$Type.navigationBars, onComputeInsets, keyboardHeight, WindowInsetsAnimation।
- Gboard arsc-प्रमाण: keyboard_height_33–52_mm (भौतिक-मिमी मॉडल) + keyboard_height_ratio।
- समाधान: ViewCompat insets-गद्दी (navigationBars|displayCutout) + API≥30 संसाधन-गद्दी-जाल (800ms) + बजट-मेट्रिक्स (avail = स्क्रीन−नेव; कुल ≤48%; पंक्ति 44–72dp) + E2E keyboardSitsAboveNavBar।
- **घटना-शिक्षा**: वर्कस्पेस-स्नैपशॉट 128MB-सीमा — बड़े APK (गबोर्ड 87MB) ने app/+.git दबा दिया; GitHub से पुनः-क्लोन सफल (81d2808)। **नियम: बड़े APK /tmp में — /home/user में कभी नहीं।**
- शोध-दस्तावेज़: apk_analysis/nav_inset_research.md

- **परिणाम (v0.4.0)**: CI रन 45721097073 (कमिट dc7780f) — **पूर्ण हरी झंडी**: यूनिट ✅ + APK ✅ + लाइव एमुलेटर E2E **7/7 पास** (नया keyboardSitsAboveNavBar सहित) — कीबोर्ड नेविगेशन-बार के ऊपर सत्यापित। APK 5.66MB → Drive `AstraKey-v0.4.0-debug-navfix-E2E-7tests.apk` (id 1FX9KB2WVdzbp9diZA4vMtfB2LHaqOhV-)।
- नया बग (13): GIT_ASKPASS फ़ाइल का execute-bit स्नैपशॉट में नहीं बचा — push विफल; chmod 755 से सुधार। **हर सत्र-शुरुआत में chmod 755 askpass.sh जाँचें।**

### 2026-11-26 — स्वचालित-परीक्षण युग + विज़ुअल-सत्यापन (v0.5.0)
- उपयोगकर्ता-निर्देश: "मैं स्वयं परीक्षण नहीं करूँगा, तुम स्वयं करो हर चीज़" → स्थायी-नियम: शून्य-मैनुअल-परीक्षण।
- दो-स्तरीय प्रणाली: (1) कार्य-E2E — 6 नए: longPressAltInsertsDigit (q→1), emojiPanelCommitsEmoji, clipboardPanelPastesFromHistory, hideButtonClosesAndReopensKeyboard, themeToggleFromSettingsAppliesAndTypingSurvives, ScreenshotTest (8 स्थितियाँ)।
- (2) विज़ुअल-E2E — CI में स्क्रीनशॉट-कैप्चर → artifact → एजेंट स्वयं देखकर सत्यापित।
- रन: 45724091846 F (Robolectric android-all डाउनलोड IOException — क्षणिक; यूनिट-पुनः-प्रयास जोड़ा), 45724289907 F (क्लिप-पैनल: चयन-पर-पेस्ट बदलता है — उचित व्यवहार; परीक्षण में चयन-समापन जोड़ा), 45725028312 S (13/13), 45725923424 S (स्क्रीनशॉट-artifact बना — MediaStore Download/AstraKey तकनीक: Gradle अनइंस्टॉल /Android/data मिटाता है — बग 14), 45726930679 S (स्वच्छता-फिक्स: आधार-स्थिति + भाषा-बहाली — रिसाव-निवारण)।
- **एजेंट-दृश्य-निष्कर्ष (स्वयं आँखों से)**: डार्क थीम + नेव-ऊपर-बैठना ✅, Ctrl-मोड A/Z/X/C/V/Y नीले ✅, इमोजी-पैनल (हाल के+ग्रिड) ✅, क्लिपबोर्ड-पैनल (लाइव-इतिहास 3 प्रविष्टियाँ) ✅, लाइट-थीम ✅, हिंग्लिश सुझाव "कैसे" लाइव ✅।
- APK v0.5.0 (code 6, 5.66MB) → Drive `AstraKey-v0.5.0-debug-E2E-14tests.apk` (id 1y6ucAEH-qCe0JCS7bMc-snakTzQdQKQU)।
- स्क्रीनशॉट संग्रह: /home/user/screenshots/AstraKey/ (01–08)

### 2026-11-26 — माइक + 4 थीम + जेस्चर-टाइपिंग (v0.6.0) — लगातार-चक्र, अंतिम-रिपोर्ट
- उपयोगकर्ता-निर्देश: "लगातार करते रहो, बीच में रोककर अपडेट मत दो" → निरंतर-चक्र, संवाद केवल अंत में।
- माइक: VoiceInputActivity (Intent-पहचानकर्ता + प्रसारण — SwiftKey VoiceInputHelperActivity-पैटर्न), टूलबार-आइकन, E2E micLaunchesRecognizerAndKeyboardSurvives।
- थीम: KeyboardTheme 4-पटल (डार्क/लाइट/एमोलेड/ओशन) + theme_name-prefs (पुरानी theme_dark-संगत) + RadioGroup-सेटिंग्स; E2E radioAmoled-स्विच।
- जेस्चर: dispatchTouchEvent-निरीक्षण (स्वाइप-पथ संग्रह, टैप-दमन), पथ-रीसैंपल (16-बिंदु) + शब्दकोश-पथ-मिलान (औसत-दूरी − आवृत्ति-सहायता), KeyView.abortTouch (रिपीट/अल्ट-टाइमर रक्षा), सेटिंग्स-स्विच।
- **जेस्चर-निदान-कथा (5 रन)**: sendPointerSync IME-अवरोध → UiAutomation.injectInputEvent (घटनाएँ पहुँची पर "type" शब्दकोश-बाह्य) → शब्दकोश type/qwerty/tree + device.swipe (कार्यशील) → निदान-लॉग से **तीव्र-स्वाइप सह-एकीकृत-मूव जाल** (पथ 74px रुका; UP 174px) → **UP-स्थिति पथ में** → विघटन सफल "qwerty"।
- रन: 45733093994 F (gradlew 644 — **बग 15: स्नैपशॉट-मोड-क्षत**; git --chmod=+x + CI chmod-चरण), 45733326530 F (toast-सहायक), 45733680565 F (Rect.centerX()), 45734287059 F (sendPointerSync), 45734931539 F (dict+inject), 45735953390 F (निदान: लॉग-खाली), 45736826785 F (निदान: water/qwerty), 45737641590 F (निदान: UP-जाल पकड़ा), **45738403238 S — 15/15 लाइव E2E**।
- एजेंट-दृश्य-सत्यापन: 09_amoled (शुद्ध-काला, माइक-आइकन, जेस्चर-स्विच) + 01_dark (स्वच्छ-आधार: English-स्पेसबार, बिना-नंबर-रो) — दोनों परफेक्ट।
- APK v0.6.0 (code 7, 5.67MB) → Drive; स्क्रीनशॉट 01–09 /home/user/screenshots/AstraKey/।
- **नया सत्र-नियम**: /home/user/.config-git/session_restore.sh — हर सत्र-शुरुआत में चलाओ (askpass-exec, gradlew-exec, origin-रिमोट बहाली)।

### 2026-11-26 — UI-परिष्कार + चिह्न-पैनल + क्लिप-पिन (v0.7.0) — निरंतर-मोड, उपयोगकर्ता-पिक्सेल-पैमाना
- उपयोगकर्ता-प्रतिक्रिया: "काफी बेहतर, पर UI थोड़ी और adjust; बहुत काम/कमियाँ बाकी" + "रुक मत, लगातार continue" → M7 (v0.7.0, code 8); UI-संदर्भ = उपयोगकर्ता-स्क्रीनशॉट पिक्सेल-मापन (PIL, 720×1570)।
- A UI-परिष्कार: टूलबार stripBg प्रति-थीम (DARK 0xFF32343C / LIGHT 0xFFE4E7ED / AMOLED 0xFF141414 / OCEAN 0xFF152238 — SwiftKey-स्ट्रिप-मेल), की-त्रिज्या 10dp, टूलबार-गद्दी-पुनर्गणना, निचली-पंक्ति 123(1.2)+😊+,+space(4.6)+.+enter(1.5) — "123" टेक्स्ट-की (स्क्रीनशॉट-प्रमाण), सपात-एंटर; तीर-पंक्ति ↑↓←→ (SK-क्रम ^v<>); एंटर applyGhost (जेस्चर-रक्षा)।
- B चिह्न-पैनल: Panel.SYMBOLS + buildSymbolsPanel 5-क्षैतिज-पृष्ठ (अंक; @#$%&*-+(); !":;,?~^|; []{}<>=/\\; ₹€£¥•©®°§±); 123-की लंबा-दबाव से पैनल; E2E symbolsPanelViaLongPress123।
- C क्लिप-पिन + शब्दकोश: CommandEngine pinnedClips/togglePin (cap 6; clear पिन-रक्षित), clipRow 📌-प्रत्यय (desc clip_pinned/clip_entry), यूनिट "pinned clips survive clear"; शब्दकोश +~450 शब्द; E2E clipPinSurvivesClear।
- **compile-जाल-शिक्षा**: python-हरमेटिक बैच Kotlin-चरित्र-एस्केप तोड़ता है (`'\''`→`''` खाली-चरित्र; KeyboardView.kt:446) — नियम: विशेष-चरित्र chr()-कोड से बनाओ (tok(): `'"'`, `'\''`, `'\\'`) + push-पूर्व repr/cat -A सत्यापन; रन 45824531060 F, 45825389733 F (assertTrue-import गायब — नया यूनिट-टेस्ट import-जाल)।
- **रन 45825752974 S — E2E 17/17 + यूनिट 25/25 शत-प्रतिशत**; स्क्रीनशॉट 10-फ़ाइलें (नया 10_symbols — 5-पृष्ठ-पूर्ण सत्यापित: बैकस्लैश/₹-पंक्तियाँ सहित); 01_dark (नई-टूलबार+तीर-पंक्ति) + 05_clip (पिन-प्रणाली) दृश्य-पुष्ट।
- APK v0.7.0 (5,688,281 B) → Drive `AstraKey-v0.7.0-debug-ui-symbols-clip.apk` (id 1Z07F79Pblib7Vsx83jfgYaq8VHb-3DDf, ⭐)।

### 2026-11-26 — फ्लोटिंग-कीबोर्ड + एक-हाथ-मोड + 4 नई थीम (v0.8.0) — निरंतर-मोड M8
- आगे-पंक्ति से: फ्लोट (घसीटने-योग्य) + एक-हाथ (बाएँ/दाएँ) + थीम-विस्तार (4 नई: सूर्यास्त/वन/लैवेंडर/गुलाब — कुल 8)।
- स्थापत्य: AstraKeyIME अब host(FrameLayout)→KeyboardView; स्थिति-मोड full/float/left/right (prefs ui_mode); KeyboardView फ्लोट-पट्टी (⠿⠿-घसीटना + ∨-पुनर्स्थापना; setOnTouchListener raw-निर्देशांक) + uiController-कॉलबैक; dispatchTouchEvent में पट्टी-क्षेत्र-बाह्य (जेस्चर-संग्रह से)।
- **LatinIME-पैटर्न (शोध-पुष्टि)**: onComputeInsets TOUCHABLE_INSETS_REGION (kv-सीमा) + contentTopInsets — फ्लोट में कीबोर्ड-बाह्य स्पर्श ऐप तक; onConfigureWindow/window.setLayout(MATCH, MATCH) — फ्लोट-विंडो पूर्ण-ऊँचाई।
- **3-जाल-निदान (5 रन)**: (1) +2 RadioGroup से demoEdit 320×640-CI-स्क्रीन-बाह्य a11y-वृक्ष → सब 20 लाल ("EditText पर टैप नहीं") — demoEdit ऊपर + स्क्रॉल-पुनःप्रयास; (2) runner प्रति-पंक्ति sh -c — multi-line brace-ब्लॉक अमान्य (एक-पंक्ति-गोली); (3) **IMS-बीच-कंटेनर WRAP-ऊँचाई** — window MATCH पर भी host सिकुड़ा (before=270=शीर्ष सबूत) → **FloatHost.onMeasure पूर्ण-ऊँचाई-गारंटी** + BOTTOM-गुरुत्व में bottomMargin (topMargin अकार्यशील); निदान-चरण CI में स्थायी (विफलता-स्क्रीनशॉट-पुल + logcat)।
- **रन 45839720758 S — E2E 20/20 + यूनिट 26/26** (नए: oneHandedLeftNarrowsKeyboardAndTypes, floatingDragRaisesKeyboardAndTypingSurvives — device.swipe पट्टी-घसीटना + spacebar-ऊपर-उठना-पुष्टि, sunsetThemeSwitchAndTypingSurvives; यूनिट +2 नई-पटल); स्क्रीनशॉट 13-फ़ाइलें (11_floating — पट्टी+संकरा ✓, 12_onehand_left ✓, 13_sunset ✓)।
- APK v0.8.0 (5,696,153 B) → Drive `AstraKey-v0.8.0-debug-float-onehand-8themes.apk` (id 15wjrFHidmizkw2YeWsQQMjFLSbmsHzGh, ⭐)।
- **स्थायी-शिक्षाएँ**: (a) CI-एमुलेटर 320×640 — activity_main में नया-UI जोड़ते समय demoEdit स्क्रीन-में रखना अनिवार्य (सब-सबूत उसी क्षेत्र पर); (b) IME-विंडो-ऊँचाई बदलना = window.setLayout + host-गारंटी दोनों (एक अकेला अधूरा); (c) android-emulator-runner स्क्रिप्ट = पंक्ति-दर-पंक्ति sh।

### 2026-11-26 — देवनागरी-सीधी-क्वर्टी + डबल-स्पेस + बैकस्पेस-स्वाइप (v0.9.0) — निरंतर-मोड M9
- भाषा-प्रणाली विस्तार: langEnglish-बूल → **langMode 3-स्थिति** (en/translit/deva; prefs lang_mode, lang_english-संगत-आधार) — चक्र EN→अ(translit-हिंग्लिश-सुझाव)→क(देवनागरी-सीधी)→EN; टूलबार-संकेत अ/क/EN; hindiRows() (पूर्व-निर्मित) deva-मोड में सीधे; deva में सुझाव-शून्य + नंबर-रो-विकल्प-निष्क्रिय।
- डबल-स्पेस-पूर्णविराम: अक्षर+स्पेस के बाद दूसरा-स्पेस → पूर्ववर्ती-स्पेस-मिटाकर ". " (मानक-IME)।
- बैकस्पेस-स्वाइप-शब्द-मिटाना: pointInBackspace-पहचान + बाएँ-60dp-घसीटने पर deleteLastWord (शब्द+अनुगामी-स्पेस; abortTouch-रिपीट-रक्षा) + **खाली-क्षेत्र पर ऑटो-कैप-पुनर्विचार** (परीक्षण-सबूत-सुधार: पहला-रन "x" बनाम "X" — shift-बहाली-शेष)।
- E2E +3: devanagariDirectTypingCommitsHindi (2×language → क-की → "क"), doubleSpaceInsertsPeriod ("A. "), backspaceSwipeDeletesWholeWord (device.swipe बाएँ → खाली → "X" — कैप-पुनर्विचार-सहित); यूनिट +3 (KeyboardLayoutsTest: hindiRows/qwerty/ctrlTargets)।
- **रन 45842023939 F (1/23 — केवल कैप-पुनर्विचार-शेष)** → सुधार `4476adf` → **45843445562 S — E2E 23/23 + यूनिट 29/29 शत-प्रतिशत**; स्क्रीनशॉट 14-फ़ाइलें (14_devanagari — पूरी-वर्णमाला+क-संकेत ✓)।
- APK v0.9.0 (5,697,273 B) → Drive `AstraKey-v0.9.0-debug-devanagari-dblspace-swipe-cut.apk` (id 17iY1gA5X4EFykk7IIpMwY3srIyULArLm, ⭐)।

### 2026-11-26 — क्लिप-मेनू + ताज़ा-शब्द + कर्सर-पैड + ऑटो-सुधार (v0.11.0) — निरंतर-मोड M10+M11
- M10: (a) क्लिप-लंबा-दबाव-मेनू — PopupMenu (IME-संदर्भ-AlertDialog-window-token-जाल-से-बचाव; anchor-view विधा) पिन/संपादित-संवाद/मिटाओ + CommandEngine.deleteClip/updateClip (पिन-स्थिति-रक्षा) + KeyView.fireLongPress सदैव onKeyAlt (alt-शून्य-सेवा-कीज़-सहित); (b) सुझाव-ताज़ा-शब्द-प्राथमिकता — ≥2-अक्षर-टाइप-शब्द पहला-स्लॉट (जैसा-लिखा-वैसा-भेजना); (c) स्पेसबार-लंबा-दबाव-कर्सर-पैड — 22dp/कदम KEYCODE_DPAD_LEFT/RIGHT, 800ms-निगल-गार्ड, लेबल "◀ ▶", padSwipeDist-ट्रैकिंग UP<8dp=पैड-ऑटो-बंद (पैड-मोड-टैप-निगमन-महा-जाल-सुधार: handleKey-SPACE-टैप-द्वार-पैड-DOWN-consume-से-अगम्य → 5-E2E-कैस्केड रन 45858802923)।
- M11 ऑटो-सुधार-इंजन: bestCorrection(damerauDistance; प्रथम-चर-मेल w[0]==p[0]; len≥3; maxDist len≥6?2:1; freq-दहलीज़ 40/55; dict-शब्द→null) — SPACE-पर en+prefs `autocorrect`, caps-संरक्षण replaceFirstChar; यूनिट +6 (teh→the, wrold→world — Levenshtein-जाल-प्रमाणित Damerau-आवश्यक)।
- E2E +4: clipLongPressMenuDeletesEntry, clipPinSurvivesClear (मेनू-विधा पिन-क्लिक+अनपिन-सफ़ाई; खुला-मेनू-पैनल-बंद-करना-अनिवार्य), suggestionRowShowsTypedWordFirst, spacebarLongPressCursorPadMovesCursor (swipe/8=40px/कदम — /4=80px→3-कदम "xAbc" जाल), autocorrectFixesTypoOnSpace; ScreenshotTest +1 (15_clip_menu)।
- **CI-गाथा (14-रन)**: 45855228995 F → 45856549325 F → 45858094642 F (यूनिट teh/wrold) → 45858802923 F (att1-Robolectric-jar-क्षणिक → rerun-failed-jobs-API; att2-पैड-कैस्केड) → 555be64-पैड-सुधार → 45860639784 F (केवल-hinglish) → 385cf25/94b9133/f33fa8a diag×3 (logcat-रिंग-बफर-उत्सर्जन-जाल — परीक्षण-भीतर-कैप्चर-समाधान) → **KeyView-महा-जाल-खोज**: alt-शून्य-कीज़-पर 450ms-पार-CI-धीमा-टैप = लंबा-दबाव-मिसफायर-टैप-निगलना (रन 45876611443 dTouch-प्रमाण) → scheduleAlt-केवल-सार्थक-कीज़-पर (1464bc7); points-swipe-tapKey = सार्व-कैस्केड (hello→-3))9 — 1f5204e/62b1524-विपुल) → **चिप-a11y-कैश-महा-जाल**: चिप-desc-in-place-अद्यतन = uiautomator-अदृश्य, पर-चिप-पिक्सेल-प्रमाणित-दिखता-हुआ (विफलता-स्क्रीनशॉट hinglish_fail.png = निर्णायक-सबूत) → By.text + सीधा-a11y-विंडो-चाल + कोऑर्डिनेट-टैप + ऐप a11y-घटना-धक्का (8064a44)।
- **निदान-स्थापत्य-शिक्षाएँ**: (a) ci.yml `exit 1` पुल-पंक्तियों-से-पहले = पुल-निगलना-जाल — पुल-समूह-भीतर; (b) Gradle-कंसोल-लंबी-पंक्ति-कटौती — JUnit-XML-रिपोर्ट (test-reports-artifact) = कटौती-रहित-चैनल; (c) परीक्षण-भीतर takeScreenshot+logcat-फ़ाइल-चैनल /sdcard/Download/AstraKey = विफलता-क्षण-पिक्सेल-सबूत।
- **रन 45884056563 S — E2E 27/27 + यूनिट 35/35 शत-प्रतिशत**; स्क्रीनशॉट 15-फ़ाइलें (15_clip_menu — पिन/संपादित/मिटाओ-मेनू पिक्सेल-सत्यापित)।
- APK v0.11.0 (5,702,273 B) → Drive `AstraKey-v0.11.0-debug-clipmenu-autocorrect-cursorpad.apk` (id 1_oYanWbK5jiaXaaXDbD_SK-Op05bolCW, ⭐)।

### 2026-11-26 — उपयोगकर्ता-शब्दकोश + पेस्ट-चिप + ऊँचाई-सेटिंग (v0.12.0) — निरंतर-मोड M12
- (a) उपयोगकर्ता-शब्दकोश-सीखना: SuggestionEngine.learn (अक्षर-मात्र 2–24 चर, cap 300 न्यूनतम-गणना-निष्कासन) + सीखे-शब्द-उच्च-प्राथमिकता (hindiFirst-बाद-से-पूर्व; अन्यथा-प्रथम-स्लॉट-स्तर) + स्थायित्व prefs learned_words (word:count,…) load/save — सीखना-बिंदु: स्पेस-कमिट-टाइप-शब्द, चिप-कमिट (lowercase), ऑटो-सुधार-शब्द; KeyboardView.kbHeightMul/applyKbHeightPref।
- (b) पेस्ट-चिप: क्लिपबोर्ड-पाठ-खाली-शब्द-क्षण-सुझाव-पट्टी "📋 सामग्री" (lastPastedClip-दोहराव-दमन) — commit(clip)+desc paste_chip; सिग्नेचर-clip-सहित।
- (c) कीबोर्ड-ऊँचाई: prefs kb_height small(0.88)/normal(1.0)/tall(1.14) → rowH-गुणक (40–84dp-बंधन) + सेटिंग्स heightGroup-रेडियो।
- E2E +3 (30 कुल): learnedWordSurfacesInSuggestions (सीखा-"flurb" चिप-क्लिक), pasteChipPastesClipboard (paste_chip-क्लिक→"hello123"), keyboardHeightSettingChangesKeySize (tall>normal spacebar-ऊँचाई-मापन); यूनिट +4 (39 कुल): सीखा-सुझाव/cap-300/junk-छन्नी/setLearned; शॉट 16 (16_paste_chip — "📋 पेस्ट-चिप" चिप स्पष्ट-सत्यापित)।
- **परीक्षण-जाल-शिक्षाएँ**: (a) am start -S = लक्ष्य-प्रक्रिया-मृत्यु = instrumentation-स्वयं-मृत (androidTest-ऐप-प्रक्रिया-में-चलता) — **कभी-नहीं**; ActivityScenario.onActivity-मुख्य-थ्रेड-setText-विधा; (b) Ctrl+a-टैप-क्षेत्र-चयन-शेष-रहता (उपयोगकर्ता-दृश्य-चयन≠प्रोग्राम-सफ़ाई); (c) SUGGEST-पैनल back-की-रहित — पैनल-परीक्षण-अंत-पुष्टि-सावधानी; (d) एक-backspace = केवल-अनुगामी-स्पेस — पूर्ण-क्षेत्र-सफ़ाई Ctrl+a-बाद-भी।
- **रन-गाथा**: 45886471371 F (3-जाल) → 45887927264 S (30/30+39/39, शॉट-सुधार-चक्र: 769bcf2/7e22b64) → 45891399396 F (am -S-मृत्यु; Robolectric-क्षणिक-पुनःचालन) → **45892822965 S — E2E 30/30 + यूनिट 39/39 शत-प्रतिशत**।
- APK v0.12.0 (5,707,741 B) → Drive `AstraKey-v0.12.0-debug-learn-pastechip-height.apk` (id 1Qwt-qcQRFrgLa3LAag5ZC9kTPAYOGFU-, ⭐)।

### 2026-11-26 — कैप्स-लॉक + तीर-रिपीट + इनकॉग्निटो (v0.13.0) — निरंतर-मोड M13
- (a) कैप्स-लॉक-सत्यापन: ShiftState.LOCK पूर्व-मौजूद (OFF→ON_ONCE→LOCK-चक्र; uppercase-कमिट-स्थिर) — E2E capsLockTypesUppercaseSticky (ABC→ABCD→e-बहाली; **जाल: खुलने-पर-ऑटो-कैप ON_ONCE-पहले-से → 2-टैप=OFF — 4-टैप-पहले-OFF-तक-फिर-2-टैप-LOCK**)।
- (b) तीर-लंबा-दबाव-रिपीट: KeyView-रिपीट-पथ-विस्तार (ARROW_LEFT/RIGHT = startRepeat-योग्य; BACKSPACE-सहित-रोस्टर) + moveCursorRepeat (~18/स, 55ms-tick) — E2E arrowLongPressRepeatsCursorMove (2.5s-दबाव→x-क्षण→"xAbcd")।
- (c) पीरियड-लंबा-दबाव = "," (SwiftKey-मानक; alt ":!"→",") — E2E periodLongPressInsertsComma ("A,"; **जाल: tapKey("A")-desc-अस्तित्वहीन — लोअरकेस-ही + ऑटो-कैप**)।
- (d) इनकॉग्निटो-गार्ड: IME_FLAG_NO_PERSONALIZED_LEARNING → KeyboardView.noLearning → सीखना-सारे-3-बिंदु-बंद (स्पेस/चिप/ऑटो-सुधार) — E2E incognitoFlagSkipsLearning (**क्रम-जाल: दूसरी-ActivityScenario-launch = टैप-अगम्य — इनपुटटाइप-खुलने-से-पूर्व-सेट-अनिवार्य**)।
- E2E +4 (34 कुल) + शॉट 17 (17_caps_lock — **पूरा-कीबोर्ड-अपरकेस+क्षेत्र-"A" पिक्सेल-सत्यापित**; जाल: पुराने-क्षेत्र-बंधन-आर्टिफैक्ट → निर्धारक-विधा सफ़ाई+पुनर्खुलना (ऑटो-कैप-गारंटी) → 1-टैप-LOCK → A-कमिट)।
- **रन-गाथा**: 45894888400 F (4-जाल) → **45896534261 S — E2E 34/34 + यूनिट 39/39** → शॉट-सुधार-चक्र (f5ae0d2, be119a0) → **45899328422 S चरम-हरा**।
- APK v0.13.0 (5,708,309 B) → Drive `AstraKey-v0.13.0-debug-capslock-arrowrepeat-incognito.apk` (id 1pckSbIF7QTnp13tZLuYbD5OLJ5nR_xBF, ⭐)।
