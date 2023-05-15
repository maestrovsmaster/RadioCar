package com.maestrovs.radiocar.data.remote

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.maestrovs.radiocar.common.Constants.PAGE_SIZE
import com.maestrovs.radiocar.data.entities.Station
import retrofit2.http.Query
import javax.inject.Inject

class StationRemoteDataSource @Inject constructor(
    //private val stationRemotePagingSource: StationRemotePagingSource,
    private val stationService: StationService,
): BaseDataSource() {

    suspend fun getStations( country: String = "UA",
                            offset: Int = 0,
                            limit: Int = PAGE_SIZE) = getResult { stationService.getStations(offset = offset, limit = limit) }


    /*fun getStationsPaging(): LiveData<PagingData<Station>> {

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                initialLoadSize = 2
            ),
            pagingSourceFactory = {
                stationRemotePagingSource
            }
            , initialKey = 1
        ).liveData
    }*/


}