package com.maestrovs.radiocar.manager.location

/**
 * Created by maestromaster$ on 15/02/2025$.
 */

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.location.Location
import kotlin.math.roundToInt

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

    fun updateLocation(location: Location) {
        val newSpeed = location.speed * 3.6f // Convert m/s to km/h
        val smoothedSpeed = getSmoothedSpeed(newSpeed)

        _locationState.value = LocationState(
            latitude = location.latitude,
            longitude = location.longitude,
            speed = newSpeed,
            smoothedSpeed = smoothedSpeed
        )
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