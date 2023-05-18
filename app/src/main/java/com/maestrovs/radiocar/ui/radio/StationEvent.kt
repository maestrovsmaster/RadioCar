package com.maestrovs.radiocar.ui.radio

import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.enums.radio.PlayState

data class StationEvent(val station: Station?, val playState: PlayState)
