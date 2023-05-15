package com.maestrovs.radiocar.ui.radio

import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.utils.Resource
import com.maestrovs.radiocar.utils.combineWith
import kotlinx.coroutines.launch

class RadioViewModel @androidx.hilt.lifecycle.ViewModelInject constructor(
    private val mainRepository: StationRepository,)
: ViewModel() {


    //fun fetchStations() =
     //   mainRepository.getStations()



    private val _fetchStationsTrigger = MutableLiveData<Unit>()
    val stations: LiveData<Resource<List<Station>>> = _fetchStationsTrigger.switchMap {
        mainRepository.getStations()
    }

    init {
        fetchStations()
    }

    fun fetchStations() {
        _fetchStationsTrigger.value = Unit
    }


    private val _fetchRecentTrigger = MutableLiveData<Unit>()
    val recent: LiveData<Resource<List<Station>>> = _fetchRecentTrigger.switchMap {
        mainRepository.getRecentStations()
    }

    fun fetchRecent() {
        _fetchRecentTrigger.value = Unit
    }
    //---

/*
    private val _stations = MutableLiveData<Resource<List<Station>>>()
    val stations: LiveData<Resource<List<Station>>> get() = _stations


    init {
        fetchAllStations()
    }

    fun fetchAllStations() {
        viewModelScope.launch {
            _stations.value =  mainRepository.getStations()
        }
    }

    fun refreshStations() {
        fetchAllStations()
    }


    fun fetchRecent() {
        viewModelScope.launch {
           _stations.value =  mainRepository.getRecentStations().value
        }
    }*/


    /* fun fetchAllStations(): LiveData<Resource<List<Station>>> {
         return mainRepository.getStations()
     }*/

   /* fun getRecent(): LiveData<Resource<List<Station>>> {
        return mainRepository.getRecentStations()
    }


    fun fetchInitList() =
        fetchAllStations().combineWith(getRecent()) { allStations, recentStations ->

            Log.d("Database",">>recentStations ${recentStations?.data?.size}")
            if (recentStations == null) {
                allStations
            } else
                if (recentStations.data.isNullOrEmpty()) {
                    allStations
                } else {
                    recentStations
                }
        }

*/
    /*  val flow = Pager(
          // Configure how data is loaded by passing additional properties to
          // PagingConfig, such as prefetchDistance.
          PagingConfig(pageSize = 20)
      ) {
          stationRemotePagingSource
      }.liveData
       .cachedIn(viewModelScope)
  */



}