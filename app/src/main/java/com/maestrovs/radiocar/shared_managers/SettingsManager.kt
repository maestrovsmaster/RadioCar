package com.maestrovs.radiocar.shared_managers

import android.content.Context
import android.os.Build

object SettingsManager {

    private const val IS_DISPLAY_ACTIVE = "IS_DISPLAY_ACTIVE"
    private const val IS_AUTOPLAY = "IS_AUTOPLAY"
    private const val SPEED_UNIT = "SPEED_UNIT"
    private const val TEMPERATURE_UNIT = "TEMPERATURE_UNIT"

    private val MILE_BASED_COUNTRIES = setOf(
        "US",
        "GB",
        "MM",
        "LR"
    )

    private val FAHRENHEIT_BASED_COUNTRIES = setOf(
        "US",
        "BS",
        "KY",
        "PW"
    )

    private const val SHOW_STATION_NAME_IN_BACKGROUND = "SHOW_STATION_NAME_IN_BACKGROUND"

    fun setDisplayActive(context: Context, isActive: Boolean) {
        SharedManager.writeBooleanOption(context, IS_DISPLAY_ACTIVE, isActive)
    }

    fun isDisplayActive(context: Context) =
        SharedManager.readBooleanOptions(context, IS_DISPLAY_ACTIVE, true)


    fun setAutoplay(context: Context, isAutoplay: Boolean) {
        SharedManager.writeBooleanOption(context, IS_AUTOPLAY, isAutoplay)
    }

    fun isAutoplay(context: Context) =
        SharedManager.readBooleanOptions(context, IS_AUTOPLAY, true)

    fun setSpeedUnit(context: Context, speedUnit: SpeedUnit) {
        SharedManager.writeStringOption(context, SPEED_UNIT, speedUnit.name)
    }

    fun getSpeedUnit(context: Context): SpeedUnit {
        val country = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].country // API 24+
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale.country // API 23
        }

        val defaultUnit = if (country in MILE_BASED_COUNTRIES) SpeedUnit.mph else SpeedUnit.kmh
        return SpeedUnit.valueOf(SharedManager.readStringOptions(context, SPEED_UNIT) ?: defaultUnit.name)
    }


    fun setTemperatureUnit(context: Context, temperatureUnit: TemperatureUnit) {
        SharedManager.writeStringOption(context, TEMPERATURE_UNIT, temperatureUnit.name)
    }

    fun getTemperatureUnit(context: Context): TemperatureUnit {

        val country = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0].country // API 24+
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale.country // API 23
        }

        val defaultUnit = if (country in FAHRENHEIT_BASED_COUNTRIES) TemperatureUnit.F else TemperatureUnit.C
        return TemperatureUnit.valueOf(SharedManager.readStringOptions(context, TEMPERATURE_UNIT) ?: defaultUnit.name)
    }



    fun setShowStationNameInBackground(context: Context, showStation: Boolean) {
        SharedManager.writeBooleanOption(context, SHOW_STATION_NAME_IN_BACKGROUND, showStation)
    }

    fun getShowStationNameInBackground(context: Context) =
        SharedManager.readBooleanOptions(context, SHOW_STATION_NAME_IN_BACKGROUND, false)


}

enum class SpeedUnit {
    kmh,
    mph
}

enum class TemperatureUnit {
    C,
    F
}