package com.maestrovs.radiocar.data.repository.mock

import androidx.lifecycle.LiveData
import com.maestrovs.radiocar.data.entities.radio.BitrateOption
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream
import com.maestrovs.radiocar.data.entities.radio.tables.Favorites
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.ui.radio.ListType
import com.maestrovs.radiocar.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created by maestromaster$ on 11/02/2025$.
 */

class FakeStationRepository : StationRepository() {
    override fun getStationsByName(searchterm: String): LiveData<Resource<List<Station>>> {
        TODO("Not yet implemented")
    }

    override fun getGroupedStationsFlow(
        countryCode: String,
        offset: Int,
        limit: Int,
        listType: ListType
    ): Flow<Resource<List<StationGroup>>> {
        return flow {
            emit(Resource.loading())
            delay(500) // Симулюємо затримку
            emit(Resource.success(listOf(
                StationGroup(
                    name = "Mock Station 1",
                    streams = listOf(StationStream(stationUuid = "124", url = "http://mock1.com", bitrate = BitrateOption.LOW)),
                    favicon = "https://mock1.com/favicon.png",
                    isFavorite = true
                ),
                StationGroup(
                    name = "Mock Station 2",
                    streams = listOf(StationStream(stationUuid = "123", url = "http://mock2.com", bitrate = BitrateOption.HD)),
                    favicon = "https://mock2.com/favicon.png",
                    isFavorite = false
                )
            )))
        }
    }

    override fun getStationsByNameGroup(searchterm: String): Flow<Resource<List<StationGroup>>> {
        TODO("Not yet implemented")
    }

    override fun getRecentGroupedStationsFlow(): Flow<Resource<List<StationGroup>>> {
        TODO("Not yet implemented")
    }

    override fun getFavoritesGroupedStationsFlow(): Flow<Resource<List<StationGroup>>> {
        TODO("Not yet implemented")
    }

    override fun getStationsFlow(countryCode: String): Flow<Resource<List<Station>>> {
        TODO("Not yet implemented")
    }

    override fun getCombinedRecentAndAllStationsFlow(countryCode: String): Flow<List<Station>> {
        TODO("Not yet implemented")
    }

    override fun getCombinedFavoritesAndAllStationsFlow(countryCode: String): Flow<List<Station>> {
        TODO("Not yet implemented")
    }

    override suspend fun setRecent(stationuuid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setRecent(stationUuids: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecent(stationuuid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteRecent(stationUuids: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun setFavorite(stationuuid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setFavorite(stationUuids: List<String>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavorite(stationuuid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavorite(stationUuids: List<String>) {
        TODO("Not yet implemented")
    }

    override fun getStations(
        countryCode: String,
        offset: Int,
        limit: Int
    ): LiveData<Resource<List<Station>>> {
        TODO("Not yet implemented")
    }

    override fun getRecentStations(): LiveData<Resource<List<Station>>> {
        TODO("Not yet implemented")
    }

    override fun getFavoritesStations(): LiveData<Resource<List<Station>>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertStations(list: List<Station>) {
        TODO("Not yet implemented")
    }

    override fun getFavoriteStationIdsFlow(): Flow<List<Favorites>> {
        return flow {
            emit(
                listOf(
                    Favorites(stationuuid = "station_1"),
                    Favorites(stationuuid = "station_2"),
                    Favorites(stationuuid = "station_3")
                )
            )
        }
    }

    override suspend fun getPagedStations(
        country: String,
        searchQuery: String,
        tag: String,
        offset: Int,
        limit: Int
    ): List<StationGroup> {
        TODO("Not yet implemented")
    }
}
