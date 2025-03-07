package com.maestrovs.radiocar.shared_managers

import android.content.Context
import com.google.gson.Gson
import com.hbb20.countrypicker.models.CPCountry

object CurrentCountryManager {
    private const val CURRENT_COUNTRY_KEY = "current_country_key"

    private const val ASK_COUNTRY_KEY = "ASK_COUNTRY_KEY"

    const val DEFAULT_COUNTRY = "GB"
    const val DEFAULT_CITY = "London"

    fun writeCountry(context: Context, country: CPCountry) {
        val countryJson = Gson().toJson(country)

        SharedManager.writeStringOption(context, CURRENT_COUNTRY_KEY, countryJson)
    }

    fun readCountry(context: Context): CPCountry? {
       /* val countryJson = SharedManager.readStringOptions(context, CURRENT_COUNTRY_KEY)

        return if (countryJson == null) {
            null
        } else {
            Gson().fromJson(countryJson, CPCountry::class.java)
        }*/
        return null
    }


    fun writeCountryCode(context: Context, country: String) {
        SharedManager.writeStringOption(context, CURRENT_COUNTRY_KEY, country)
    }

    fun readCountryCode(context: Context) =
        SharedManager.readStringOptions(context, CURRENT_COUNTRY_KEY)


    fun isAskCountry(context: Context) =
        SharedManager.readBooleanOptions(context, ASK_COUNTRY_KEY, false)

    fun setAskedCountryTrue(context: Context) {
        SharedManager.writeBooleanOption(context, ASK_COUNTRY_KEY, true)
    }


    fun getPlaceholderUK(): CPCountry {
        return CPCountry(
            alpha2 = "GB",
            alpha3 = "GBR",
            englishName = "United Kingdom",
            demonym = "British",
            capitalEnglishName = "London",
            areaKM2 = "243610",
            population = 67220000,
            currencyCode = "GBP",
            currencyName = "Pound Sterling",
            currencySymbol = "£",
            cctld = ".uk",
            flagEmoji = "\uD83C\uDDEC\uD83C\uDDE7",
            phoneCode = 44,
            name = "United Kingdom"
        )

    }
}