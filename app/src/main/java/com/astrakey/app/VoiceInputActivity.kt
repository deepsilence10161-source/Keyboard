package com.astrakey.app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast

/**
 * वाक्-पहचान सहायक (पारदर्शी) — IME से Intent-आधारित पहचानकर्ता चलाता है;
 * परिणाम प्रसारण (broadcast) से KeyboardView तक पहुँचता है, जो कर्सर-स्थान पर लिखता है।
 * (SwiftKey dex-प्रमाण: VoiceInputHelperActivity — वही पैटर्न)
 */
class VoiceInputActivity : Activity() {

    companion object {
        const val ACTION_RESULT = "com.astrakey.app.VOICE_RESULT"
        const val EXTRA_TEXT = "text"
        private const val REQ = 4101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
        }
        try {
            startActivityForResult(i, REQ)
        } catch (_: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "वाक्-पहचान उपलब्ध नहीं (Google ऐप चाहिए)", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = results?.firstOrNull()
            if (!text.isNullOrBlank()) {
                sendBroadcast(Intent(ACTION_RESULT).setPackage(packageName).putExtra(EXTRA_TEXT, text))
            }
        }
        finish()
    }
}
