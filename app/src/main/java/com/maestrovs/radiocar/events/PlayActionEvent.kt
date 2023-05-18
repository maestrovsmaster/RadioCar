package com.maestrovs.radiocar.events

import com.maestrovs.radiocar.enums.radio.PlayAction

data class PlayActionEvent(val playAction: PlayAction, val playUrlEvent: PlayUrlEvent?)