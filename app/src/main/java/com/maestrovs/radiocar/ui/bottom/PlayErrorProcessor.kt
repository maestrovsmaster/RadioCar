package com.maestrovs.radiocar.ui.bottom

import android.content.Context
import android.util.Log
import com.google.android.exoplayer2.PlaybackException
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.enums.radio.PlayAction

fun processError(context: Context, playAction: PlayAction.Error): String {

    return if (playAction.error != null) {
        context.getString(R.string.radio_stream_unavailable)
    } else playAction.description ?: context.getString(R.string.playback_error)


}