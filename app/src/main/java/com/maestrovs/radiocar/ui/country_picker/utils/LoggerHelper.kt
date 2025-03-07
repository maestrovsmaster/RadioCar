package com.maestrovs.radiocar.ui.country_picker.utils

/**
 * Created by maestromaster$ on 07/03/2025$.
 */

import android.util.Log

internal object LoggerHelper {

    private const val LOG_TAG = "CountryPicker"

    /**
     * Logs an error message along with the exception details to the console.
     * @param exception The [Exception] instance that contains the error details to be logged.
     */
    fun logError(exception: Exception) {
        Log.e(LOG_TAG, exception.message, exception)
    }
}