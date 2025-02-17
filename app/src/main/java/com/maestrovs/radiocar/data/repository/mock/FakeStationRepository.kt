package com.maestrovs.radiocar.data.repository.mock

import androidx.lifecycle.LiveData
import com.maestrovs.radiocar.data.entities.radio.BitrateOption
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream
import com.maestrovs.radiocar.data.repository.StationRepository
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

    override fun getGroupedStationsFlow(countryCode: String, offset: Int, limit: Int): Flow<Resource<List<StationGroup>>> {
        return flow {
            emit(Resource.loading())
            delay(500) // Симулюємо затримку
            emit(Resource.success(listOf(
                StationGroup(
                    name = "Mock Station 1",
                    streams = listOf(StationStream(url = "http://mock1.com", bitrate = BitrateOption.LOW)),
                    favicon = "https://mock1.com/favicon.png"
                ),
                StationGroup(
                    name = "Mock Station 2",
                    streams = listOf(StationStream(url = "http://mock2.com", bitrate = BitrateOption.HD)),
                    favicon = "https://mock2.com/favicon.png"
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

    override suspend fun deleteRecent(stationuuid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun setFavorite(stationuuid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavorite(stationuuid: String) {
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
}
