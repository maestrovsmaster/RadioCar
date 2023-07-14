package com.maestrovs.radiocar.ui.radio.utils

import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.enums.radio.PlayState

data class StationEvent(val station: Station?, val playState: PlayState)
