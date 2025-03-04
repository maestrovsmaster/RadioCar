package com.maestrovs.radiocar.manager.radio


import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream
import com.maestrovs.radiocar.data.entities.radio.tables.Favorites
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/**
 * Created by maestromaster$ on 18/02/2025$.
 */

object PlaylistManager {
    private val _currentIndex = MutableStateFlow(0);
    private val _stationGroups = MutableStateFlow<List<StationGroup>>(emptyList())
    val stationGroups: StateFlow<List<StationGroup>> = _stationGroups.asStateFlow()

    private val _favorites = MutableStateFlow<Set<String>>(emptySet())

    fun init(favoritesFlow: Flow<List<Favorites>>) {
        favoritesFlow.onEach { favorites ->
            _favorites.value = favorites.map { it.stationuuid }.toSet()
            updateFavoritesInStations()
        }.launchIn(GlobalScope)
    }




    private fun updateFavoritesInStations() {
        _stationGroups.value = _stationGroups.value.map { group ->
           /* group.copy(stations = group.stations.map { station ->
                station.copy(isFavorite = station.stationuuid in _favorites.value)
            })*/
            group.copy(isFavorite = group.streams.any { it.stationUuid in _favorites.value })
        }
    }


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
