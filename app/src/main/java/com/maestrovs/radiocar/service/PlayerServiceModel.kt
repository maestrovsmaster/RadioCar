package com.maestrovs.radiocar.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maestrovs.radiocar.common.CurrentCountryManager
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.remote.radio.StationRemoteDataSource
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.ui.radio.ListType
import com.maestrovs.radiocar.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerServiceModel @Inject constructor(
    @ApplicationContext private val context: Context,
    // private val remoteDataSource: StationRemoteDataSource
    private val mainRepository: StationRepository,
) {

    //  private val modelScope = CoroutineScope(Dispatchers.IO + Job())

     private var lastStation: Station? = null
    private var listType: ListType = ListType.All

    var allList: List<Station>? = listOf<Station>()


    private var currentStationIndex = 0


    fun getNextStation(): Station? {

        return allList?.let {
            currentStationIndex += 1
            if (currentStationIndex >= it.size) {

                currentStationIndex = 0
            }
            it.get(currentStationIndex)
        } ?: run {
            null
        }
    }

    fun getCurrentStation(): Station?{
        if (currentStationIndex == -1) return null
        return allList?.let {lst ->
            if(lst.isNotEmpty()) {

                if (currentStationIndex >= lst.size) {
                    currentStationIndex = 0
                }
                lst[currentStationIndex]
            }else {
                null
            }
        } ?: run {
            null
        }
    }

    fun getPrevStation(): Station? {

        return allList?.let { list ->
            currentStationIndex -= 1
            if (currentStationIndex < 0) {
                currentStationIndex = list.size - 1
            }
            list.get(currentStationIndex)
        } ?: run {
            null
        }
    }

    suspend fun fetchStations(
        countryCode: String,
        listType: ListType,
        station: Station?,
    ) {
        lastStation = station
        this@PlayerServiceModel.listType = listType

        Log.d("CurrentStationFS", "fetchStations...")


        withContext(Dispatchers.IO) {

            when (listType) {
                ListType.Recent -> {
                    mainRepository.getCombinedRecentAndAllStationsFlow(countryCode).collect {
                        Log.d("CurrentStationFS", "fetchStationsRecent...  ")

                        allList = it
                        Log.d("CurrentStationFS", "station = ${station?.name}")


                        if (!allList.isNullOrEmpty()) {
                            station?.let {  setCurrentItem(it)}


                        }



                    }
                }

                ListType.Favorites -> {
                    mainRepository.getCombinedFavoritesAndAllStationsFlow(countryCode).collect {
                        Log.d("MainActivity22", "fetchStationsFavorites...")

                        allList = it
                        if (!allList.isNullOrEmpty()) {
                            station?.let {  setCurrentItem(it)}

                        }


                    }

                }

                else -> {
                    mainRepository.getStationsFlow(countryCode).collect {
                        when (it.status) {
                            Resource.Status.SUCCESS -> {
                                allList = it.data
                                if (!allList.isNullOrEmpty() && listType == ListType.All) {

                                    station?.let {  setCurrentItem(it)}


                                }

                            }

                            else -> {}
                        }
                    }
                }
            }


        }


    }

    fun setCurrentItem(station: Station){
        allList?.let {lst ->
            for(i in 0..allList!!.size) {
                if(lst[i].name == station.name){
                    currentStationIndex = i
                    break
                }
            }
        }
    }




    fun clear() {
        //   modelScope.cancel()
    }

}




