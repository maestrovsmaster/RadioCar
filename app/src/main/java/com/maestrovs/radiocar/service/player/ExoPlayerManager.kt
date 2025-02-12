package com.maestrovs.radiocar.service.player

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.utils.isPlayableStream

import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@UnstableApi
@Singleton
class ExoPlayerManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val mediaSessionHelper: MediaSessionHelper2
) : Player.Listener {

    val TAG = "ExoPlayerManager"

    private var exoPlayer: ExoPlayer? = null

    private val audioFocusManager: AudioFocusManager = AudioFocusManager(context)

    private var lastVolume: Float = 100f

    var listener: AudioPlayerListener? = null


    init {
        audioFocusManager.setupAudioFocus { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> exoPlayer?.pause()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> exoPlayer?.volume = 0.1f
                AudioManager.AUDIOFOCUS_GAIN -> exoPlayer?.volume = lastVolume
            }
        }

        //Receiver commands from Bluetooth
        mediaSessionHelper.initializeMediaButtonsPlayStopSession(
            object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    //Log.d("AudioPlayerService","Bluetooth play ")
                    listener?.onPlayEvent(PlayAction.Resume)
                }

                override fun onPause() {
                    super.onPause()
                   // Log.d("AudioPlayerService","Bluetooth pause ")
                    listener?.onPlayEvent(PlayAction.Pause)
                }

                override fun onSkipToNext() {
                    listener?.onPlayEvent(PlayAction.Next)
                }

                override fun onSkipToPrevious() {
                    listener?.onPlayEvent(PlayAction.Previous)
                }
            }
        )
    }

    @OptIn(UnstableApi::class)
    fun initializePlayer(listener: AudioPlayerListener) {
        if (exoPlayer == null) {
            val trackSelector = DefaultTrackSelector(context)
            exoPlayer = ExoPlayer.Builder(context) // ✅ Використовуємо новий Builder
                .setTrackSelector(trackSelector)
                .build()
        }
        this.listener = listener
        exoPlayer?.addListener(this)
    }

    fun playUrl(url: String) {


        val isPlayableStream = isPlayableStream(url)
       if(!isPlayableStream){
           listener?.onPlayEvent(PlayAction.Error("Invalid stream", null))
           return
       }

        val gotFocus = audioFocusManager.requestAudioFocus()
        if (gotFocus) {
            exoPlayer?.playWhenReady = true

                val mediaItem = MediaItem.Builder()
                    .setUri(Uri.parse(url))
                    .build()
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.play()

        }
    }

    fun pausePlayer() {
        exoPlayer?.pause()
    }

    fun stopPlayer() {
        exoPlayer?.stop()
        exoPlayer?.playWhenReady = false
        audioFocusManager.releaseAudioFocus()
    }

    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
        mediaSessionHelper.release()
    }

    @OptIn(UnstableApi::class)
    override fun onPlayerError(error: PlaybackException) {
        listener?.onPlayEvent(PlayAction.Error(null, error))
        Log.e(TAG, "Player error: ${error.errorCode}", error)
    }

    override fun onPlaybackStateChanged(state: Int) {
        when (state) {
            Player.STATE_BUFFERING -> {
                listener?.onPlayEvent(PlayAction.Buffering(true))
            }
            Player.STATE_READY -> {
                listener?.onPlayEvent(PlayAction.Buffering(false))
            }
            Player.STATE_ENDED -> {
                //Log.d("ExoPlayerManager", "Playback ended")
            }
            Player.STATE_IDLE -> {
                //Log.d("ExoPlayerManager", "Player is idle")
            }
        }
    }


}
