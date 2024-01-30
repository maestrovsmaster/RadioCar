package com.maestrovs.radiocar.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Binder
import android.os.IBinder
import com.google.android.exoplayer2.ui.PlayerNotificationManager.NotificationListener
import com.maestrovs.radiocar.enums.radio.PlayAction
import com.maestrovs.radiocar.events.PlayActionEvent
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


import android.util.Log
import com.maestrovs.radiocar.events.PlayEvent
import com.maestrovs.radiocar.service.network.NetworkHelper
import com.maestrovs.radiocar.service.notifications.PlayerNotificationManagerHelper
import com.maestrovs.radiocar.service.player.AudioPlayerListener
import com.maestrovs.radiocar.service.player.ExoPlayerManager
import javax.inject.Inject

const val NOTIFICATION_REQUEST_CODE = 56465

@AndroidEntryPoint
class AudioPlayerService : Service() {
    val TAG = "AudioPlayerService"

    @Inject
    lateinit var exoPlayerManager: ExoPlayerManager

    @Inject
    lateinit var playerNotificationManagerHelper: PlayerNotificationManagerHelper

    @Inject
    lateinit var serviceModel: PlayerServiceModel

    private val binder = LocalBinder()


    override fun onCreate() {
        super.onCreate()

        EventBus.getDefault().register(this)

        Log.d("AudioPlayerService","serviceModel = ${serviceModel}")

        exoPlayerManager.initializePlayer(object : AudioPlayerListener {
            override fun onPlayEvent(playAction: PlayAction) {
                sendMessageToViewModel(playAction)
            }
        })

        playerNotificationManagerHelper.initialize(exoPlayerManager,
            object : NotificationListener {
                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
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
            }
        )



        NetworkHelper(this@AudioPlayerService, object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                Log.e(TAG, "Lost network connection")
            }

            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                Log.e(TAG, "Network connection available again")
            }
        })


    }//onCreate





    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
        exoPlayerManager.releasePlayer()

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayUrlEvent(event: PlayEvent) {
        exoPlayerManager.onPlayUrlEvent(event)
    }

    private fun sendMessageToViewModel(playAction: PlayAction) {
        EventBus.getDefault().post(PlayActionEvent(playAction, exoPlayerManager.lastPlayUrlEvent))
    }
}


