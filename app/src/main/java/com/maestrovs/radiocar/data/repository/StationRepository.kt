package com.maestrovs.radiocar.data.repository

import androidx.lifecycle.LiveData
import com.maestrovs.radiocar.common.Constants
import com.maestrovs.radiocar.common.Constants.PAGE_SIZE
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.tables.Favorites
import com.maestrovs.radiocar.data.entities.radio.tables.Recent
import com.maestrovs.radiocar.data.local.radio.FavoritesDao
import com.maestrovs.radiocar.data.local.radio.RecentDao
import com.maestrovs.radiocar.data.local.radio.StationDao
import com.maestrovs.radiocar.data.remote.radio.StationRemoteDataSource
import com.maestrovs.radiocar.data.repository.mapper.toGroupedStations
import com.maestrovs.radiocar.ui.radio.ListType
import com.maestrovs.radiocar.utils.Resource
import com.maestrovs.radiocar.utils.performGetOperation
import com.maestrovs.radiocar.utils.performGetOperationFlow
import com.maestrovs.radiocar.utils.performLocalGetOperation
import com.maestrovs.radiocar.utils.performLocalGetOperationFlow
import com.maestrovs.radiocar.utils.performNetworkOperation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject


abstract class StationRepository() {
    abstract fun getStationsByName(searchterm: String): LiveData<Resource<List<Station>>>
    abstract fun getGroupedStationsFlow(
        countryCode: String,
        offset: Int = 0,
        limit: Int = Constants.PAGE_SIZE,
        listType: ListType = ListType.All
    ): Flow<Resource<List<StationGroup>>>

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
    abstract fun getStations(
        countryCode: String,
        offset: Int = 0,
        limit: Int = Constants.PAGE_SIZE
    ): LiveData<Resource<List<Station>>>

    abstract fun getRecentStations(): LiveData<Resource<List<Station>>>
    abstract fun getFavoritesStations(): LiveData<Resource<List<Station>>>
    abstract suspend fun insertStations(list: List<Station>)
    abstract suspend fun setRecent(stationUuids: List<String>)
    abstract suspend fun setFavorite(stationUuids: List<String>)
    abstract suspend fun deleteFavorite(stationUuids: List<String>)
    abstract suspend fun deleteRecent(stationUuids: List<String>)
}

