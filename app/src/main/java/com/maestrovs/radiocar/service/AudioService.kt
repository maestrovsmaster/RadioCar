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
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.content.res.ResourcesCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.google.android.exoplayer2.util.Log
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.enums.PlayAction
import com.maestrovs.radiocar.enums.PlayState
import com.maestrovs.radiocar.events.PlayActionEvent
import com.maestrovs.radiocar.events.PlayUrlEvent
import com.maestrovs.radiocar.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

const val NOTIFICATION_REQUEST_CODE = 56465

@AndroidEntryPoint
class AudioPlayerService : Service(), Player.Listener {


    private val binder = LocalBinder()
    private var exoPlayer: SimpleExoPlayer? = null

    private lateinit var playerNotificationManager: PlayerNotificationManager


    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {

        sendMessageToViewModel(
            if (playWhenReady) {
                PlayAction.Resume
            } else {
                PlayAction.Pause
            }
        )
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        // Handle playback state changes here
        sendMessageToViewModel(PlayAction.Previous)
    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        super.onPositionDiscontinuity(oldPosition, newPosition, reason)
        if (reason == Player.DISCONTINUITY_REASON_AUTO_TRANSITION) {
            // The player has moved to the next item
            sendMessageToViewModel(PlayAction.Next)
        }
    }




    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        EventBus.getDefault().register(this)


        val channelId = 1231//"audio_player_channel"
        val channelName = "RadioCarChannel"//"getString(R.string.audio_player_channel_name)"
        val channelDescription = "RadioCarChannelDescription"//"getString(R.string.audio_player_channel_description)"

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
                        title = it.name ?:""
                    }

                    return    getString(R.string.symbol_radio)+" $title"
                }





                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val notificationIntent =
                        Intent(this@AudioPlayerService, MainActivity::class.java)
                    return PendingIntent.getActivity(
                        this@AudioPlayerService,
                        0,
                        notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                    )
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
        })


            .build()




        playerNotificationManager.setPlayer(exoPlayer)
        // playerNotificationManager.setUsePlayPauseActions(true)
        playerNotificationManager.setUseStopAction(true)

        playerNotificationManager.setColor(ResourcesCompat.getColor(this.resources, R.color.pink_gray, null))



        playerNotificationManager.setUseNextAction(false)
        playerNotificationManager.setUsePreviousAction(false)
        exoPlayer?.addListener(this)


    }//onCreate


    private fun sendMessageToViewModel(playAction: PlayAction) {
        EventBus.getDefault().post(PlayActionEvent(playAction,lastPlayUrlEvent))
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
    }

    var lastPlayUrlEvent: PlayUrlEvent? = null

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayUrlEvent(event: PlayUrlEvent) {
        lastPlayUrlEvent = event
        event.url?.let {
            playUrl(event.url)

            when (event.playState) {
                PlayState.Play -> playUrl(event.url)
                PlayState.Stop -> pausePlayer()
            }

        } ?: kotlin.run {
            stopPlayer()
        }

    }





}


