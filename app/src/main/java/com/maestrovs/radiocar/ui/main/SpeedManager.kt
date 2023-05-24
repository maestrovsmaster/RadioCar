package com.maestrovs.radiocar.ui.main

import android.location.Location
import android.util.Log
import kotlin.math.round

object SpeedManager {


    private val cacheSize = 5

    private var arrSpeed = mutableListOf<Float>(
        0f, 0f, 0f, 0f, 0f,
    )


    fun getSmoothSpeed(newSpeed: Float): Float {


        Log.d("CustomSpeed", "array in = $arrSpeed")
        Log.d("CustomSpeed", ">>>>in Speed = $newSpeed")


        val mediumSpeed = (arrSpeed.sum()) / (cacheSize)
        val newMediumSpeed = (arrSpeed.sum() + newSpeed) / (cacheSize + 1)

        var addedValue = 0f

        if (mediumSpeed < 1 && newSpeed < 9f) {
            addedValue = 0f
        } else {
            addedValue = newSpeed
        }

        arrSpeed.removeAt(0)
        arrSpeed.add(newSpeed)

        Log.d("CustomSpeed", "array out = $arrSpeed ")
        Log.d("CustomSpeed", "====out Speed = ${round(newMediumSpeed)}  \n\n")
        Log.d("CustomSpeed", "--------------")

        return newMediumSpeed
    }

    fun updateSpeed(location: Location): Float {


        val speed = location.speed // Speed in meters per second


        val speedKmPerHour = (speed * 3.6).toFloat() // Convert to km/h


        val mediumSpeed = getSmoothSpeed(speedKmPerHour)

        return mediumSpeed

    }


}