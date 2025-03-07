package com.maestrovs.radiocar.data.entities.radio

/**
 * Created by maestromaster on 10/02/2025$.
 */

data class StationGroup(
    val name: String,
    val streams: List<StationStream>,
    val stations: List<Station>,
    val favicon: String,
    val isFavorite: Boolean,
    val countryCode: String? = null
)

