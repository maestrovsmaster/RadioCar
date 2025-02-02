package com.maestrovs.radiocar.service.player

import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.events.ActivityStatus
import com.maestrovs.radiocar.events.PlayEvent
import com.maestrovs.radiocar.events.PlayUrlEvent
import com.maestrovs.radiocar.events.PlayVolume
import com.maestrovs.radiocar.events.UIStatusEvent
import com.maestrovs.radiocar.ui.settings.SettingsManager
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

    var exoPlayer: ExoPlayer? = null

    val audioFocusManager: AudioFocusManager = AudioFocusManager(context)

    var lastVolume: Float = 100f

    var lastPlayUrlEvent: PlayUrlEvent? = null

    var listener: AudioPlayerListener? = null

    var activityStatus: ActivityStatus = ActivityStatus.VISIBLE

    init {
        audioFocusManager.setupAudioFocus { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> exoPlayer?.pause()
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> exoPlayer?.volume = 0.1f
                AudioManager.AUDIOFOCUS_GAIN -> exoPlayer?.volume = lastVolume
            }
        }

        mediaSessionHelper.initializeMediaButtonsPlayStopSession(
            object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    lastPlayUrlEvent?.url?.let {
                        playUrl(it)
                    }
                }

                override fun onPause() {
                    super.onPause()
                    pausePlayer()
                }

                override fun onSkipToNext() {
                    super.onSkipToNext()
                    listener?.onPlayEvent(PlayAction.Next)
                }

                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
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

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                if (playWhenReady) {
                    listener?.onPlayEvent(PlayAction.Resume)
                } else {
                    listener?.onPlayEvent(PlayAction.Pause)
                }
            }
            Player.STATE_BUFFERING -> listener?.onPlayEvent(PlayAction.Buffering)
            Player.STATE_IDLE -> listener?.onPlayEvent(PlayAction.Idle)
        }
    }

    fun onPlayUrlEvent(event: PlayEvent) {
        when (event) {
            is PlayUrlEvent -> {
                event.url?.let { newUrl ->
                    if (newUrl == lastPlayUrlEvent?.url) {
                        if (exoPlayer?.isPlaying == true) {
                            pausePlayer()
                        } else {
                            playUrl(newUrl)
                            displayCurrentStation(event.name)
                        }
                    } else {
                        playUrl(newUrl)
                        displayCurrentStation(event.name)
                    }
                } ?: stopPlayer()

                lastPlayUrlEvent = event
            }

            is PlayVolume -> {
                var volume: Float = event.volume.toFloat()
                volume = volume.coerceIn(0f, 100f) / 100f
                lastVolume = volume
                exoPlayer?.volume = lastVolume
            }

            is UIStatusEvent -> {}
        }
    }

    private fun displayCurrentStation(name: String?) {
        if (name == null || !SettingsManager.getShowStationNameInBackground(context) || activityStatus == ActivityStatus.VISIBLE) return
        val radioSymbol = context.getString(R.string.symbol_radio)
        Toast.makeText(context, "$radioSymbol  $name", Toast.LENGTH_SHORT).show()
    }
}
