package com.maestrovs.radiocar.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.radio.tables.Favorites
import com.maestrovs.radiocar.data.entities.radio.tables.Recent
import com.maestrovs.radiocar.data.local.radio.FavoritesDao
import com.maestrovs.radiocar.data.local.radio.RecentDao
import com.maestrovs.radiocar.data.local.radio.StationDao
import com.maestrovs.radiocar.data.remote.radio.StationRemoteDataSource
import com.maestrovs.radiocar.utils.Resource
import com.maestrovs.radiocar.utils.performGetOperation
import com.maestrovs.radiocar.utils.performLocalGetOperation
import com.maestrovs.radiocar.utils.performNetworkOperation
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import androidx.lifecycle.liveData
import com.maestrovs.radiocar.common.Constants
import com.maestrovs.radiocar.utils.performGetOperationFlow
import com.maestrovs.radiocar.utils.performLocalGetOperationFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class StationRepository @Inject constructor(
    private val remoteDataSource: StationRemoteDataSource,
    private val localDataSource: StationDao,
    private val recentSource: RecentDao,
    private val favoritesSource: FavoritesDao
) {


    fun getStations(countryCode: String,
                    offset: Int = 0,limit: Int = Constants.PAGE_SIZE
    ) = performGetOperation(
        databaseQuery = { localDataSource.getStationsByCountryCode(countryCode,) //getAllStationsWithFavouriteStatus()/
                        },
        networkCall = { remoteDataSource.getStations(countryCode, offset, limit) },
        saveCallResult = { localDataSource.insertAll(it) }
    )

    fun getStationsFlow(countryCode: String) = performGetOperationFlow(
        databaseQuery = { localDataSource.getStationsByCountryCodeFlow(countryCode) //getAllStationsWithFavouriteStatus()/
        },
        networkCall = { remoteDataSource.getStations(countryCode) },
        saveCallResult = { localDataSource.insertAll(it) }
    )


    fun getStationsByName(searchterm: String) = performNetworkOperation(

        networkCall = { remoteDataSource.getStationsByName(searchterm) },
        saveCallResult = { localDataSource.insertAll(it) }
    )

    suspend fun insertStations(list: List<Station>){

       localDataSource.insertAll(list)

    }

    fun getRecentStations() = performLocalGetOperation(databaseQuery = {
        localDataSource.getRecentStations() })

    fun getRecentStationsFlow() = performLocalGetOperationFlow(databaseQuery = {
        localDataSource.getRecentStationsFlow() })

    fun getFavoritesStations() = performLocalGetOperation(databaseQuery = {
        localDataSource.getFavoritesStations() })

    fun getFavoritesStationsFlow(): Flow<Resource<List<Station>>> =
        performLocalGetOperationFlow(databaseQuery = { localDataSource.getFavoritesStationsFlow() })


    suspend fun setRecent(stationuuid: String) {
        recentSource.insert(Recent(stationuuid = stationuuid))
    }

    suspend fun deleteRecent(stationuuid: String) {
        recentSource.delete( stationuuid)
    }


    suspend fun setFavorite(stationuuid: String) {
        favoritesSource.insert(Favorites(stationuuid = stationuuid))
    }

    suspend fun deleteFavorite(stationuuid: String) {
        favoritesSource.delete(stationuuid)
    }



    fun getCombinedRecentAndAllStationsFlow(countryCode: String): Flow<List<Station>> {
        val recentStationsFlow = localDataSource.getRecentStationsFlow()
            //getRecentStationsFlow()
        val allStationsFlow = getStationsFlow(countryCode)

        // Assuming both flows emit lists of stations
        return combine(recentStationsFlow, allStationsFlow) { recent, all ->
            val combinedList = mutableListOf<Station>()

            // Add all recent stations first
            combinedList.addAll(recent?: listOf(), )
            combinedList.addAll(all.data?: listOf(), )
            // Then add all stations, avoiding duplicates
          //  combinedList.addAll(all.filter { station ->
           //     station.id !in recent.map { it.id }
          //  })

            combinedList
        }
    }

    fun getCombinedFavoritesAndAllStationsFlow(countryCode: String): Flow<List<Station>> {
        val favoritesStationsFlow = localDataSource.getFavoritesStationsFlow()
            //getFavoritesStationsFlow()
        val allStationsFlow = getStationsFlow(countryCode)

        // Assuming both flows emit lists of stations
        return combine(favoritesStationsFlow, allStationsFlow) { favorites, all ->
            val combinedList = mutableListOf<Station>()

            // Add all recent stations first
            combinedList.addAll(favorites?: listOf())
            combinedList.addAll(all.data?: listOf(), )

            combinedList
        }
    }


}