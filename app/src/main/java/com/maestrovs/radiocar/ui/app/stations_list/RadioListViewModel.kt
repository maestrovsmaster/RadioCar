package com.maestrovs.radiocar.ui.app.stations_list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.data.repository.StationRepositoryIml
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.manager.radio.PlaylistManager
import com.maestrovs.radiocar.ui.radio.ListType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by maestromaster$ on 19/02/2025$.
 */
@HiltViewModel
class RadioListViewModel @Inject constructor(
    private val repository: StationRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedCountry = MutableStateFlow("UA")
    private val _selectedTag = MutableStateFlow("")


    init {
        PlaylistManager.init(repository.getFavoriteStationIdsFlow())
    }

    private val searchParams = combine(_searchQuery, _selectedCountry, _selectedTag) { query, country, tag ->
        Triple(query, country, tag)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Triple("", "UA", ""))

    val stationFlow = searchParams.flatMapLatest { (query, country, tag) ->
        Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                Log.d("StationRemoteDataSource", "PagingSource re-created: $query, $country, $tag")
                StationPagingSource(repository, country, query, tag)
            }
        ).flow.cachedIn(viewModelScope)
    }.stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    fun searchStations(query: String, country: String, tag: String) {
        Log.d("StationRemoteDataSource", "model searchStations: $query, $country, $tag")
        _searchQuery.value = query
        _selectedCountry.value = country
        _selectedTag.value = tag
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

            invalidatePagingSource()
        }
    }


    private fun invalidatePagingSource() {
        viewModelScope.launch {
            stationFlow.collectLatest { pagingData ->
                //pagingData.refresh() // refresh not found
            }
        }
    }
}

