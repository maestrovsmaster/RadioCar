package com.maestrovs.radiocar.common

import android.content.Context
import com.google.gson.Gson
import com.hbb20.countrypicker.models.CPCountry

object CurrentCountryManager {
    private const val CURRENT_COUNTRY_KEY = "current_country_key"

    private const val ASK_COUNTRY_KEY = "ASK_COUNTRY_KEY"

    const val DEFAULT_COUNTRY = "UK"

    fun writeCountry(context: Context, country: CPCountry) {
        val countryJson = Gson().toJson(country)

        SharedManager.writeStringOption(context, CURRENT_COUNTRY_KEY, countryJson)
    }

    fun readCountry(context: Context): CPCountry? {
        val countryJson = SharedManager.readStringOptions(context, CURRENT_COUNTRY_KEY)

        return if (countryJson == null) {
            null
        } else {
            Gson().fromJson(countryJson, CPCountry::class.java)
        }
    }

    fun isAskCountry(context: Context) = SharedManager.readBooleanOptions(context,ASK_COUNTRY_KEY,false)

    fun setAskedCountryTrue(context: Context){
        SharedManager.writeBooleanOption(context, ASK_COUNTRY_KEY, true)
    }
}