package com.maestrovs.radiocar.data.remote.radio

import android.util.Log
import com.maestrovs.radiocar.common.Constants.PAGE_SIZE
import com.maestrovs.radiocar.data.remote.base.BaseDataSource
import javax.inject.Inject

class StationRemoteDataSource @Inject constructor(
    private val stationService: StationService,
) : BaseDataSource() {

    suspend fun getStationsExt(//Used for paging
        country: String = "UA",
        name :String,
        tag :String,
        offset: Int = 0,
        limit: Int = PAGE_SIZE
    ) = getResult {
        Log.d("StationRemoteDataSource", "getStationsExt: $country, $name, $tag, $offset, $limit")

        stationService.getStationsExt(country = country, name = name, tag = tag,  offset = offset, limit = limit) }

    suspend fun getStations(
        country: String = "UA",
        offset: Int = 0,
        limit: Int = PAGE_SIZE
    ) = getResult { stationService.getStations(country = country, offset = offset, limit = limit) }

    suspend fun getStationsByName(
        searchterm: String,
        offset: Int = 0,
        limit: Int = PAGE_SIZE
    ) = getResult {
        stationService.getStationsByName(
            searchterm = searchterm,
            offset = offset,
            limit = limit
        )
    }

}