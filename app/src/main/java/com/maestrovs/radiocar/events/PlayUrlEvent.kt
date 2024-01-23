package com.maestrovs.radiocar.events

import com.maestrovs.radiocar.enums.radio.PlayAction


sealed class PlayEvent

data class PlayVolume(val volume: Int):  PlayEvent()

data class PlayUrlEvent(
    val url: String?,
    val name: String?,
    val description: String?,
    val iconUrl: String?,
    val playAction: PlayAction? = null
): PlayEvent()
