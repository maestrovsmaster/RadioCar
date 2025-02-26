package com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories

import android.content.Context
import android.util.Log
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.local.radio.FavoritesDao
import com.maestrovs.radiocar.data.local.radio.RecentDao
import com.maestrovs.radiocar.data.local.radio.StationDao
import com.maestrovs.radiocar.data.repository.mapper.toGroupedStations
import com.maestrovs.radiocar.shared_managers.CurrentListTypeManager
import com.maestrovs.radiocar.shared_managers.RecentStationGroupManager
import com.maestrovs.radiocar.ui.radio.ListType
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Created by maestromaster$ on 25/02/2025$.
 */

abstract class SharedPreferencesRepository() {

    abstract fun setListType(listType: ListType)
    abstract fun getListType(): ListType
    abstract fun saveRecentStationGroup(stationGroup: StationGroup)
    abstract suspend fun getRecentStationGroup(): StationGroup?
}

class SharedPreferencesRepositoryIml @Inject constructor(
    @ApplicationContext val context: Context, private val localDataSource: StationDao,
) :
    SharedPreferencesRepository() {

    override fun setListType(listType: ListType) {
        //  val savedList =
        // if (listType == ListType.All || listType == ListType.Searched) ListType.Recent else listType
        CurrentListTypeManager.setListType(context, listType)
    }

    override fun getListType() =
        CurrentListTypeManager.readListType(context)


    override fun saveRecentStationGroup(stationGroup: StationGroup) {
        val ids = stationGroup.streams.map { it.stationUuid }
        if (ids.isNullOrEmpty()) {
            return
        }
        RecentStationGroupManager.saveStationGroupIds(context, ids)
    }

    override suspend fun getRecentStationGroup(): StationGroup? {
        Log.d("RadioViewModel", "getRecentStationGroup")
        val ids = RecentStationGroupManager.getStationGroupIds(context)
        Log.d("RadioViewModel", "getRecentStationGroup: $ids")
        if (ids.isNullOrEmpty()) {
            return null
        }
        val stations = localDataSource.getStationsByIds(ids)
        if (stations.isNullOrEmpty()) {
            return null
        }
        val groups = stations.toGroupedStations()
        if (groups.isNullOrEmpty()) {
            return null
        }
        return groups[0]
    }

}

class SharedPreferencesRepositoryMock : SharedPreferencesRepository() {
    override fun setListType(listType: ListType) {
        TODO("Not yet implemented")
    }

    override fun getListType(): ListType {
        TODO("Not yet implemented")
    }

    override fun saveRecentStationGroup(stationGroup: StationGroup) {
        TODO("Not yet implemented")
    }

    override suspend fun getRecentStationGroup(): StationGroup? {
        TODO("Not yet implemented")
    }


}