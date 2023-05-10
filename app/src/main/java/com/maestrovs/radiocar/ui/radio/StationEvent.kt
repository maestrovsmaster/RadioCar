package com.maestrovs.radiocar.ui.radio

import com.maestrovs.radiocar.data.entities.Station

data class StationEvent(val station: Station?, val playState: PlayState)
