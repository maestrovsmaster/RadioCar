package com.maestrovs.radiocar.ui.main

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.enums.PlayAction
import com.maestrovs.radiocar.enums.PlayState
import com.maestrovs.radiocar.events.PlayActionEvent
import com.maestrovs.radiocar.events.PlayUrlEvent
import com.maestrovs.radiocar.ui.radio.StationEvent
import com.maestrovs.radiocar.utils.Resource
import com.maestrovs.radiocar.utils.combineWith
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainViewModel @androidx.hilt.lifecycle.ViewModelInject constructor(
    private val mainRepository: StationRepository,
    //  private val stationRemotePagingSource: StationRemotePagingSource
    // private val stationRemoteDataSource: StationRemoteDataSource
) : ViewModel() {



    init {
        EventBus.getDefault().register(this);
    }


    private var _selectedStation = MutableLiveData<StationEvent>(null).apply {
        value = StationEvent(null, PlayState.Stop)
    }
    val selectedStation: LiveData<StationEvent> = _selectedStation

    var lastPlayUrlEvent: PlayUrlEvent? = null

    private fun setStationEvent() {
        Log.d("Station", "switchStationState  setStationEvent = ${selectedStation.value}")
        selectedStation.value?.let { stationEvent ->
            stationEvent.station?.let { station ->
                lastPlayUrlEvent = if (station.url != null) {
                    when (stationEvent.playState) {
                        PlayState.Play -> PlayUrlEvent(
                            station.url,
                            station.name,
                            "",
                            station.favicon,
                            PlayState.Play
                        )

                        PlayState.Stop -> PlayUrlEvent(
                            station.url,
                            station.name,
                            "",
                            station.favicon,
                            PlayState.Stop
                        )
                    }
                } else {
                    PlayUrlEvent(null, station.name, "", station.favicon, PlayState.Stop)
                }

            } ?: kotlin.run {
                lastPlayUrlEvent = PlayUrlEvent(null, null, "", null, PlayState.Stop)
            }


            EventBus.getDefault().post(lastPlayUrlEvent)
            dismissLastPlayerUrlEvent()
        }

    }


    private fun dismissLastPlayerUrlEvent() {
        Handler(Looper.getMainLooper()).postDelayed({ lastPlayUrlEvent = null }, 1000)
    }

    fun switchCurrentStationState() {
        switchStationState(_selectedStation.value?.station)
    }

    fun switchStationState(newStation: Station?) {

        changeCurrentStationState(newStation)
        setStationEvent()


        newStation?.let { station ->
            Log.d("Database",">>recentStations setRecent0")


                setRecent(station.stationuuid, true)

        }


    }

    private fun changeCurrentStationState(newStation: Station?) {
        var currentStation: Station? = null

        _selectedStation.value?.let {
            it.station?.let { station -> currentStation = station }
        }
        var stationEvent = StationEvent(null, PlayState.Stop)

        Log.d("Station", "s@@@@@@@@@@@ currentStation= ${currentStation}")
        if (currentStation == null) {
            Log.d("Station", "s@@@@@@@@@@@ currentStation1")
            if (newStation != null) {
                Log.d("Station", "s@@@@@@@@@@@ currentStation2")
                stationEvent = StationEvent(newStation, PlayState.Play)
            }
        } else {
            stationEvent = if (newStation == null) {
                StationEvent(null, PlayState.Stop)
            } else {
                if (newStation.stationuuid == currentStation!!.stationuuid) {
                    val newState = if (_selectedStation.value!!.playState == PlayState.Play) {
                        PlayState.Stop
                    } else {
                        PlayState.Play
                    }
                    StationEvent(currentStation, newState)
                } else {
                    StationEvent(newStation, PlayState.Play)
                }
            }
        }
        Log.d("Station", "s@@@@@@@@@@@ stationEvent = $stationEvent")
        _selectedStation.value = stationEvent
    }


    private fun refreshCurrentStationStateFromNotification() {
        var newStationEvent: StationEvent? = null
        _selectedStation.value?.let { currentEvent ->
            val newState = if (_selectedStation.value!!.playState == PlayState.Play) {
                PlayState.Stop
            } else {
                PlayState.Play
            }
            newStationEvent = StationEvent(currentEvent.station, newState)
        }
        _selectedStation.value = newStationEvent
    }


    /*fun getData(): LiveData<PagingData<Station>> {
        return stationRemoteDataSource.getStations()
    }
*/




    fun setRecent(stationuuid: String, isRecent: Boolean) {
        Log.d("Database",">>recentStations setRecent1")
        viewModelScope.launch {
            mainRepository.setRecent(stationuuid)
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCustomEvent(event: PlayActionEvent) {

        Log.d("Station", "onCustomEvent $event")

        var eventLast = event.playUrlEvent



        when (event.playAction) {
            PlayAction.Resume, PlayAction.Pause -> {
                if (eventLast != lastPlayUrlEvent) {
                    refreshCurrentStationStateFromNotification()
                }
            }

            PlayAction.Previous -> {}
            PlayAction.Next -> {}
        }
    }


    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }


}