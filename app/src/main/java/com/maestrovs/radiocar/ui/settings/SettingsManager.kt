package com.maestrovs.radiocar.ui.settings

import android.content.Context
import com.maestrovs.radiocar.common.SharedManager

object SettingsManager {

    private const val IS_DISPLAY_ACTIVE = "IS_DISPLAY_ACTIVE"
    private const val IS_AUTOPLAY = "IS_AUTOPLAY"
    private const val SPEED_UNIT = "SPEED_UNIT"
    private const val TEMPERATURE_UNIT = "TEMPERATURE_UNIT"

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

    fun getSpeedUnit(context: Context): SpeedUnit =
        SpeedUnit.valueOf(SharedManager.readStringOptions(context, SPEED_UNIT) ?: "kmh")

    fun setTemperatureUnit(context: Context, temperatureUnit: TemperatureUnit) {
        SharedManager.writeStringOption(context, TEMPERATURE_UNIT, temperatureUnit.name)
    }

    fun getTemperatureUnit(context: Context): TemperatureUnit =
        TemperatureUnit.valueOf(SharedManager.readStringOptions(context, TEMPERATURE_UNIT) ?: "C")


}

enum class SpeedUnit {
    kmh,
    mph
}

enum class TemperatureUnit {
    C,
    F
}