package com.maestrovs.radiocar.ui.radio

import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.enums.PlayState

data class StationEvent(val station: Station?, val playState: PlayState)
