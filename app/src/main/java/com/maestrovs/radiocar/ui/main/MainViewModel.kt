package com.maestrovs.radiocar.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.service.AudioPlayerService
import com.maestrovs.radiocar.service.PlayUrlEvent
import com.maestrovs.radiocar.utils.Resource
import org.greenrobot.eventbus.EventBus


class MainViewModel @androidx.hilt.lifecycle.ViewModelInject constructor(
    private val mainRepository: StationRepository,
) : ViewModel() {



    private fun playUrl(url: String) {
        EventBus.getDefault().post(PlayUrlEvent(url))
    }



    private var _selectedStation =  MutableLiveData<Station?>(null).apply {
        value = null
    }
    val selectedStation: LiveData<Station?> = _selectedStation

    fun setStation(station: Station?){
        _selectedStation.postValue(station)


        station?.let {
            val radioUrl = it.url
            playUrl(radioUrl)
        }
    }



    fun getData(): LiveData<Resource<List<Station>>> {
        return mainRepository.getStations()
    }


}