class StationRepositoryIml @Inject constructor(
    private val remoteDataSource: StationRemoteDataSource,
    private val localDataSource: StationDao,
    private val recentSource: RecentDao,
    private val favoritesSource: FavoritesDao
) : StationRepository() {

    suspend fun getPagedStations(
        country: String = "UA",
        searchQuery: String = "",
        tag: String = "",
        offset: Int = 0,
        limit: Int = PAGE_SIZE
    ): List<StationGroup> {
        return try {
            val response = remoteDataSource.getStationsExt(
                country = country,
                name = searchQuery,
                tag = tag,
                offset = offset,
                limit = limit
            )
            if (response.status == Resource.Status.SUCCESS) {
                (response.data ?: emptyList()).toGroupedStations()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun getGroupedStationsFlow(
        countryCode: String,
        offset: Int,
        limit: Int,
        listType: ListType
    ): Flow<Resource<List<StationGroup>>> {
        return performGetOperationFlow(
            databaseQuery = {
                localDataSource.getStationsByCountryWithFavouriteStatusFlow(
                    countryCode
                ).map {
                    it.filter { st ->
                        when (listType) {
                            ListType.All -> true
                            ListType.Recent -> st.isRecent == 1
                            ListType.Favorites -> st.isFavorite == 1
                            ListType.Searched -> true
                        }

                    }.toGroupedStations()

                }.distinctUntilChanged()
            },
            networkCall = { remoteDataSource.getStations(countryCode) },
            saveCallResult = { localDataSource.insertAll(it) }
        )
    }

    override fun getRecentGroupedStationsFlow() = performLocalGetOperationFlow(databaseQuery = {
        localDataSource.getRecentStationsFlow().map { it.toGroupedStations() }
            .distinctUntilChanged()
    })

    override fun getFavoritesGroupedStationsFlow() = performLocalGetOperationFlow(databaseQuery = {
        localDataSource.getFavoritesStationsFlow().map { it.toGroupedStations() }
    })


    override fun getStationsByNameGroup(searchterm: String): Flow<Resource<List<StationGroup>>> {
        return performGetOperationFlow(
            databaseQuery = {
                localDataSource.getStationsByNameFlow(searchterm).map { it.toGroupedStations() }
                    .distinctUntilChanged()
            },
            networkCall = { remoteDataSource.getStationsByName(searchterm) },
            saveCallResult = { localDataSource.insertAll(it) }
        )
    }

    override fun getStationsByName(searchterm: String) = performNetworkOperation(

        networkCall = { remoteDataSource.getStationsByName(searchterm) },
        saveCallResult = { localDataSource.insertAll(it) }
    )


    override fun getStations(
        countryCode: String,
        offset: Int, limit: Int
    ) = performGetOperation(
        databaseQuery = {
            localDataSource.getStationsByCountryCode(countryCode) //getAllStationsWithFavouriteStatus()/
        },
        networkCall = { remoteDataSource.getStations(countryCode, offset, limit) },
        saveCallResult = { localDataSource.insertAll(it) }
    )

    override fun getStationsFlow(countryCode: String) = performGetOperationFlow(
        databaseQuery = {
            localDataSource.getStationsByCountryCodeFlow(countryCode) //getAllStationsWithFavouriteStatus()/
        },
        networkCall = { remoteDataSource.getStations(countryCode) },
        saveCallResult = { localDataSource.insertAll(it) }
    )


    override suspend fun insertStations(list: List<Station>) {

        localDataSource.insertAll(list)

    }

    override fun getRecentStations() = performLocalGetOperation(databaseQuery = {
        localDataSource.getRecentStations()
    })

    fun getRecentStationsFlow() = performLocalGetOperationFlow(databaseQuery = {
        localDataSource.getRecentStationsFlow()
    })


    override fun getFavoritesStations() = performLocalGetOperation(databaseQuery = {
        localDataSource.getFavoritesStations()
    })


    fun getFavoritesStationsFlow(): Flow<Resource<List<Station>>> =
        performLocalGetOperationFlow(databaseQuery = { localDataSource.getFavoritesStationsFlow() })


    override suspend fun setRecent(stationuuid: String) {
        recentSource.insert(Recent(stationuuid = stationuuid))
    }

    override suspend fun setRecent(stationUuids: List<String>) {
        val recentStations = stationUuids.map { Recent(stationuuid = it) }
        recentSource.insertAll(recentStations)
    }

    override suspend fun deleteRecent(stationuuid: String) {
        recentSource.delete(stationuuid)
    }

    override suspend fun deleteRecent(stationUuids: List<String>) {
        recentSource.deleteAllByStationUuids(stationUuids)
    }


    override suspend fun setFavorite(stationuuid: String) {
        favoritesSource.insert(Favorites(stationuuid = stationuuid))
    }

    override suspend fun setFavorite(stationUuids: List<String>) {
        val favoriteStations = stationUuids.map { Favorites(stationuuid = it) }
        favoritesSource.insertAll(favoriteStations)
    }

    override suspend fun deleteFavorite(stationuuid: String) {
        favoritesSource.delete(stationuuid)
    }

    override suspend fun deleteFavorite(stationUuids: List<String>) {
        favoritesSource.deleteAllByStationUuids(stationUuids)
    }


    override fun getCombinedRecentAndAllStationsFlow(countryCode: String): Flow<List<Station>> {
        val recentStationsFlow = localDataSource.getRecentStationsFlow()
        //getRecentStationsFlow()
        val allStationsFlow = getStationsFlow(countryCode)

        // Assuming both flows emit lists of stations
        return combine(recentStationsFlow, allStationsFlow) { recent, all ->
            val combinedList = mutableListOf<Station>()

            // Add all recent stations first
            combinedList.addAll(recent ?: listOf())
            combinedList.addAll(all.data ?: listOf())
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
            combinedList.addAll(favorites ?: listOf())
            combinedList.addAll(all.data ?: listOf())

            combinedList
        }
    }


}