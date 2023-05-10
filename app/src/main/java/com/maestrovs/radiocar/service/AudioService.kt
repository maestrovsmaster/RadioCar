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
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.google.android.exoplayer2.util.Log
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.main.MainActivity
import com.maestrovs.radiocar.ui.radio.PlayState
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

const val NOTIFICATION_REQUEST_CODE = 56465

@AndroidEntryPoint
class AudioPlayerService  : Service(),Player.Listener {

    /*val updatedPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_REQUEST_CODE,
        Intent(MainActivity.callingIntent(this)),
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // setting the mutability flag
    )*/

    private val binder = LocalBinder()
    private var exoPlayer: SimpleExoPlayer? = null

    private lateinit var playerNotificationManager: PlayerNotificationManager


   /* private fun updateForegroundState(isForeground: Boolean) {
        if (isForeground) {
            val notification = playerNotificationManager.createNotification()
            startForeground(1, notification)
        } else {
            stopForeground(false)
        }
    }*/

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
       // updateForegroundState(playWhenReady)
    }


    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        EventBus.getDefault().register(this)



        val channelId = 1231//"audio_player_channel"
        val channelName = "getString(R.string.audio_player_channel_name)"
        val channelDescription = "getString(R.string.audio_player_channel_description)"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId.toString(), channelName, importance).apply {
                description = channelDescription
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }



        playerNotificationManager = PlayerNotificationManager.Builder(


            this,
            channelId,
            channelId.toString(),
            object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return "Your Content Title"
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val notificationIntent = Intent(this@AudioPlayerService, MainActivity::class.java)
                    return PendingIntent.getActivity(this@AudioPlayerService, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return "Your Content Text"
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
                }
            }
        ).
        setNotificationListener(object : NotificationListener {
            override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                stopSelf()
            }

            override fun onNotificationPosted(notificationId: Int, notification: Notification, ongoing: Boolean) {
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


        playerNotificationManager.setUseNextAction(false)
        playerNotificationManager.setUsePreviousAction(false)
        exoPlayer?.addListener(this)




    }//onCreate








    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder? {
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


     fun playUrl(url: String){


         Log.d("ASD","Play exoPlayer = $exoPlayer")
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .build()


        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()



     }




    private fun pausePlayer(){
        exoPlayer?.pause()
    }

    private fun stopPlayer(){
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayUrlEvent(event: PlayUrlEvent) {
        event.url?.let {
            playUrl(event.url)

            when(event.playState){
                PlayState.Play -> playUrl(event.url)
                PlayState.Stop -> pausePlayer()
            }

        }?: kotlin.run {
            stopPlayer()
        }

    }










}


