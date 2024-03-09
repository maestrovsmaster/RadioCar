package com.maestrovs.radiocar.ui.settings

import androidx.lifecycle.ViewModel
import com.maestrovs.radiocar.BuildConfig

class SettingsViewModel : ViewModel() {


    val versionDisplay: String
        get() = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

}