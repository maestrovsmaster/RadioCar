package com.maestrovs.radiocar.data.remote.weather

import android.content.Context
import com.maestrovs.radiocar.shared_managers.SettingsManager
import com.maestrovs.radiocar.shared_managers.TemperatureUnit

/**
 * Created by maestromaster$ on 04/03/2025$.
 */

fun convertCelsiumToFahrenheit(celsium: Float) = ((celsium*(9.0 / 5.0))+32)

fun getTemperatureWithUnit(context: Context, temperature: Float): Pair<Float, TemperatureUnit>{

    val unit = SettingsManager.getTemperatureUnit(context)
    return  when (unit) {
        TemperatureUnit.C -> Pair(temperature, TemperatureUnit.C)
        TemperatureUnit.F -> Pair(convertCelsiumToFahrenheit(temperature).toFloat(), TemperatureUnit.F)
    }
}