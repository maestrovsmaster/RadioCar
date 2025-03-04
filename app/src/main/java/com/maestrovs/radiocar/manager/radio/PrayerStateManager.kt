package com.maestrovs.radiocar.manager.radio

import android.graphics.Bitmap
import android.util.Log
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
            currentGroup = null,
            volume = 0.8f,
            isBuffering = false,
            audioSessionId = null,
            songMetadata = null,
            preferredBitrateOption = BitrateOption.STANDARD,
            bitmap = null,
            error = null,
            isLiked = false
        )
    )
    val playerState: StateFlow<PlayerState> = _playerState.asStateFlow()

    // val stationsGroupFlow = playerState.map { it.stationGroups }.distinctUntilChanged()

    val isPlayingBoolFlow = playerState.map { it.isPlaying }.distinctUntilChanged()
    val isPlayingFlow = combine(
        playerState.map { it.isPlaying }.distinctUntilChanged(),
        playerState.map { it.currentGroup }.distinctUntilChanged()
    ) { isPlaying, currentGroupIndex ->
        isPlaying to currentGroupIndex
    }

    val currentGroup = playerState.map { it.currentGroup }.distinctUntilChanged()

    val isBufferingFlow = playerState.map { it.isBuffering }.distinctUntilChanged()
    val volumeFlow = playerState.map { it.volume }.distinctUntilChanged()
    val audioSessionIdFlow = playerState.map { it.audioSessionId }.distinctUntilChanged()
    val songMetadataFlow = playerState.map { it.songMetadata }.distinctUntilChanged()

    val bitmapFlow = playerState.map { it.bitmap }.distinctUntilChanged()
    val errorFlow = playerState.map { it.error }.distinctUntilChanged()

    val preferredBitrateOptionFlow =
        playerState.map { it.preferredBitrateOption }.distinctUntilChanged()

    val isLikedFlow = playerState.map { it.isLiked }.distinctUntilChanged()


    fun updateStationGroup(stationGroup: StationGroup) {
        val state = _playerState.value

        var isFavourite = false

        _playerState.value = state.copy(
            currentGroup = stationGroup,

            //bitmap = null,
            isLiked = stationGroup.isFavorite

        )

    }


    /*  fun updateStationGroups(groups: List<StationGroup>) {
          Log.d("PlayerStateManager", "updateStationGroups() called")

              _playerState.value = _playerState.value.copy(stationGroups = groups)


         /* if(_playerState.value.currentGroup == null) {//Update current station only if it is null

              // Якщо список не пустий, вибираємо першу групу і перший потік
              if (groups.isNotEmpty() && groups.first().streams.isNotEmpty()) {
                  _playerState.value = _playerState.value.copy(
                      currentGroupIndex = 0,
                      currentStationIndex = 0
                  )
              }
          }*/
      }*/


    /* fun setPlaying(isPlaying: Boolean) {
         _playerState.value = _playerState.value.copy(isPlaying = isPlaying)
     }*/

    fun play() {
        _playerState.value = _playerState.value.copy(isPlaying = true)
    }


    fun pause() {
        _playerState.value = _playerState.value.copy(isPlaying = false)
    }

    fun setVolume(volume: Float) {
        val newVolume = volume.coerceIn(0f, 1f)
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

    fun setLiked(isLiked: Boolean) {
        _playerState.value = _playerState.value.copy(isLiked = isLiked)
    }

    fun setPreferredBitrate(bitrate: BitrateOption) {
        _playerState.value = _playerState.value.copy(preferredBitrateOption = bitrate)

    }

}
