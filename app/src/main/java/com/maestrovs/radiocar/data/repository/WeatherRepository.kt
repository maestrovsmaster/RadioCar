package com.maestrovs.radiocar.data.repository

import com.maestrovs.radiocar.data.remote.weather.WeatherRemoteDataSource
import javax.inject.Inject

class WeatherRepository @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,

) {

    suspend fun getWeatherDataByCity(
        cityName: String = "Kyiv",
        lang: String = "ua"
    ) = remoteDataSource.getWeatherDataByCity(cityName, lang)

    suspend fun getWeatherDataByCoordinates(
        lat: Double, lon: Double,
        lang: String = "ua"
    ) = remoteDataSource.getWeatherDataByCoordinates(lat, lon, lang)


}