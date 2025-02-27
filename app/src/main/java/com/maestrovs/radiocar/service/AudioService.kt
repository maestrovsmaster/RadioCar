package com.maestrovs.radiocar.service


import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.media3.common.util.UnstableApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.service.notifications.PlayerNotificationManagerHelper
import com.maestrovs.radiocar.service.player.AudioPlayerListener
import com.maestrovs.radiocar.service.player.ExoPlayerManager
import com.maestrovs.radiocar.manager.radio.PlayerState
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.manager.radio.PlaylistManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

const val NOTIFICATION_REQUEST_CODE = 56465

@UnstableApi
@AndroidEntryPoint
class AudioPlayerService : Service() {
    private val TAG = "AudioPlayerService"

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    @Inject
    lateinit var playerNotificationManagerHelper: PlayerNotificationManagerHelper

    @Inject
    lateinit var serviceModel: PlayerServiceModel

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val binder = LocalBinder()
    private var isForegroundService = false

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        observePlayerState()
        //observeBufferingState()
        observeSongMetadata()

       // Log.d(TAG, "Service created, serviceModel = $serviceModel")

        exoPlayerManager.initializePlayer(object : AudioPlayerListener {
            override fun onPlayEvent(playAction: PlayAction) {

                when (playAction) {
                    is PlayAction.Buffering -> {
                        PlayerStateManager.setBuffering(playAction.isBuffering)
                    }

                    is PlayAction.Error -> {}
                    PlayAction.Idle -> {}
                    PlayAction.Next -> {
                        PlaylistManager.next()}
                    PlayAction.Pause -> PlayerStateManager.pause()//Receiver commands from Bluetooth

                    PlayAction.Previous -> {PlaylistManager.prev()}
                    PlayAction.Resume -> PlayerStateManager.play()//Receiver commands from Bluetooth
                }
            }

            override fun onSetAudioPlayerSessionId(sessionId: Int) {
                PlayerStateManager.setAudioSessionId(sessionId)
            }

            override fun onSongMetadata(metadata: String) {
                PlayerStateManager.setSongMetadata(metadata)
            }
        })



        // Запускаємо Foreground Service одразу
        startForegroundService()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        //There are commands from Notification bar
        when (intent?.action) {
            ACTION_PLAY -> {
                PlayerStateManager.play()
            }

            ACTION_PAUSE -> {
                PlayerStateManager.pause()

            }

            ACTION_NEXT -> {
                PlaylistManager.next()

            }

            ACTION_PREV -> {
                PlaylistManager.prev()
            }
        }
        return START_STICKY
    }


    private fun startForegroundService() {
        val notification =
            playerNotificationManagerHelper.showNotification(NotificationStatus.Pause, null, null)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
        isForegroundService = true
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "audio_player_channel",
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls media playback"
                setShowBadge(false)
                enableLights(false)
                enableVibration(false)
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }


    private fun updateNotification(isPlaying: Boolean, stationName: String?, imageBitmap: Bitmap?, songMetadata: String? = null) {

        val status = if (isPlaying) {
            NotificationStatus.Play
        } else {
            NotificationStatus.Pause
        }
        val notification =
            playerNotificationManagerHelper.showNotification(status, stationName, imageBitmap, songMetadata)

        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(1, notification) // ✅ Оновлення нотифікації без `startForeground()`

    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        serviceModel.clear()
        // mediaSessionHelper.release()
        exoPlayerManager.releasePlayer()
        stopForeground(true)
        super.onDestroy()
    }

    private fun observeGroupBitmap() {

        serviceScope.launch {
            PlayerStateManager.bitmapFlow.collect { bitmap ->
                val state = PlayerStateManager.playerState.value
                updateNotification(state.isPlaying, state.currentGroup?.name, bitmap, state.songMetadata)
            }
        }
    }

    private fun observeSongMetadata() {

        serviceScope.launch {
            PlayerStateManager.songMetadataFlow.collect { metadata ->
                val state = PlayerStateManager.playerState.value
                updateNotification(state.isPlaying, state.currentGroup?.name, state.bitmap, state.songMetadata)
            }
        }
    }

    private fun observePlayerState() {
        serviceScope.launch {
            PlayerStateManager.isPlayingFlow.collect { isPlaying ->
                if (isPlaying.first) {
                    startPlaying(PlayerStateManager.playerState.value)
                } else {
                    stopPlaying(PlayerStateManager.playerState.value)
                }
            }
        }
    }

    private fun startPlaying(state: PlayerState) {
        val group = state.currentGroup ?: return
     //   val stream = group.streams.getOrNull(state.currentStationIndex)

        val preferredBitrate = state.preferredBitrateOption
        var stream = state.currentGroup.streams.getOrNull(0)
        state.currentGroup.streams.forEach {
            if (it.bitrate == preferredBitrate) {
                stream = it
            }
        }
        stream?.let {

            serviceScope.launch(Dispatchers.Main) {
                //val problemUrl = "https://765211.live.tvstitch.com/stream.m3u8?&m=aHR0cHM6Ly9zdHJlYW12aWRlby5sdXhuZXQudWEvbHV4X2Fkdi9tYXN0ZXIubTN1OA==&u=&channel=luxfm"
                exoPlayerManager.playUrl(it.url)
            }
            //val state = PlayerStateManager.playerState.value
            updateNotification(state.isPlaying, state.currentGroup?.name, state.bitmap, state.songMetadata)


            if (group.favicon != null) {
                Glide.with(this)
                    .asBitmap()
                    .load(group.favicon)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            PlayerStateManager.setBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {}
                    })
            }


        }
    }


    private fun stopPlaying(state: PlayerState) {
        val group = state.currentGroup ?: return
        serviceScope.launch(Dispatchers.Main) {
            exoPlayerManager.stopPlayer()
        }
        updateNotification(state.isPlaying, state.currentGroup?.name, state.bitmap, state.songMetadata)
    }


    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder = binder


}
