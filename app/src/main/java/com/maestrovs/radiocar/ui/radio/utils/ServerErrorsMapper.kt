package com.maestrovs.radiocar.ui.radio.utils

import android.content.Context
import com.maestrovs.radiocar.R

fun errorMapper(error: String, context: Context) = if (error.contains("500")) {
    context.getString(R.string.error_500)
} else if (error.contains("503")) {
    context.getString(R.string.error_503)
} else if (error.contains("No address associated with hostname")) {
    context.getString(R.string.error_no_address)
} else {
    error
}
