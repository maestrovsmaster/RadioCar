package com.maestrovs.radiocar.events

import com.maestrovs.radiocar.enums.PlayAction

data class PlayActionEvent(val playAction: PlayAction, val playUrlEvent: PlayUrlEvent?)