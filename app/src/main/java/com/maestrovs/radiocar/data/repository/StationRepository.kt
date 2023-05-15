package com.maestrovs.radiocar.data.repository

import android.util.Log
import androidx.lifecycle.liveData
import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.data.entities.tables.Favorites
import com.maestrovs.radiocar.data.entities.tables.Recent
import com.maestrovs.radiocar.data.local.FavoritesDao
import com.maestrovs.radiocar.data.local.RecentDao
import com.maestrovs.radiocar.data.local.StationDao
import com.maestrovs.radiocar.data.remote.StationRemoteDataSource
import com.maestrovs.radiocar.utils.Resource
import com.maestrovs.radiocar.utils.performGetOperation
import com.maestrovs.radiocar.utils.performLocalGetOperation
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class StationRepository @Inject constructor(
    private val remoteDataSource: StationRemoteDataSource,
    private val localDataSource: StationDao,
    private val recentSource: RecentDao,
    private val favoritesSource: FavoritesDao
) {


    fun getStations() = performGetOperation(
        databaseQuery = { localDataSource.getAllStations() },
        networkCall = { remoteDataSource.getStations() },
        saveCallResult = { localDataSource.insertAll(it) }
    )



    fun getStations2() =
        liveData<Resource<List<Station>>>(Dispatchers.IO) {remoteDataSource.getStations() }



    fun getRecentStations() = performLocalGetOperation(databaseQuery = {
        localDataSource.getRecentStations() })

    fun getFavoritesStations() = performLocalGetOperation(databaseQuery = {
        localDataSource.getFavoritesStations() })


    suspend fun setRecent(stationuuid: String) {
        recentSource.insert(Recent(stationuuid))
    }

    suspend fun deleteRecent(stationuuid: String) {
        recentSource.delete(Recent(stationuuid))
    }


    suspend fun setFavorite(stationuuid: String) {
        favoritesSource.insert(Favorites(stationuuid))
    }

    suspend fun deleteFavorite(stationuuid: String) {
        favoritesSource.delete(Favorites(stationuuid))
    }





}