package com.maestrovs.radiocar.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.local.radio.StationDao
import com.maestrovs.radiocar.data.remote.radio.StationService
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class StationRemoteMediator(
    private val countryCode: String,
    private val stationService: StationService,
    private val stationDao: StationDao
) : RemoteMediator<Int, Station>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Station>): MediatorResult {

        val medResult = try {
            // The index of the page to load data from.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.let {1// it.id
                } ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
            Log.d("PagingPage","StationRemoteMediator loadKey = $loadKey")
            // Get the data from the API.
           // val response = stationService.getStations1(country = countryCode, offset = loadKey ?: 0, state.config.pageSize)

            //Log.d("PagingPage","response = $response")

            // Save the data to the database.
          //  stationDao.insertAll(response)
            Log.d("PagingPage","insertAlle")
            MediatorResult.Success(endOfPaginationReached = false)

        } catch (exception: IOException) {
            Log.d("PagingPage","exception1  = $exception")
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            Log.d("PagingPage","exception2  = $exception")
            MediatorResult.Error(exception)
        }
        Log.d("PagingPage","medResult $medResult")
        return medResult
    }
}