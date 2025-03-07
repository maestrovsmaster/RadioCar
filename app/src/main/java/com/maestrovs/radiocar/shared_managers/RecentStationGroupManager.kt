package com.maestrovs.radiocar.shared_managers

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.hbb20.countrypicker.models.CPCountry
import com.maestrovs.radiocar.ui.radio.ListType

object RecentStationGroupManager {
    private const val RECENT_STATION_GROUP_IDS = "RECENT_STATION_GROUP_IDS"



    fun saveStationGroupIds(context: Context, groupIds: List<String>)  {
        SharedManager.saveList(context, RECENT_STATION_GROUP_IDS, groupIds)
    }

    fun getStationGroupIds(context: Context): List<String> {

        return SharedManager.getList(context, RECENT_STATION_GROUP_IDS)

    }


}