package com.maestrovs.radiocar.ui.radio.utils

import android.content.Context
import com.maestrovs.radiocar.R

fun errorMapper(error: String) = if (error.contains("500")) {

    RadioErrorType.ServiceNotAvailable
} else if (error.contains("503")) {
    RadioErrorType.ServiceNotAvailable
} else if (error.contains("No address associated with hostname")) {

    RadioErrorType.NoInternet
} else {
    RadioErrorType.Other
}


enum class RadioErrorType{
    ServiceNotAvailable,
    NoInternet,
    Other
}
