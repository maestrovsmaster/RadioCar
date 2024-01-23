package com.maestrovs.radiocar.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.content.res.ResourcesCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.google.android.exoplayer2.util.Log
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.events.PlayActionEvent
import com.maestrovs.radiocar.events.PlayUrlEvent
import com.maestrovs.radiocar.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.media.AudioManager
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import com.maestrovs.radiocar.events.PlayEvent
import com.maestrovs.radiocar.events.PlayVolume

const val NOTIFICATION_REQUEST_CODE = 56465

@AndroidEntryPoint
class AudioPlayerService : Service(), Player.Listener {

    val TAG = "AudioPlayerService"


    private val binder = LocalBinder()
    private var exoPlayer: SimpleExoPlayer? = null

    private lateinit var playerNotificationManager: PlayerNotificationManager

    private lateinit var mediaSession: MediaSessionCompat //Class for controll bluetooth hardware buttons clicking

    var lastPlayUrlEvent: PlayUrlEvent? = null
    var lastVolume: Float = 100f


    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        // Handle playback state changes here
        //  sendMessageToViewModel(PlayAction.Previous)
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
            // The player has moved to the next item
            // sendMessageToViewModel(PlayAction.Next)
        }
    }


    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        EventBus.getDefault().register(this)

        setupAudioFocus()


        val channelId = 1231//"audio_player_channel"
        val channelName = "RadioCarChannel"//"getString(R.string.audio_player_channel_name)"
        val channelDescription =
            "RadioCarChannelDescription"//"getString(R.string.audio_player_channel_description)"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId.toString(), channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }



        playerNotificationManager = PlayerNotificationManager.Builder(this,
            channelId, channelId.toString(),
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    var title = ""
                    lastPlayUrlEvent?.let {
                        title = it.name ?: ""
                    }
                    return getString(R.string.symbol_radio) + " $title"
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val notificationIntent =
                        Intent(this@AudioPlayerService, MainActivity::class.java)

                    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        PendingIntent.getActivity(
                            this@AudioPlayerService,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                        )
                    } else {
                        PendingIntent.getActivity(
                            this@AudioPlayerService,
                            0,
                            notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                        )
                    }
                }

                override fun getCurrentContentText(player: Player): CharSequence {

                    return getString(R.string.notification_radio_description)
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_launcher_background
                    )
                }
            }
        ).setNotificationListener(object : NotificationListener {
            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                stopSelf()
            }

            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {

                if (ongoing) {
                    startForeground(notificationId, notification)
                } else {
                    stopForeground(false)
                }
            }
        }).build()

        playerNotificationManager.setPlayer(exoPlayer)
        playerNotificationManager.setUseStopAction(true)

        playerNotificationManager.setColor(
            ResourcesCompat.getColor(
                this.resources,
                R.color.blue,
                null
            )
        )



        playerNotificationManager.setUseNextAction(false)
        playerNotificationManager.setUsePreviousAction(false)
        exoPlayer?.addListener(this)


        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                android.util.Log.e(TAG, "Lost network connection")
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                android.util.Log.e(TAG, "Network connection available again")
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)


        initializeMediaButtonsPlayStopSession()

    }//onCreate


    /**
     * Callback for listening hardware bluetooth clicking buttons.
     */
    private fun initializeMediaButtonsPlayStopSession() {
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON)
        mediaButtonIntent.setClass(this, AudioPlayerService::class.java)

        val pendingIntent: PendingIntent =
            PendingIntent.getService(
                this,
                0,
                mediaButtonIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

        mediaSession = MediaSessionCompat(this, "AudioPlayerService").apply {

            // Setup Callback
            setCallback(object : MediaSessionCompat.Callback() {

                override fun onPlay() {
                    super.onPlay()
                    android.util.Log.d("BluetoothDevice", "Bluetooth play")
                    lastPlayUrlEvent?.url?.let {
                        playUrl(it)
                    }
                }

                override fun onPause() {
                    super.onPause()
                    // Pause media
                    android.util.Log.d("BluetoothDevice", "Bluetooth pause")
                    pausePlayer()
                }


                override fun onSkipToNext() {
                    super.onSkipToNext()
                    // Next media
                    android.util.Log.d("BluetoothDevice", "Bluetooth next")
                    sendMessageToViewModel(PlayAction.Next)
                }

                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
                    // Previous media
                    android.util.Log.d("BluetoothDevice", "Bluetooth previous")
                    sendMessageToViewModel(PlayAction.Previous)
                }
            })

            // Other media session configurations...
        }


        //mediaSession.setCallback(MediaSessionCallback())
        mediaSession.setMediaButtonReceiver(pendingIntent)

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(
                PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE
                        or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                or PlaybackStateCompat.ACTION_SEEK_TO
            )
            .build()
        mediaSession.setPlaybackState(playbackState)
    }

    private fun sendMessageToViewModel(playAction: PlayAction) {
        EventBus.getDefault().post(PlayActionEvent(playAction, lastPlayUrlEvent))
    }


    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    fun initializePlayer() {
        if (exoPlayer == null) {
            val trackSelector = DefaultTrackSelector(this)
            exoPlayer = SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build()
        }
    }


    fun playUrl(url: String) {
        Log.d("ASD", "Play exoPlayer = $exoPlayer")
        val gotFocus = requestAudioFocus()
        if (gotFocus) {
            // Почати медіа відтворення, оскільки ми отримали аудіо фокус
            exoPlayer?.playWhenReady = true
            val mediaItem = MediaItem.Builder()
                .setUri(Uri.parse(url))
                .build()
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            exoPlayer?.play()
        }
    }


    private fun pausePlayer() {
        exoPlayer?.pause()
    }

    private fun stopPlayer() {
        exoPlayer?.stop()
        exoPlayer?.playWhenReady = false
        // Відпустити аудіо фокус, якщо існує
        releaseAudioFocus()
    }


    fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        releasePlayer()
        mediaSession.release()
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayUrlEvent(event: PlayEvent) {

        when(event){
            is PlayUrlEvent -> {
                event.url?.let { newUrl ->
                    val playAction = event.playAction
                    if (event.playAction != null) {
                        if (playAction is PlayAction.Resume) {
                            playUrl(event.url)
                        } else pausePlayer()
                    } else {
                        if (newUrl == lastPlayUrlEvent?.url) {
                            if (exoPlayer?.isPlaying == true) {
                                pausePlayer()
                            } else {
                                playUrl(event.url)
                            }
                        } else {
                            playUrl(event.url)
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
        }

    }


    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_READY && playWhenReady) {
            sendMessageToViewModel(PlayAction.Resume)
        } else if (playbackState == Player.STATE_READY) {
            sendMessageToViewModel(PlayAction.Pause)
        } else if (playbackState == Player.STATE_BUFFERING) {
            sendMessageToViewModel(PlayAction.Buffering)
        } else if (playbackState == Player.STATE_IDLE) {
            sendMessageToViewModel(PlayAction.Idle)
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        sendMessageToViewModel(PlayAction.Error(null, error))
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


    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null

    private fun setupAudioFocus() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(audioAttributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener { focusChange ->
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
                .build()
        }
    }

    private fun requestAudioFocus(): Boolean {
        val result: Int =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
                audioManager.requestAudioFocus(audioFocusRequest!!)
            } else {
                audioManager.requestAudioFocus({ focusChange ->
                }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            }

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true
        }
        return false
    }


    private fun releaseAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && audioFocusRequest != null) {
            audioManager.abandonAudioFocusRequest(audioFocusRequest!!)
        } else {
            audioManager.abandonAudioFocus(null)
        }
    }


}


