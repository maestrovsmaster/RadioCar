package com.maestrovs.radiocar.ui.radio

import android.util.Log
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



    private val _fetchStationsTrigger = MutableLiveData<String>()
    val stations: LiveData<Resource<List<Station>>> = _fetchStationsTrigger.switchMap {countryCode->

        if(countryCode == "RU"){
            mainRepository.getStationsByName("байрактар")
        }else {
            mainRepository.getStations(countryCode)
        }
    }

    init {
       // fetchStations("sdf")
    }

    fun fetchStations(countryCode: String) {
        _fetchStationsTrigger.value = countryCode
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

    private val _searchStationsTrigger = MutableLiveData<String>()
    val searched: LiveData<Resource<List<Station>>> = _searchStationsTrigger.switchMap {
        Log.d("Searched","Searched 0  $it")
        mainRepository.getStationsByName(it)

       // mainRepository.getFavoritesStations()

    }


    fun searchStations(term:String) {
        //mainRepository.getStationsByName(term)
        _searchStationsTrigger.value = term
    }

}