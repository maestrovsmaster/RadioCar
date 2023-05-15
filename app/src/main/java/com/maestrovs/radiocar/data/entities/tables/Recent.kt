package com.maestrovs.radiocar.data.entities.tables

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent")
data class Recent(
    @PrimaryKey
    var stationuuid: String,
)