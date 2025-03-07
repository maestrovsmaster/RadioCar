package com.maestrovs.radiocar.utils

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.maestrovs.radiocar.utils.Resource.Status.ERROR
import com.maestrovs.radiocar.utils.Resource.Status.SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

fun <T, A> performGetOperation(
    databaseQuery: () -> LiveData<T>,
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): LiveData<Resource<T>> =
    liveData(Dispatchers.IO) {
        emit(Resource.loading())
        val source = databaseQuery.invoke().map { Resource.success(it) }
        emitSource(source)

        val responseStatus = networkCall.invoke()
        if (responseStatus.status == SUCCESS) {
            saveCallResult(responseStatus.data!!)

        } else if (responseStatus.status == ERROR) {
             emit(Resource.error(responseStatus.message!!))
            //  emitSource(source)
        }
    }

fun <T, A> performGetOperationFlow(
    databaseQuery: () -> Flow<T>,
    networkCall: suspend () -> Resource<A>,
    saveCallResult: suspend (A) -> Unit
): Flow<Resource<T>> = flow {
    emit(Resource.loading<T>()) // Emit loading state
    Log.d("StationRepositoryIml", "getGroupedStationsFlow... 1c")
    // Emit database items initially.
    val initialData = databaseQuery.invoke().first()
    Log.d("StationRepositoryIml", "getGroupedStationsFlow... 1cc")
    emit(Resource.success(initialData))

    // Perform the network call.
    try {
        Log.d("StationRepositoryIml", "getGroupedStationsFlow... 1")
        val responseStatus = networkCall.invoke()
        Log.d("StationRepositoryIml", "getGroupedStationsFlow /....2 $responseStatus")

        if (responseStatus.status == SUCCESS) {
            Log.d("StationRepositoryIml", "getGroupedStationsFlow... succcc")
            // Save the result of the call and fetch & emit from DB again.
            responseStatus.data?.let { data ->
                Log.d("StationRepositoryIml", "getGroupedStationsFlowrr... 2succ2 $data")
                saveCallResult(data)
                // Emit new data from the database after saving.
                Log.d("StationRepositoryIml", "getGroupedStationsFlow... 3")
                databaseQuery.invoke().collect { newlyFetchedData ->
                    Log.d("StationRepositoryIml", "getGroupedStationsFlow... 4 newlyFetchedData = $newlyFetchedData")
                    emit(Resource.success(newlyFetchedData))
                }

            }
        } else if (responseStatus.status == ERROR) {
            emit(Resource.error<T>(responseStatus.message ?: "Unknown Error"))
        }
    } catch (e: Exception) {
        emit(Resource.error<T>("Network call failed: ${e.message}"))
    }
}.flowOn(Dispatchers.IO)

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
        emit(Resource.loading())
        databaseQuery.invoke()
        emit(Resource.success(Unit))
    }


fun <T> performLocalGetOperationFlow(databaseQuery: () -> Flow<T>): Flow<Resource<T>> =
    flow {
        emit(Resource.loading())
        try {
            databaseQuery.invoke().collect { data ->
                emit(Resource.success(data))
            }
        } catch (e: Exception) {
            emit(Resource.error(e.localizedMessage ?: "An error occurred"))
        }
    }.flowOn(Dispatchers.IO) // Perform the operation in the IO context