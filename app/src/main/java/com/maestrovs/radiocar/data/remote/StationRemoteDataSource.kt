package com.maestrovs.radiocar.data.remote

import retrofit2.http.Query
import javax.inject.Inject

class StationRemoteDataSource @Inject constructor(
    private val stationService: StationService
): BaseDataSource() {

    suspend fun getStations( country: String = "UA",
                            offset: Int = 0,
                            limit: Int = 200) = getResult { stationService.getStations(offset = offset, limit = limit) }

   // suspend fun getCharacter(id: Int) = getResult { stationService.getCharacter(id) }
}