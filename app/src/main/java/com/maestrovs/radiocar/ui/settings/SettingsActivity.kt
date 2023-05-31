package com.maestrovs.radiocar.ui.settings

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.settings.ui.main.SettingsFragment


const val KEY_SETTINGS_INPUT_MESSAGE = "inputMessage"
const val KEY_SETTINGS_RESULT_MESSAGE = "resultMessage"
const val REQUEST_CODE_SETTINGS = 1001

class SettingsActivity : AppCompatActivity() {



    companion object {
        fun newIntent(activity: Context) = Intent(activity, SettingsActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SettingsFragment.newInstance())
                .commitNow()
        }

    }
}