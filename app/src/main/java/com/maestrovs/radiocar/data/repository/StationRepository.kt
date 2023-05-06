package com.maestrovs.radiocar.data.repository

import com.maestrovs.radiocar.data.local.StationDao
import com.maestrovs.radiocar.data.remote.StationRemoteDataSource
import com.maestrovs.radiocar.utils.performGetOperation
import javax.inject.Inject

class StationRepository @Inject constructor(
    private val remoteDataSource: StationRemoteDataSource,
    private val localDataSource: StationDao
) {



    fun getStations() = performGetOperation(
        databaseQuery = { localDataSource.getAllStations() },
        networkCall = { remoteDataSource.getStations() },
        saveCallResult = { localDataSource.insertAll(it) }
    )
}