package com.maestrovs.radiocar.data.repository

import android.content.Context
import com.maestrovs.radiocar.data.entities.weather.MainInfo
import com.maestrovs.radiocar.data.entities.weather.Rain
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse
import com.maestrovs.radiocar.data.remote.weather.WeatherRemoteDataSource
import com.maestrovs.radiocar.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

abstract class WeatherRepository
{


    abstract suspend fun getWeatherDataByCoordinates(
        lat: Double, lon: Double,
        lang: String = "ua"
    ): WeatherResponse?

    abstract fun getCachedWeather(): WeatherResponse

    abstract suspend fun getWeatherDataByCity(
        cityName: String = "Kyiv",
        lang: String = "ua"
    ): Resource<WeatherResponse>
}


class WeatherRepositoryIml @Inject constructor(
    @ApplicationContext val context: Context,
    private val remoteDataSource: WeatherRemoteDataSource,
): WeatherRepository() {

    override suspend fun getWeatherDataByCity(
        cityName: String,
        lang: String
    ) = remoteDataSource.getWeatherDataByCity(cityName, lang)

    /*suspend fun getWeatherDataByCoordinates(
        lat: Double, lon: Double,
        lang: String = "ua"
    ) = remoteDataSource.getWeatherDataByCoordinates(lat, lon, lang)*/

    override suspend fun getWeatherDataByCoordinates(lat: Double, lon: Double, lang: String ): WeatherResponse? {
        val weatherResponse = remoteDataSource.getWeatherDataByCoordinates(lat, lon, lang)
        weatherResponse.data?.let{
            com.maestrovs.radiocar.shared_managers.WeatherManager.saveWeatherToCache(context,it)
        }
        return weatherResponse.data
    }

    override fun getCachedWeather(): WeatherResponse {
        return com.maestrovs.radiocar.shared_managers.WeatherManager.getCachedWeather(context)

    }
}


class MockWeatherRepository: WeatherRepository()
{
    override suspend fun getWeatherDataByCoordinates(
        lat: Double,
        lon: Double,
        lang: String
    ): WeatherResponse? {
        return null
    }

    override fun getCachedWeather(): WeatherResponse {
        return WeatherResponse(
            name = "Kyiv",
            main = MainInfo(
                temp = 25.0f,
                humidity = 50
            ),
            weather = listOf(),
            rain = null,
            snow = null



        )
    }

    override suspend fun getWeatherDataByCity(
        cityName: String,
        lang: String
    ): Resource<WeatherResponse> {
       return Resource.success(WeatherResponse(
           name = "Kyiv",
           main = MainInfo(
               temp = 25.0f,
               humidity = 50
           ),
           weather = listOf(),
           rain = Rain(5.0f),
           snow = null



       ))

    }

}