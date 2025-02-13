package com.maestrovs.radiocar.manager

import android.graphics.Bitmap
import com.maestrovs.radiocar.data.entities.radio.BitrateOption
import com.maestrovs.radiocar.data.entities.radio.StationGroup
import com.maestrovs.radiocar.data.entities.radio.StationStream

/**
 * Created by maestromaster on 11/02/2025$.
 */

data class PlayerState(
    val isPlaying: Boolean,
    val currentGroupIndex: Int,
    val currentStationIndex: Int,
    val stationGroups: List<StationGroup>,
    val volume: Int,
    val isBuffering: Boolean = false,
    val audioSessionId: Int?,
    val songMetadata: String? = null,
    val bitmap: Bitmap? = null,
    val preferredBitrateOption: BitrateOption = BitrateOption.STANDARD,
    val error: String? = null,
) {
    val currentGroup: StationGroup?
        get() = stationGroups.getOrNull(currentGroupIndex)

    val currentStation: StationStream?
        get() = currentGroup?.streams?.getOrNull(currentStationIndex)

    val hasStation: Boolean
        get() = currentStation != null
}
