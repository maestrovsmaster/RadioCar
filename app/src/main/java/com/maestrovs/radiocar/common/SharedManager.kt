package com.maestrovs.radiocar.common

import android.content.Context


object SharedManager {

    const val RADIOCAR_PREFS = "RADIOCAR_PREFS"


    fun writeStringOption(context: Context, key: String?, value: String?) {
        val editor =
            context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE).edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun removeOption(context: Context, key: String?) {
        val editor =
            context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE).edit()
        editor.remove(key)
        editor.apply()
    }

    fun readStringOptions(context: Context, key: String?): String? {
        val prefs = context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE)
        return prefs.getString(key, null)
    }


    fun writeBooleanOption(context: Context, key: String, value: Boolean) {
        val editor =
            context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE).edit()
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun readBooleanOptions(context: Context, key: String, defValue: Boolean): Boolean {
        val prefs = context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE)
        return prefs.getBoolean(key, defValue)
    }


}