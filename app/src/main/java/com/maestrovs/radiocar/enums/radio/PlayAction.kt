package com.maestrovs.radiocar.enums.radio

import androidx.media3.common.PlaybackException


sealed class PlayAction {
    object Resume : PlayAction()
    object Pause : PlayAction()
    object Previous : PlayAction()
    object Next : PlayAction()
    data class Buffering(val isBuffering: Boolean) : PlayAction()
    object Idle : PlayAction()
    data class Error(val description: String?, val error: PlaybackException?) : PlayAction()
}