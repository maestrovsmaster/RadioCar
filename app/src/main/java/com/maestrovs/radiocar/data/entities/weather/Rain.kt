package com.maestrovs.radiocar.data.entities.weather

import com.google.gson.annotations.SerializedName

data class Rain(  @SerializedName("1h") val volumeLastHour: Float // Rain volume in the last hour (mm)
 )
