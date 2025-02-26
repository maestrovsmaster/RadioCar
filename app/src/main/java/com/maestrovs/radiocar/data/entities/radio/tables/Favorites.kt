package com.maestrovs.radiocar.data.entities.radio.tables

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "favorites",
        indices = [androidx.room.Index(value = ["stationuuid"], unique = true)]
)
data class Favorites(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var stationuuid: String,
    var lastPlayedTime: Long,
)


