package com.maestrovs.radiocar.manager

import android.graphics.Bitmap
import com.maestrovs.radiocar.data.entities.radio.BitrateOption
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Created by maestromaster on 10/02/2025$.
 */

object PlayerStateManager {
    private val _playerState = MutableStateFlow(
        PlayerState(
            isPlaying = false,
            currentGroupIndex = 0,
            currentStationIndex = 0,
            stationGroups = emptyList(),
            volume = 50,
            isBuffering = false,
            audioSessionId = null,
            songMetadata = null,
            preferredBitrateOption = BitrateOption.STANDARD,
            bitmap = null,
            error = null
        )
    )
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    //val isPlayingFlow = playerState.map { it.isPlaying }.distinctUntilChanged()
    val isPlayingFlow = combine(
        playerState.map { it.isPlaying }.distinctUntilChanged(),
        playerState.map { it.currentGroupIndex }.distinctUntilChanged()
    ) { isPlaying, currentGroupIndex ->
        isPlaying to currentGroupIndex
    }

    val isBufferingFlow = playerState.map { it.isBuffering }.distinctUntilChanged()
    val volumeFlow = playerState.map { it.volume }.distinctUntilChanged()
    val audioSessionIdFlow = playerState.map { it.audioSessionId }.distinctUntilChanged()
    val songMetadataFlow = playerState.map { it.songMetadata }.distinctUntilChanged()

    val bitmapFlow = playerState.map { it.bitmap }.distinctUntilChanged()
    val errorFlow = playerState.map { it.error }.distinctUntilChanged()

    val preferredBitrateOptionFlow = playerState.map { it.preferredBitrateOption }.distinctUntilChanged()

    fun updateStation(stationStream: StationStream) {
        val state = _playerState.value

        val groupIndex = state.stationGroups.indexOfFirst { group ->
            group.streams.contains(stationStream)
        }

        if (groupIndex != -1) {
            val stationIndex = state.stationGroups[groupIndex].streams.indexOf(stationStream)
            _playerState.value = state.copy(
                currentGroupIndex = groupIndex,
                currentStationIndex = stationIndex
            )
        }
    }

    fun updateStationGroup(stationGroup: StationGroup) {
        val state = _playerState.value

        val groupIndex = state.stationGroups.indexOfFirst { it.name == stationGroup.name }
        if (groupIndex != -1) {
            _playerState.value = state.copy(
                currentGroupIndex = groupIndex,
                currentStationIndex = 0, // Завжди починаємо з першого потоку у групі
                //bitmap = null,

            )
        }
    }





    fun updateStationGroups(groups: List<StationGroup>) {
        _playerState.value = _playerState.value.copy(stationGroups = groups)

        // Якщо список не пустий, вибираємо першу групу і перший потік
        if (groups.isNotEmpty() && groups.first().streams.isNotEmpty()) {
            _playerState.value = _playerState.value.copy(
                currentGroupIndex = 0,
                currentStationIndex = 0
            )
        }
    }



   /* fun nextStationStream() {
        val state = _playerState.value
        val group = state.currentGroup

        if (group != null) {
            val newIndex = state.currentStationIndex + 1
            if (newIndex < group.streams.size) {
                _playerState.value = state.copy(currentStationIndex = newIndex)
            } else {
                nextGroup()
            }
        }
    }

    fun prevStationStream() {
        val state = _playerState.value
        if (state.currentStationIndex > 0) {
            _playerState.value = state.copy(currentStationIndex = state.currentStationIndex - 1)
        } else {
            prevGroup() // Якщо ми на першій станції в групі, переходимо до попередньої групи
        }
    }*/




    fun next() {
        val state = _playerState.value
        val newIndex = state.currentGroupIndex + 1
        if (newIndex < state.stationGroups.size) {
            _playerState.value = state.copy(currentGroupIndex = newIndex, currentStationIndex = 0, bitmap = null,)
        }
    }

    fun prev() {
        val state = _playerState.value
        if (state.currentGroupIndex > 0) {
            _playerState.value = state.copy(currentGroupIndex = state.currentGroupIndex - 1, currentStationIndex = 0, bitmap = null,)
        }
    }

   /* fun setPlaying(isPlaying: Boolean) {
        _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
    }*/

    fun play(){
        _playerState.value = _playerState.value.copy(isPlaying = true)
    }


    fun pause() {
        _playerState.value = _playerState.value.copy(isPlaying = false)
    }

    fun setVolume(volume: Int) {
        val newVolume = volume.coerceIn(0, 100)
        _playerState.value = _playerState.value.copy(volume = newVolume)
    }

    fun setBuffering(isBuffering: Boolean) {
        _playerState.value = _playerState.value.copy(isBuffering = isBuffering)
    }

    fun setAudioSessionId(audioSessionId: Int?) {
        _playerState.value = _playerState.value.copy(audioSessionId = audioSessionId)
    }

    fun setSongMetadata(metadata: String?) {
        _playerState.value = _playerState.value.copy(songMetadata = metadata)
    }

    fun setBitmap(bitmap: Bitmap?) {
        _playerState.value = _playerState.value.copy(bitmap = bitmap)
    }

    fun setError(error: String?) {
        _playerState.value = _playerState.value.copy(error = error)
    }

    fun setPreferredBitrateOption(bitrateOption: BitrateOption) {
        _playerState.value = _playerState.value.copy(preferredBitrateOption = bitrateOption)
    }

}
