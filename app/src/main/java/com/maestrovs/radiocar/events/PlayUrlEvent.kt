package com.maestrovs.radiocar.events

import com.maestrovs.radiocar.enums.radio.PlayAction


data class PlayUrlEvent(
    val url: String?,
    val name: String?,
    val description: String?,
    val iconUrl: String?,
    val playAction: PlayAction? = null
)
