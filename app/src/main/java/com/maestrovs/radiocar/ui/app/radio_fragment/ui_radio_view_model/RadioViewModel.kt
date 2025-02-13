package com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model

/**
 * Created by maestromaster$ on 10/02/2025$.
 */

import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.default_podcasts.bbc_uk_json
import com.maestrovs.radiocar.data.default_podcasts.cowboys_juke_json
import com.maestrovs.radiocar.data.default_podcasts.jaz_fm_json
import com.maestrovs.radiocar.data.default_podcasts.parseStation
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.data.repository.StationRepositoryIml
import com.maestrovs.radiocar.extensions.setVisible
import com.maestrovs.radiocar.manager.PlayerState
import com.maestrovs.radiocar.manager.PlayerStateManager
import com.maestrovs.radiocar.ui.radio.utils.RadioErrorType
import com.maestrovs.radiocar.ui.radio.utils.errorMapper
import com.maestrovs.radiocar.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RadioViewModel @Inject constructor(
    private val repository: StationRepository
) : ViewModel() {

    private val _stations = MutableLiveData<List<StationGroup>>()
    val stations: LiveData<List<StationGroup>> = _stations

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchStations()
    }



    fun fetchStations() {
        viewModelScope.launch {
            repository.getGroupedStationsFlow(offset = 0, limit = 100, countryCode = "UA") //getStationsByNameGroup("хіт фм")
                .collectLatest { resource ->
                    processResources(resource)
                }
        }
    }




    private fun processResources(response: Resource<List<StationGroup>>) {
        _isLoading.value = false
        when (response.status) {
            Resource.Status.SUCCESS -> {
                _stations.value = response.data ?: emptyList()
                PlayerStateManager.updateStationGroups(response.data ?: emptyList())
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
        }
    }

    fun stop() {
        PlayerStateManager.pause()
    }

    fun next() {
        PlayerStateManager.next()
    }

    fun prev() {
        PlayerStateManager.prev()
    }
}
