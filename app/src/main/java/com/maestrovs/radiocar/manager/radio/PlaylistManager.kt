package com.maestrovs.radiocar.manager.radio


import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by maestromaster$ on 18/02/2025$.
 */

object PlaylistManager {
    private val _currentIndex = MutableStateFlow(0);
    private val _stationGroups = MutableStateFlow<List<StationGroup>>(emptyList())
    val stationGroups: StateFlow<List<StationGroup>> = _stationGroups.asStateFlow()

    fun updateStationGroups(groups: List<StationGroup>) {
        _stationGroups.value = groups
        if(_stationGroups.value.isNotEmpty()) {
            if(PlayerStateManager.playerState.value.currentGroup == null) {
                PlayerStateManager.updateStationGroup(_stationGroups.value[0])
            }
        }
    }

    fun getGroupForStation(station: StationStream): Int? {
        return _stationGroups.value.indexOfFirst { it.streams.contains(station) }
            .takeIf { it != -1 }
    }



    fun next() {
        val newIndex = _currentIndex.value + 1
        if (newIndex < _stationGroups.value.size) {
            _currentIndex.value = newIndex
            PlayerStateManager.updateStationGroup(_stationGroups.value[newIndex])
        }
    }

    fun prev() {
        val newIndex = _currentIndex.value - 1
        if (newIndex >=0 && newIndex < _stationGroups.value.size) {
            _currentIndex.value = newIndex
            PlayerStateManager.updateStationGroup(_stationGroups.value[newIndex])
        }
    }
}
