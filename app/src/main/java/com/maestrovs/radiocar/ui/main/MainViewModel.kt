package com.maestrovs.radiocar.ui.main

import android.location.Location
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.enums.bluetooth.BT_Status
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.events.ActivityStatus
import com.maestrovs.radiocar.events.PlayActionEvent
import com.maestrovs.radiocar.events.PlayUrlEvent
import com.maestrovs.radiocar.events.PlayVolume
import com.maestrovs.radiocar.events.UIStatusEvent
import com.maestrovs.radiocar.ui.control.SpeedManager
import com.maestrovs.radiocar.ui.radio.ListType
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainViewModel @androidx.hilt.lifecycle.ViewModelInject constructor(
    private val mainRepository: StationRepository,

) : ViewModel() {



    init {
        EventBus.getDefault().register(this);
    }

    private var listType = ListType.All


    private var _bluetoothStatus =  MutableLiveData<BT_Status?>(null)
    val bluetoothStatus get() = _bluetoothStatus
    fun setBluetoothStatus(btStatus: BT_Status){
        _bluetoothStatus.value = btStatus

    }


    private var _mustRefreshStatus =  MutableLiveData<Boolean?>(null)
    val mustRefreshStatus get() = _mustRefreshStatus
    fun setMustRefreshStatus(){
        _mustRefreshStatus.value = true
        _mustRefreshStatus.value = false
    }

    private var _mustNavToSettings =  MutableLiveData<Boolean?>(null)
    val mustNavToSettings get() = _mustNavToSettings
    fun setMustNavToSettings(){
        _mustNavToSettings.value = true
        _mustNavToSettings.value = false
    }


    private var _speed =  MutableLiveData<Float>(0f)
    val speed get() = _speed


    private var _currentLocation =  MutableLiveData<Location?>(null)
    val location get() = _currentLocation
    fun updateLacationAndSpeed(newLocation: Location) {

        _currentLocation.value = newLocation

        location.value?.let {
            _speed.value = SpeedManager.updateSpeed(it)
        }

    }


    private var _selectedStation = MutableLiveData<Station?>(null)
    val selectedStation: LiveData<Station?> = _selectedStation

    fun setStation(station: Station, updateRecent: Boolean){
        _selectedStation.value = station

        EventBus.getDefault().post(PlayUrlEvent(station.url, station.name, "", null))
       // dismissLastPlayerUrlEvent()

        if(updateRecent) setRecent(station.stationuuid, true)
    }

    fun playCurrentStationState() {
        Log.d("AudioPlayerService","playCurrentStationState")
        _selectedStation.value?.let { station ->
            EventBus.getDefault().post(PlayUrlEvent(station.url, station.name, "", null, PlayAction.Resume))
        }
    }

    fun stopCurrentStationState() {
        Log.d("AudioPlayerService","stopCurrentStationState")
        _selectedStation.value?.let { station ->
            EventBus.getDefault().post(PlayUrlEvent(station.url, station.name, "", null, PlayAction.Pause))
        }?:run{
            EventBus.getDefault().post(PlayUrlEvent(null, null, "", null))
        }
    }

    fun switchCurrentStationState(){
        Log.d("AudioPlayerService","switchCurrentStationState")
        _selectedStation.value?.let { station ->
            EventBus.getDefault().post(PlayUrlEvent(station.url, station.name, "", null))
        }?:run{
            EventBus.getDefault().post(PlayUrlEvent(null, null, "", null))
        }

    }


    private var _playAction = MutableLiveData<PlayAction>(PlayAction.Idle)
    val playAction: LiveData<PlayAction> = _playAction

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onCustomEvent(event: PlayActionEvent) {

        com.google.android.exoplayer2.util.Log.d("MainActivity22",
            "MainViewModel: received event from service: ${event.playAction.toString()}" )

        var eventLast = event.playUrlEvent
        _playAction.value = event.playAction

    }

    var lastPlayUrlEvent: PlayUrlEvent? = null
    private fun dismissLastPlayerUrlEvent() {
        Handler(Looper.getMainLooper()).postDelayed({ lastPlayUrlEvent = null }, 1000)
    }

    fun setIfNeedInitStation(station: Station){
        if(_selectedStation.value == null){
            _selectedStation.value = station
        }
    }


    fun switchFavorite(){
        val currenStation = _selectedStation.value?:return

        val newStation = currenStation

        var shouldBeFavorite = false
        if(newStation.isFavorite != null){
            if(newStation.isFavorite==0){
                shouldBeFavorite = true
            }
        }else{
            shouldBeFavorite = true
        }


        if(shouldBeFavorite){
            newStation.isFavorite = 1
            addFavorite(newStation.stationuuid)
        }else{
            newStation.isFavorite = null
            deleteFavorite(newStation.stationuuid, )
        }

        _selectedStation.value = newStation
    }





    private fun addFavorite(stationuuid: String) {
        viewModelScope.launch {
            mainRepository.setFavorite(stationuuid)
        }
    }

    private fun deleteFavorite(stationuuid: String) {
        viewModelScope.launch {
            mainRepository.deleteFavorite(stationuuid)
        }
    }

     fun deleteRecent(stationuuid: String) {
         stopCurrentStationState()
        viewModelScope.launch {
            mainRepository.deleteRecent(stationuuid)
        }
    }


    fun setRecent(stationuuid: String, isRecent: Boolean) {
        Log.d("Database",">>recentStations setRecent1")
        viewModelScope.launch {
            mainRepository.setRecent(stationuuid)
        }
    }


    fun updateVolume(progress: Int){
        EventBus.getDefault().post(PlayVolume(progress))
    }

    fun setListType(listType: ListType){
        this.listType = listType
    }


    fun updateActivityStatus(activityStatus: ActivityStatus){
        EventBus.getDefault().post(UIStatusEvent(activityStatus, listType, selectedStation.value))
    }


    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }


}