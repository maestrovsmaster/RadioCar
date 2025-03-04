package com.maestrovs.radiocar.shared_managers

import android.content.Context
import android.util.Log


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
        val str =  prefs.getString(key, null)
        return str
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

    fun writeIntOption(context: Context, key: String, value: Int) {
        val editor =
            context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE).edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun readIntOptions(context: Context, key: String, defValue: Int): Int {
        val prefs = context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE)
        return prefs.getInt(key, defValue)
    }

    fun saveList(context: Context, key: String, list: List<String>) {
        val sharedPreferences = context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putStringSet(key, list.toSet())
            .apply()
    }

    fun getList(context: Context, key: String): List<String> {
        val sharedPreferences = context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet(key, emptySet())?.toList() ?: emptyList()
    }



    fun writeFloatOption(context: Context, key: String, value: Float) {
        val editor =
            context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE).edit()
        editor.putFloat(key, value)
        editor.apply()
    }

    fun readFloatOptions(context: Context, key: String, defValue: Float): Float {
        val prefs = context.getSharedPreferences(RADIOCAR_PREFS, Context.MODE_PRIVATE)
        return prefs.getFloat(key, defValue)
    }

}