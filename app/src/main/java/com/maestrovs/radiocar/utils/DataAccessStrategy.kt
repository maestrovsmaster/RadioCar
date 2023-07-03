package com.maestrovs.radiocar.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.maestrovs.radiocar.utils.Resource.Status.*
import kotlinx.coroutines.Dispatchers

fun <T, A> performGetOperation(
    databaseQuery: () -> LiveData<T>,
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Resource<T>> =
    liveData(Dispatchers.IO) {
        Log.d("RequestResult", "emit...")
        emit(Resource.loading())
        val source = databaseQuery.invoke().map { Resource.success(it) }
        emitSource(source)

        val responseStatus = networkCall.invoke()
        if (responseStatus.status == SUCCESS) {
            saveCallResult(responseStatus.data!!)

        } else if (responseStatus.status == ERROR) {
            // emit(Resource.error(responseStatus.message!!))
            //  emitSource(source)
        }
    }

fun <A> performNetworkOperation(
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Resource<A>> =
    liveData(Dispatchers.IO) {

        val resources = networkCall.invoke()
        emit(resources)

        if (resources.status == SUCCESS) {
            saveCallResult(resources.data!!)

        } else if (resources.status == ERROR) {
            // emit(Resource.error(responseStatus.message!!))
            //  emitSource(source)
        }
    }


fun <T> performLocalGetOperation(databaseQuery: () -> LiveData<T>): LiveData<Resource<T>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val source = databaseQuery.invoke().map { Resource.success(it) }
        emitSource(source)
    }


fun performLocalSetOperation(databaseQuery: () -> (Unit)): LiveData<Resource<Unit>> =
    liveData(Dispatchers.IO) {
        Log.d("Database", ">>recentStations setRecent----")
        emit(Resource.loading())
        databaseQuery.invoke()
        emit(Resource.success(Unit))
    }