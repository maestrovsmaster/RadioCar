package com.maestrovs.radiocar.data.entities.weather

import com.google.gson.annotations.SerializedName

data class Snow(
    @SerializedName("1h") val volumeLastHour: Float // Snow volume in the last hour (mm)
)
