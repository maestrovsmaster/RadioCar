package com.maestrovs.radiocar.data.default_podcasts

import com.google.gson.Gson
import com.maestrovs.radiocar.data.entities.radio.Station

fun parseStation(json: String): Station {
    return Gson().fromJson(json, Station::class.java)
}