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
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.repository.mapper.toGroupedStations
import com.maestrovs.radiocar.utils.performGetOperationFlow
import com.maestrovs.radiocar.utils.performLocalGetOperationFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


abstract class StationRepository(){
    abstract fun getStationsByName(searchterm: String):  LiveData<Resource<List<Station>>>
    abstract fun getGroupedStationsFlow(countryCode: String, offset: Int = 0, limit: Int = Constants.PAGE_SIZE): Flow<Resource<List<StationGroup>>>
    abstract fun getStationsByNameGroup(searchterm: String): Flow<Resource<List<StationGroup>>>
    abstract fun getRecentGroupedStationsFlow(): Flow<Resource<List<StationGroup>>>
    abstract fun getFavoritesGroupedStationsFlow(): Flow<Resource<List<StationGroup>>>
    abstract fun getStationsFlow(countryCode: String): Flow<Resource<List<Station>>>
    abstract fun getCombinedRecentAndAllStationsFlow(countryCode: String): Flow<List<Station>>
    abstract fun getCombinedFavoritesAndAllStationsFlow(countryCode: String): Flow<List<Station>>
    abstract suspend fun setRecent(stationuuid: String)
    abstract suspend fun deleteRecent(stationuuid: String)
    abstract suspend fun setFavorite(stationuuid: String)
    abstract suspend fun deleteFavorite(stationuuid: String)
    abstract fun getStations(countryCode: String, offset: Int = 0,limit: Int = Constants.PAGE_SIZE): LiveData<Resource<List<Station>>>
    abstract fun getRecentStations(): LiveData<Resource<List<Station>>>
    abstract fun getFavoritesStations(): LiveData<Resource<List<Station>>>
    abstract suspend fun insertStations(list: List<Station>)
}

 class StationRepositoryIml @Inject constructor(
    private val remoteDataSource: StationRemoteDataSource,
    private val localDataSource: StationDao,
    private val recentSource: RecentDao,
    private val favoritesSource: FavoritesDao
) : StationRepository() {

    override fun getGroupedStationsFlow(countryCode: String, offset: Int, limit: Int): Flow<Resource<List<StationGroup>>> {
        return performGetOperationFlow(
            databaseQuery = { localDataSource.getStationsByCountryCodeFlow(countryCode).map { it.toGroupedStations() } },
            networkCall = { remoteDataSource.getStations(countryCode) },
            saveCallResult = { localDataSource.insertAll(it) }
        )
    }

     override fun getStationsByNameGroup(searchterm: String): Flow<Resource<List<StationGroup>>> {
         return performGetOperationFlow(
             databaseQuery = { localDataSource.getStationsByNameFlow(searchterm).map { it.toGroupedStations() } },
             networkCall = { remoteDataSource.getStationsByName(searchterm) },
             saveCallResult = { localDataSource.insertAll(it) }
         )
     }

     override fun getStationsByName(searchterm: String) = performNetworkOperation(

         networkCall = { remoteDataSource.getStationsByName(searchterm) },
         saveCallResult = { localDataSource.insertAll(it) }
     )



     override fun getStations(countryCode: String,
                    offset: Int ,limit: Int
    ) = performGetOperation(
        databaseQuery = { localDataSource.getStationsByCountryCode(countryCode,) //getAllStationsWithFavouriteStatus()/
                        },
        networkCall = { remoteDataSource.getStations(countryCode, offset, limit) },
        saveCallResult = { localDataSource.insertAll(it) }
    )

     override  fun getStationsFlow(countryCode: String) = performGetOperationFlow(
        databaseQuery = { localDataSource.getStationsByCountryCodeFlow(countryCode) //getAllStationsWithFavouriteStatus()/
        },
        networkCall = { remoteDataSource.getStations(countryCode) },
        saveCallResult = { localDataSource.insertAll(it) }
    )




     override suspend fun insertStations(list: List<Station>){

       localDataSource.insertAll(list)

    }

     override fun getRecentStations() = performLocalGetOperation(databaseQuery = {
        localDataSource.getRecentStations() })

    fun getRecentStationsFlow() = performLocalGetOperationFlow(databaseQuery = {
        localDataSource.getRecentStationsFlow() })

     override fun getRecentGroupedStationsFlow() = performLocalGetOperationFlow(databaseQuery = {
         localDataSource.getRecentStationsFlow().map { it.toGroupedStations() }  })

     override fun getFavoritesStations() = performLocalGetOperation(databaseQuery = {
        localDataSource.getFavoritesStations() })

     override fun getFavoritesGroupedStationsFlow() = performLocalGetOperationFlow(databaseQuery = {
         localDataSource.getRecentStationsFlow().map { it.toGroupedStations() }  })

    fun getFavoritesStationsFlow(): Flow<Resource<List<Station>>> =
        performLocalGetOperationFlow(databaseQuery = { localDataSource.getFavoritesStationsFlow() })


     override suspend fun setRecent(stationuuid: String) {
        recentSource.insert(Recent(stationuuid = stationuuid))
    }

     override suspend fun deleteRecent(stationuuid: String) {
        recentSource.delete( stationuuid)
    }


     override suspend fun setFavorite(stationuuid: String) {
        favoritesSource.insert(Favorites(stationuuid = stationuuid))
    }

     override suspend fun deleteFavorite(stationuuid: String) {
        favoritesSource.delete(stationuuid)
    }



     override fun getCombinedRecentAndAllStationsFlow(countryCode: String): Flow<List<Station>> {
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

     override fun getCombinedFavoritesAndAllStationsFlow(countryCode: String): Flow<List<Station>> {
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