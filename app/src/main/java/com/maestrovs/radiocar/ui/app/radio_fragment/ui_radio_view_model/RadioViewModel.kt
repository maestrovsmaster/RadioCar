package com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model

/**
 * Created by maestromaster$ on 10/02/2025$.
 */

import android.bluetooth.BluetoothAdapter
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.data.entities.radio.BitrateOption
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.manager.bluetooth.BluetoothStateManager
import com.maestrovs.radiocar.manager.location.LocationStateManager
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.manager.radio.PlaylistManager
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepository
import com.maestrovs.radiocar.ui.radio.ListType
import com.maestrovs.radiocar.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: StationRepository,
    private val sharedPreferencesRepository: SharedPreferencesRepository
) : ViewModel() {

  //  private val _currentCountry = MutableStateFlow<String>("UK")

    private val _currentListType = MutableStateFlow<ListType>(ListType.All)
    val currentListType: StateFlow<ListType> = _currentListType

    private val _stations = MutableLiveData<List<StationGroup>>()
    val stations: LiveData<List<StationGroup>> = _stations

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var fetchStationsJob: Job? = null

    init {
       // _currentCountry.value = sharedPreferencesRepository.getCountryCode()


       // LocationStateManager.updateCountryCode(_currentCountry.value)


        _currentListType.value = sharedPreferencesRepository.getListType()
        fetchStations()

        viewModelScope.launch(Dispatchers.IO) {
            val stationLatest = sharedPreferencesRepository.getRecentStationGroup()
            if(stationLatest != null) {
                PlayerStateManager.updateStationGroup(stationLatest)
            }
        }
        val volume = sharedPreferencesRepository.getSavedVolume().toFloat()/100
        PlayerStateManager.setVolume(volume)
    }






    fun setListType(type: ListType) {
        _currentListType.value = type
        sharedPreferencesRepository.setListType(type)
        fetchStations()
    }

     fun fetchStations() {
        _errorMessage.value = null
        _isLoading.value = true

        fetchStationsJob?.cancel()

        fetchStationsJob = viewModelScope.launch {

            val flow = when (_currentListType.value) {
                ListType.Recent -> repository.getRecentStationDetailsByLastTimeGrouped()
                ListType.Favorites -> repository.getFavoriteStationDetailsByLastTimeGrouped()
                else ->   repository.getPagedStationsFlow(
                        country = sharedPreferencesRepository.getCountryCode(),
                        offset = 0,
                        limit = 100
                    )
            }

            flow.collectLatest { resource ->
                processResources(resource)
            }
        }
    }


    private fun processResources(response: Resource<List<StationGroup>>) {
        Log.d("MediumPlayerWidget", "processResources... 2")
        _isLoading.value = false
        when (response.status) {
            Resource.Status.SUCCESS -> {
                Log.d("StationRepositoryIml", "processResources... 4 ${response.data}")

                _stations.value = response.data ?: emptyList()
                PlaylistManager.updateStationGroups(response.data ?: emptyList())
            }

            Resource.Status.ERROR -> {
                _errorMessage.value = response.message ?: "Error"
            }

            Resource.Status.LOADING -> {
                //_isLoading.value = true
            }
        }
    }


    fun playGroup(stationGroup: StationGroup) {
        if (stationGroup.streams.isNotEmpty()) {
            PlayerStateManager.updateStationGroup(stationGroup)
            PlayerStateManager.play()
            setRecent(stationGroup)
        }
    }

    fun stop() {
        PlayerStateManager.pause()
    }

    fun next() {
        PlaylistManager.next()
    }

    fun prev() {
        PlaylistManager.prev()
    }

    fun setVolume(volume: Float) {
        //0.0..1.0 to 0..100 int
        val intVolume = (volume*100).toInt()
        sharedPreferencesRepository.saveVolume(intVolume)
        PlayerStateManager.setVolume(volume)
    }

    fun setBitrate(bitrate: BitrateOption)  {
        PlayerStateManager.setPreferredBitrate(bitrate)
    }

    private fun setRecent(stationGroup: StationGroup) {
        viewModelScope.launch {
            repository.insertStations(stationGroup.stations)
            repository.setRecent(stationGroup.streams.map { it.stationUuid })
            sharedPreferencesRepository.saveRecentStationGroup(stationGroup)
        }
    }

    fun setIsLike(stationGroup: StationGroup, isFavorite: Boolean) {
        PlayerStateManager.setLiked(isFavorite)
        viewModelScope.launch {
            repository.insertStations(stationGroup.stations)
            if (isFavorite) {
                repository.setFavorite(stationGroup.streams.map { it.stationUuid })
            } else {
                repository.deleteFavorite(stationGroup.streams.map { it.stationUuid })
            }
        }
    }

    fun deleteFromRecentAndFavorites(stationGroup: StationGroup) {
        PlayerStateManager.setLiked(false)
        viewModelScope.launch {
            repository.deleteFavorite(stationGroup.streams.map { it.stationUuid })
            repository.deleteRecent(stationGroup.streams.map { it.stationUuid })

        }
    }


    val currentBluetoothDevice: StateFlow<String?> =
        BluetoothStateManager.currentBluetoothDevice.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            ""
        )


    private val _manualBluetoothState = MutableStateFlow<Int?>(null)

    val isBluetoothEnabled: StateFlow<Int?> = combine(
        BluetoothStateManager.bluetoothState,
        _manualBluetoothState
    ) { managerState, manualState ->

        val actualState = managerState ?: manualState
       // Log.d(
        //    "BluetoothStatusReceiver",
        //    "Combined Bluetooth state: Manager - $managerState, Manual - $manualState Actual - $actualState"
        //)
        actualState
    }.stateIn(viewModelScope, SharingStarted.Lazily, BluetoothAdapter.STATE_OFF)

    fun setBluetoothState(state: Int) {
        _manualBluetoothState.value = state
    }
}
