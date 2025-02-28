package com.maestrovs.radiocar.shared_managers

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hbb20.countrypicker.models.CPCountry
import com.maestrovs.radiocar.ui.radio.ListType

object VolumeManager {
    private const val SAVED_VOLUME_KEY = "SAVED_VOLUME_KEY"

    fun setVolume(context: Context, volume: Int)  {
        SharedManager.writeIntOption(context, SAVED_VOLUME_KEY, volume)
    }

    fun getVolume(context: Context) = SharedManager.readIntOptions(context, SAVED_VOLUME_KEY,80)


}