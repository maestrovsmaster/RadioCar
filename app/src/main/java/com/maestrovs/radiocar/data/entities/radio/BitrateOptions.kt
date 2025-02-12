package com.maestrovs.radiocar.data.entities.radio

/**
 * Created by maestromaster on 10/02/2025$.
 */

enum class BitrateOption(val bitrate: Int, val displayName: String) {
    HD(320, "HD Quality"),
    HIGH(192, "High Quality"),
    STANDARD(128, "Standard"),
    LOW(64, "Low Quality"),
    VERY_LOW(32, "Very Low Quality");

    companion object {
        fun fromBitrate(value: Int): BitrateOption {
            return values().find { it.bitrate == value }
                ?: values().minByOrNull { kotlin.math.abs(it.bitrate - value) }!!
        }
    }
}
