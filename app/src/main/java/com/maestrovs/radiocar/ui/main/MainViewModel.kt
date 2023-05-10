package com.maestrovs.radiocar.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.service.AudioPlayerService
import com.maestrovs.radiocar.service.PlayUrlEvent
import com.maestrovs.radiocar.ui.radio.PlayState
import com.maestrovs.radiocar.ui.radio.StationEvent
import com.maestrovs.radiocar.utils.Resource
import org.greenrobot.eventbus.EventBus


class MainViewModel @androidx.hilt.lifecycle.ViewModelInject constructor(
    private val mainRepository: StationRepository,
) : ViewModel() {




    private var _selectedStation =  MutableLiveData<StationEvent>(null).apply {
        value = StationEvent(null, PlayState.Stop)
    }
    val selectedStation: LiveData<StationEvent> = _selectedStation

    private fun setStationEvent(){
        Log.d("Station","switchStationState  setStationEvent = ${selectedStation.value}")
        selectedStation.value?.let { stationEvent ->
            stationEvent.station?.let { station ->
                if (station.url != null) {
                    when (stationEvent.playState) {
                        PlayState.Play -> EventBus.getDefault()
                            .post(PlayUrlEvent(station.url, PlayState.Play))

                        PlayState.Stop -> EventBus.getDefault()
                            .post(PlayUrlEvent(station.url, PlayState.Stop))
                    }
                   // _selectedStation.postValue(stationEvent)
                } else {
                    EventBus.getDefault().post(PlayUrlEvent(null, PlayState.Stop))
                   // _selectedStation.postValue(StationEvent(null, PlayState.Stop))
                }

            } ?: kotlin.run {
                EventBus.getDefault().post(PlayUrlEvent(null, PlayState.Stop))
               // _selectedStation.postValue(StationEvent(null, PlayState.Stop))
            }
        }

    }


    fun switchStationState(newStation: Station?){

        Log.d("Station","switchStationState  newStation = $newStation")
        var currentStation: Station? = null

        _selectedStation.value?.let {
            it.station?.let { station -> currentStation = station }
        }

        var  stationEvent = StationEvent(null, PlayState.Stop)

        if(currentStation == null){
            if(newStation != null){
                Log.d("Station","switchStationState 1>>>>")
                stationEvent = StationEvent(newStation, PlayState.Play)
            }
        }else{
            if(newStation == null){
                stationEvent = StationEvent(null, PlayState.Stop)
            }else{
                if(newStation.stationuuid == currentStation!!.stationuuid){
                    stationEvent = StationEvent(currentStation, PlayState.Stop)
                }else{
                    stationEvent = StationEvent(newStation, PlayState.Play)
                }
            }
        }
        _selectedStation.value = stationEvent
        setStationEvent()
    }



    fun getData(): LiveData<Resource<List<Station>>> {
        return mainRepository.getStations()
    }


}