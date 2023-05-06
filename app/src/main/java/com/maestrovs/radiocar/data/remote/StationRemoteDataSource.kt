package com.maestrovs.radiocar.data.remote

import javax.inject.Inject

class StationRemoteDataSource @Inject constructor(
    private val stationService: StationService
): BaseDataSource() {

    suspend fun getStations() = getResult { stationService.getStations() }
   // suspend fun getCharacter(id: Int) = getResult { stationService.getCharacter(id) }
}