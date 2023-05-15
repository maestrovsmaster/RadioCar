package com.maestrovs.radiocar.data.entities.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorites(
    @PrimaryKey
    var stationuuid: String,
)
