package com.maestrovs.radiocar.ui.main

import android.content.Context
import com.maestrovs.radiocar.shared_managers.SharedManager

object WeatherManager {

    private val LAST_VOLUME = "LAST_VOLUME"


    public fun setVolume(context: Context, volume: Int) {
        SharedManager.writeIntOption(context, LAST_VOLUME, volume)

    }

    public fun getVolume(context: Context): Int {
        return try {
            SharedManager.readIntOptions(context, LAST_VOLUME, 100)
        } catch (e: Exception) {
            100
        }
    }


}


