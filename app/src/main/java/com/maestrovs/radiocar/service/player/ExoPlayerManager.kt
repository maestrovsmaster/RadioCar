package com.maestrovs.radiocar.service.player

import android.content.Context
import android.graphics.Color
import android.media.AudioManager
import android.net.Uri
import android.support.v4.media.session.MediaSessionCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.util.Log
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


@Singleton
class ExoPlayerManager @Inject constructor(@ApplicationContext private val context: Context,
                                           private val mediaSessionHelper: MediaSessionHelper2
    ): Player.Listener {

    val TAG = "ExoPlayerManager"

    var exoPlayer: SimpleExoPlayer? = null

    val audioFocusManager: AudioFocusManager = AudioFocusManager(context)

    var lastVolume: Float = 100f

    var lastPlayUrlEvent: PlayUrlEvent? = null

    var listener: AudioPlayerListener? = null

    var activityStatus: ActivityStatus = ActivityStatus.VISIBLE



    init {
        audioFocusManager.setupAudioFocus{
                focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    exoPlayer?.stop()
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    exoPlayer?.volume = 0.1f
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    exoPlayer?.volume = lastVolume
                }
            }
        }



        mediaSessionHelper.initializeMediaButtonsPlayStopSession(

            object : MediaSessionCompat.Callback() {
                override fun onPlay() {
                    super.onPlay()
                    Log.d("ExoPlayerManager22","onPlay")
                    lastPlayUrlEvent?.url?.let {
                        playUrl(it)
                    }
                }
                override fun onPause() {
                    Log.d("ExoPlayerManager22","onPause")
                    super.onPause()
                    pausePlayer()
                }
                override fun onSkipToNext() {
                    super.onSkipToNext()
                    Log.d("ExoPlayerManager22","onSkipToNext")
                    // sendMessageToViewModel(PlayAction.Next)
                    listener?.onPlayEvent(PlayAction.Next)
                }
                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
                    Log.d("ExoPlayerManager22","onSkipToPrevious")
                    // sendMessageToViewModel(PlayAction.Previous)
                    listener?.onPlayEvent(PlayAction.Previous)
                }





            }
        )



    }

    fun initializePlayer(listener: AudioPlayerListener) {
        if (exoPlayer == null) {
            val trackSelector = DefaultTrackSelector(context)
            exoPlayer = SimpleExoPlayer.Builder(context)
                .setTrackSelector(trackSelector)
                .build()
        }
        this.listener = listener
        exoPlayer?.addListener(this)
    }


    fun playUrl(url: String) {
      //  val gotFocus = audioFocusManager.requestAudioFocus()
      //  if (gotFocus) {
        //Log.d("MainActivity22","playUrl = ${url}")



            exoPlayer?.playWhenReady = true
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(url))
                .build()
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
       // }
    }

    fun pausePlayer() {
        exoPlayer?.pause()
    }

     fun stopPlayer() {
        exoPlayer?.stop()
        exoPlayer?.playWhenReady = false
      //  audioFocusManager.releaseAudioFocus()
    }


    fun releasePlayer() {

        exoPlayer?.release()
        exoPlayer = null
        mediaSessionHelper.release()
    }

    override fun onPlayerError(error: PlaybackException) {
        listener?.onPlayEvent(PlayAction.Error(null, error))
        when (error.errorCode) {
            ExoPlaybackException.TYPE_SOURCE -> {
                Log.e(TAG, "Source error: ", error)
            }

            ExoPlaybackException.TYPE_RENDERER -> {
                Log.e(TAG, "Renderer error: ", error)
            }

            ExoPlaybackException.TYPE_UNEXPECTED -> {
                Log.e(TAG, "Unexpected error: ", error)
            }

            else -> {
                Log.e(TAG, "Unknown error: ", error)
            }
        }
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_READY && playWhenReady) {
            listener?.onPlayEvent(PlayAction.Resume)
        } else if (playbackState == Player.STATE_READY) {
            listener?.onPlayEvent(PlayAction.Pause)
        } else if (playbackState == Player.STATE_BUFFERING) {
            listener?.onPlayEvent(PlayAction.Buffering)
        } else if (playbackState == Player.STATE_IDLE) {
            listener?.onPlayEvent(PlayAction.Idle)
        }
    }



    fun onPlayUrlEvent(event: PlayEvent) {
        when(event){
            is PlayUrlEvent -> {
                event.url?.let { newUrl ->
                    val playAction = event.playAction
                    if (event.playAction != null) {
                        if (playAction is PlayAction.Resume) {
                            playUrl(event.url)
                             displayCurrentStation(event.name)
                        } else pausePlayer()
                    } else {
                        if (newUrl == lastPlayUrlEvent?.url) {
                            if (exoPlayer?.isPlaying == true) {
                                pausePlayer()
                            } else {
                                playUrl(event.url)
                                 displayCurrentStation(event.name)
                            }
                        } else {
                            playUrl(event.url)
                              displayCurrentStation(event.name)

                        }
                    }
                } ?: kotlin.run {
                    stopPlayer()
                }
                lastPlayUrlEvent = event
            }
            is PlayVolume ->  {
                var volume: Float = event.volume.toFloat()
                if(volume > 100) volume = 100f;
                if(volume < 0) volume = 0f;
                lastVolume = volume/100f
                exoPlayer?.volume = lastVolume
            }

            is UIStatusEvent -> {}
        }
    }

    private fun displayCurrentStation(name: String?){
        if(name == null) return;
        if(!SettingsManager.getShowStationNameInBackground(context)) return
        if(activityStatus == ActivityStatus.VISIBLE) return
        val radioSymbol = context.getString(R.string.symbol_radio)
            Toast.makeText(context, "$radioSymbol  $name", Toast.LENGTH_SHORT).show()


    }


}