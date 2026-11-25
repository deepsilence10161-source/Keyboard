# AstraKey ⌨️ — Advance, Smart, Attractive, Easy Keyboard

> Gboard + SwiftKey की गहरी स्कैनिंग से सीखकर बना **भारत का स्मार्ट कीबोर्ड** — Kotlin (Android Native)

## फीचर्स (v0.1.0 — Phase A+B)
- ✅ SwiftKey-स्टाइल UI: सफेद गोल कीज़, नीला एक्सेंट, सुझाव-स्ट्रिप, टूलबार
- ✅ Samsung-स्टाइल **Ctrl-कमांड**: Ctrl बटन → A(सेलेक्ट-ऑल) C(कॉपी) X(कट) V(पेस्ट)
- ✅ English QWERTY + **हिंदी देवनागरी** + **हिंग्लिश ट्रांसलिटरेशन** (namaste → नमस्ते)
- ✅ नंबर-रो टॉगल, लंबा-दबाव वैकल्पिक अक्षर, शिफ्ट/कैप्सलॉक, ऑटो-कैपिटलाइज़
- ✅ क्लिपबोर्ड पैनल, इमोजी पैनल, हैप्टिक फीडबैक
- ✅ **लाइव एमुलेटर E2E टेस्टिंग** — हर कमिट पर GitHub Actions में

## बिल्ड
```bash
./gradlew assembleDebug
```

## टेस्ट
- यूनिट: `./gradlew testDebugUnitTest`
- E2E (एमुलेटर चाहिए): `adb shell ime set com.astrakey.app/.ime.AstraKeyIME && ./gradlew connectedDebugAndroidTest`

## रोडमैप
देखें: `../ROADMAP.md` (M0–M9 माइलस्टोन)
