package com.maestrovs.radiocar.service.player

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.maestrovs.radiocar.service.AudioPlayerService
import javax.inject.Inject

class MediaSessionHelper2 @Inject
constructor(
    private val context: Context
) {

    lateinit var mediaSession: MediaSessionCompat
        private set

    @OptIn(UnstableApi::class)
    fun initializeMediaButtonsPlayStopSession(callback: MediaSessionCompat.Callback) {
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
            setClass(context, AudioPlayerService::class.java)

        }


        val pendingIntent = PendingIntent.getService(
            context,
            0,
            mediaButtonIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSessionCompat(context, "AudioPlayerService").apply {
            setCallback(callback)
            setMediaButtonReceiver(pendingIntent)

            val playbackState = PlaybackStateCompat.Builder()
                .setActions(
                    PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE
                            or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                            or PlaybackStateCompat.ACTION_SEEK_TO
                )
                .build()
            setPlaybackState(playbackState)
            isActive = true
        }
    }

    fun release() {
        mediaSession.release()
    }

}