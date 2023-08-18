package com.maestrovs.radiocar.enums.radio

import com.google.android.exoplayer2.PlaybackException

sealed class PlayAction {
    object Resume : PlayAction()
    object Pause : PlayAction()
    object Previous : PlayAction()
    object Next : PlayAction()
    object Buffering : PlayAction()
    object Idle : PlayAction()
    data class Error(val description: String?, val error: PlaybackException?) : PlayAction()
}