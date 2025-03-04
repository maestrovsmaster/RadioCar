package com.maestrovs.radiocar.manager.location

/**
 * Created by maestromaster$ on 15/02/2025$.
 */

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.location.Location
import android.util.Log
import com.maestrovs.radiocar.shared_managers.WeatherManager
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object LocationStateManager {




    private var _locationAvailability = MutableStateFlow(false)
    val locationAvailability = _locationAvailability.asStateFlow()

    fun updateLocationAvailability(isAvailable: Boolean) {
        _locationAvailability.value = isAvailable
    }


    private val _locationState = MutableStateFlow(
        LocationState(
            latitude = 0.0,
            longitude = 0.0,
            speed = 0f,
            smoothedSpeed = 0f
        )
    )
    val locationState = _locationState.asStateFlow()

    private val speedHistory = mutableListOf<Float>()

    private const val MAX_HISTORY_SIZE = 3

    private val _locationWeatherState = MutableStateFlow<Location?>(null)
    val locationWeatherState = _locationWeatherState.asStateFlow()
    private var _latestWeatherLocation: Pair<Double, Double>? = null

    fun initLocation(context: Context) {
        val cachedLocation = WeatherManager.getLatestLocation(context)
        if (cachedLocation != null) {
            Log.d("LocationStateManager", "Using cached location: $cachedLocation")
            _locationWeatherState.value = cachedLocation
            _latestWeatherLocation= cachedLocation.latitude to cachedLocation.longitude
        } else {
            val defLocation = DefaultLocations.getDefaultLocation()
            _locationWeatherState.value = defLocation
            Log.d("LocationStateManager", "Using default location: ${_locationWeatherState.value}")
            _latestWeatherLocation= defLocation.latitude to defLocation.longitude
        }
    }

    fun updateLocation(context: Context,location: Location) {
        Log.d("LocationStateManager", "-------updateLocation: , location: $location")
        val newSpeed = location.speed * 3.6f // Convert m/s to km/h
        val smoothedSpeed = getSmoothedSpeed(newSpeed)

        _locationState.value = LocationState(
            latitude = location.latitude,
            longitude = location.longitude,
            speed = newSpeed,
            smoothedSpeed = smoothedSpeed
        )

        if(_locationWeatherState.value == null){
            _locationWeatherState.value = location
        }

        WeatherManager.cacheLatestLocation(context,location)

        // Перевіряємо, чи нова локація змінилася на 5 км
        _latestWeatherLocation?.let { lastLocation ->
            val distance = calculateDistance(
                lastLocation.first, lastLocation.second,
                location.latitude, location.longitude
            )
            Log.d("LocationStateManager", "-------distance: : $distance")
            if (distance >= 5.0) {
                _latestWeatherLocation = location.latitude to location.longitude
                _locationWeatherState.value = location
            }
        } ?: run {
            // Якщо ще не було жодної збереженої локації
            _latestWeatherLocation = location.latitude to location.longitude
        }
    }


    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371 // Радіус Землі в км
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }

    private fun getSmoothedSpeed(newSpeed: Float): Float {
        if (speedHistory.size >= MAX_HISTORY_SIZE) {
            speedHistory.removeAt(0)
        }
        speedHistory.add(newSpeed)

        return (speedHistory.sum() / speedHistory.size).roundToInt().toFloat()
    }
}

data class LocationState(
    val latitude: Double,
    val longitude: Double,
    val speed: Float,
    val smoothedSpeed: Float
)