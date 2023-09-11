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


const val NOTIFICATION_REQUEST_CODE = 56465

@AndroidEntryPoint
class AudioPlayerService : Service(), Player.Listener {

    val TAG = "AudioPlayerService"


    private val binder = LocalBinder()
    private var exoPlayer: SimpleExoPlayer? = null

    private lateinit var playerNotificationManager: PlayerNotificationManager

    private lateinit var mediaSession: MediaSessionCompat //Class for controll bluetooth hardware buttons clicking


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



        playerNotificationManager = PlayerNotificationManager.Builder(


            this,
            channelId,
            channelId.toString(),
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
                    /*return PendingIntent.getActivity(
                        this@AudioPlayerService,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )*/

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
        // playerNotificationManager.setUsePlayPauseActions(true)
        playerNotificationManager.setUseStopAction(true)

        playerNotificationManager.setColor(
            ResourcesCompat.getColor(
                this.resources,
                R.color.pink_gray,
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
                    // Play media
                    android.util.Log.d("BluetoothDevice", "Bluetooth play")
                    lastPlayUrlEvent?.url?.let {
                        playUrl(it)
                    }
                    sendMessageToViewModel(PlayAction.Resume)
                }

                override fun onPause() {
                    super.onPause()
                    // Pause media
                    android.util.Log.d("BluetoothDevice", "Bluetooth pause")
                    pausePlayer()
                    sendMessageToViewModel(PlayAction.Pause)
                }


                override fun onSkipToNext() {
                    super.onSkipToNext()
                    // Next media
                    android.util.Log.d("BluetoothDevice", "Bluetooth next")
                }

                override fun onSkipToPrevious() {
                    super.onSkipToPrevious()
                    // Previous media
                    android.util.Log.d("BluetoothDevice", "Bluetooth previous")
                }
            })

            // Other media session configurations...
        }


        //mediaSession.setCallback(MediaSessionCallback())
        mediaSession.setMediaButtonReceiver(pendingIntent)

        val playbackState = PlaybackStateCompat.Builder()
            .setActions(PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE)
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
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .build()


        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()


    }


    private fun pausePlayer() {
        exoPlayer?.pause()
    }

    private fun stopPlayer() {
        exoPlayer?.stop()
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

    var lastPlayUrlEvent: PlayUrlEvent? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayUrlEvent(event: PlayUrlEvent) {
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


    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_READY && playWhenReady) {
            Log.d(TAG, "Player is playing")
            sendMessageToViewModel(PlayAction.Resume)
        } else if (playbackState == Player.STATE_READY) {
            Log.d(TAG, "Player is paused")
            sendMessageToViewModel(PlayAction.Pause)
        } else if (playbackState == Player.STATE_BUFFERING) {
            Log.d(TAG, "Player is buffering")
            sendMessageToViewModel(PlayAction.Buffering)
        } else if (playbackState == Player.STATE_IDLE) {
            Log.d(TAG, "Player is idle")
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


}


