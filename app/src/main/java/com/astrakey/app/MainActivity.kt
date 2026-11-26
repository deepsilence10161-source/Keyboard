package com.astrakey.app

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial

/**
 * सेटअप गाइड — Enable → Choose → टाइप करके देखें + कीबोर्ड-सेटिंग्स
 */
class MainActivity : AppCompatActivity() {

    private lateinit var statusEnabled: TextView
    private lateinit var statusSelected: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusEnabled = findViewById(R.id.statusEnabled)
        statusSelected = findViewById(R.id.statusSelected)

        findViewById<Button>(R.id.btnEnable).setOnClickListener {
            startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
        }
        findViewById<Button>(R.id.btnChoose).setOnClickListener {
            try {
                startActivity(Intent("android.settings.INPUT_METHOD_PICKER"))
            } catch (e: Exception) {
                startActivity(Intent(Settings.ACTION_INPUT_METHOD_SETTINGS))
            }
        }

        bindSwitch(R.id.switchArrows, "arrow_row", true)
        bindSwitch(R.id.switchHaptic, "haptic", true)
        bindSwitch(R.id.switchGesture, "gesture_on", true)
        bindSwitch(R.id.switchAutoCorrect, "autocorrect", true)

        val prefs = getSharedPreferences("astrakey_prefs", MODE_PRIVATE)
        val current = prefs.getString("theme_name", null)
            ?: if (prefs.getBoolean("theme_dark", true)) "dark" else "light"
        val radioIds = mapOf(
            "dark" to R.id.radioDark,
            "light" to R.id.radioLight,
            "amoled" to R.id.radioAmoled,
            "ocean" to R.id.radioOcean,
            "sunset" to R.id.radioSunset,
            "forest" to R.id.radioForest,
            "lavender" to R.id.radioLavender,
            "rose" to R.id.radioRose
        )
        (radioIds[current] ?: R.id.radioDark).let { findViewById<android.widget.RadioButton>(it).isChecked = true }
        radioIds.forEach { (name, id) ->
            findViewById<android.widget.RadioButton>(id).setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    // दूसरी थीम-पंक्ति साफ़ (दो RadioGroup — परस्पर-अनन्य)
                    if (name in setOf("dark", "light", "amoled", "ocean")) {
                        findViewById<android.widget.RadioGroup>(R.id.themeGroup2).clearCheck()
                    } else {
                        findViewById<android.widget.RadioGroup>(R.id.themeGroup).clearCheck()
                    }
                    prefs.edit().putString("theme_name", name).apply()
                }
            }
        }

        // स्थिति-मोड: पूर्ण / फ्लोटिंग / एक-हाथ-बाएँ / एक-हाथ-दाएँ
        val modeIds = mapOf(
            "full" to R.id.radioModeFull,
            "float" to R.id.radioModeFloat,
            "left" to R.id.radioModeLeft,
            "right" to R.id.radioModeRight
        )
        val currentMode = prefs.getString("ui_mode", "full") ?: "full"
        (modeIds[currentMode] ?: R.id.radioModeFull).let { findViewById<android.widget.RadioButton>(it).isChecked = true }
        modeIds.forEach { (name, id) ->
            findViewById<android.widget.RadioButton>(id).setOnCheckedChangeListener { _, checked ->
                if (checked) prefs.edit().putString("ui_mode", name).apply()
            }
        }
    }

    /** कीबोर्ड-सेटिंग स्विच — prefs में सुरक्षित, IME अगली खुलने पर लागू करता है */
    private fun bindSwitch(id: Int, key: String, def: Boolean) {
        val sw = findViewById<SwitchMaterial>(id)
        val prefs = getSharedPreferences("astrakey_prefs", MODE_PRIVATE)
        sw.isChecked = prefs.getBoolean(key, def)
        sw.setOnCheckedChangeListener { _, checked ->
            prefs.edit().putBoolean(key, checked).apply()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshStatus()
    }

    private fun refreshStatus() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val enabled = imm.enabledInputMethodList.any { it.packageName == packageName }
        val selected = Settings.Secure.getString(
            contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD
        )?.startsWith(packageName) == true

        statusEnabled.text = if (enabled) getString(R.string.status_enabled) else getString(R.string.status_not_enabled)
        statusSelected.text = if (selected) getString(R.string.status_selected) else getString(R.string.status_not_selected)
    }
}
