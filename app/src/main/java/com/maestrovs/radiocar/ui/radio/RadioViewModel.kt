package com.maestrovs.radiocar.ui.radio

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.utils.Resource

class RadioViewModel @androidx.hilt.lifecycle.ViewModelInject constructor(
    private val mainRepository: StationRepository,)
: ViewModel() {



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




    private val _fetchFavoritesTrigger = MutableLiveData<Unit>()
    val favorites: LiveData<Resource<List<Station>>> = _fetchFavoritesTrigger.switchMap {
        mainRepository.getFavoritesStations()
    }

    fun fetchFavorites() {
        _fetchFavoritesTrigger.value = Unit
    }

}