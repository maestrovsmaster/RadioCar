package com.maestrovs.radiocar.data.entities.radio.tables

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recent",
    indices = [Index(value = ["stationuuid"], unique = true)]
)
data class Recent(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var stationuuid: String,
)
