package com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model

/**
 * Created by maestromaster$ on 10/02/2025$.
 */

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.manager.radio.PlaylistManager
import com.maestrovs.radiocar.ui.radio.ListType
import com.maestrovs.radiocar.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: StationRepository
) : ViewModel() {

    private val _currentListType = MutableStateFlow<ListType>(ListType.Recent)
    val currentListType: StateFlow<ListType> = _currentListType


    fun setListType(type: ListType) {
        _currentListType.value = type
        fetchStations()
    }

    private val _stations = MutableLiveData<List<StationGroup>>()
    val stations: LiveData<List<StationGroup>> = _stations

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchStations()
    }

    private var fetchStationsJob: Job? = null

    private fun fetchStations() {
        _errorMessage.value = null
        _isLoading.value = true

        fetchStationsJob?.cancel()

        fetchStationsJob = viewModelScope.launch {
            val flow = repository.getGroupedStationsFlow(
                countryCode = "UA",
                offset = 0,
                limit = 100,
                listType = _currentListType.value
            )

            flow.collectLatest { resource ->
                processResources(resource)
            }
        }
    }


    private fun processResources(response: Resource<List<StationGroup>>) {
        _isLoading.value = false
        when (response.status) {
            Resource.Status.SUCCESS -> {


                _stations.value = response.data ?: emptyList()
                PlaylistManager.updateStationGroups(response.data ?: emptyList())
            }

            Resource.Status.ERROR -> {
                _errorMessage.value = response.message ?: "Сталася помилка"
            }

            Resource.Status.LOADING -> {
                _isLoading.value = true
            }
        }
    }

    /* fun playStream(stationGroup: StationGroup) {
         if (stationGroup.streams.isNotEmpty()) {
             PlayerStateManager.updateStation(stationGroup.streams.first())
             PlayerStateManager.play()
         }
     }*/

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

    private fun setRecent(stationGroup: StationGroup) {
        viewModelScope.launch {

            repository.setRecent(stationGroup.streams.map { it.stationUuid })
        }


    }

    fun setIsLike(stationGroup: StationGroup, isFavorite: Boolean) {
        PlayerStateManager.setLiked(isFavorite)
        viewModelScope.launch {

            if (isFavorite) {
                repository.setFavorite(stationGroup.streams.map { it.stationUuid })
            } else {
                repository.deleteFavorite(stationGroup.streams.map { it.stationUuid })
            }
        }
    }
}
