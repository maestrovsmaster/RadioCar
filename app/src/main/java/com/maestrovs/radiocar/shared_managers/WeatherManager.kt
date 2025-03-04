package com.maestrovs.radiocar.shared_managers

import android.content.Context
import android.location.Location
import com.maestrovs.radiocar.data.entities.weather.MainInfo
import com.maestrovs.radiocar.data.entities.weather.Weather
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse

/**
 * Created by maestromaster$ on 02/03/2025$.
 */

object WeatherManager {

    private const val PREFS_NAME = "weather_prefs"
    private const val KEY_LAST_LAT = "last_latitude"
    private const val KEY_LAST_LON = "last_longitude"

    fun cacheLatestLocation(context: Context, location: Location) {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putFloat(KEY_LAST_LAT, location.latitude.toFloat())
            .putFloat(KEY_LAST_LON, location.longitude.toFloat())
            .apply()
    }

    fun getLatestLocation(context: Context): Location? {
        val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lat = sharedPrefs.getFloat(KEY_LAST_LAT, 0f).toDouble()
        val lon = sharedPrefs.getFloat(KEY_LAST_LON, 0f).toDouble()

        return if (lat != 0.0 && lon != 0.0) {
            Location("").apply {
                latitude = lat
                longitude = lon
            }
        } else null
    }



     fun saveWeatherToCache(context: Context,weather: WeatherResponse) {

        SharedManager.writeStringOption(context,"city", weather.name)
        SharedManager.writeFloatOption(context,"temp", weather.main.temp)
        SharedManager.writeIntOption(context,"humidity", weather.main.humidity)
        SharedManager.writeStringOption(context,"condition", weather.weather.firstOrNull()?.description ?: "Unknown")
        SharedManager.writeStringOption(context,"icon", weather.weather.firstOrNull()?.icon ?: "01d")

    }

    fun getCachedWeather(context: Context): WeatherResponse {
        return WeatherResponse(
            name = SharedManager.readStringOptions(context,"city") ?: "Unknown",
            main = MainInfo(
                temp = SharedManager.readFloatOptions(context,"temp",0.0f) ?: 0.0f,
                humidity = SharedManager.readIntOptions(context,"humidity",0) ?: 0
            ),
            weather = listOf(
                Weather(
                    id = 0,
                    description = SharedManager.readStringOptions(context,"condition") ?: "Unknown",
                    icon = SharedManager.readStringOptions(context,"icon") ?: "01d"
                )
            ),
            rain = null,
            snow = null
        )
    }
}