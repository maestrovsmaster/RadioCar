package com.maestrovs.radiocar.data.remote.weather

import com.maestrovs.radiocar.BuildConfig
import com.maestrovs.radiocar.data.remote.base.BaseDataSource
import com.maestrovs.radiocar.utils.getApiKeyFromLocalProperties
import retrofit2.Response
import javax.inject.Inject

class WeatherRemoteDataSource @Inject constructor(
    private val stationService: WeatherService,
) : BaseDataSource() {

    suspend fun getWeatherDataByCity(
        cityName: String = "Kyiv", lang: String = "ua"
    ) = getResult {
        stationService.getCurrentWeather(
            cityName,
            "metric",
            lang,
             BuildConfig.WEATHER_API_KEY
        )


    }


    suspend fun getWeatherDataByCoordinates(
        lat: Double, lon: Double, lang: String = "ua"
    ) = getResult {
        stationService.getCurrentWeatherByCoordinates(
            lat,
            lon,
            "metric",
            lang,
            BuildConfig.WEATHER_API_KEY
        )
    }

}