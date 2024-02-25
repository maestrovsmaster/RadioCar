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
import com.maestrovs.radiocar.common.CurrentCountryManager
import com.maestrovs.radiocar.events.ActivityStatus
import com.maestrovs.radiocar.events.PlayEvent
import com.maestrovs.radiocar.events.PlayUrlEvent
import com.maestrovs.radiocar.events.UIStatusEvent
import com.maestrovs.radiocar.service.network.NetworkHelper
import com.maestrovs.radiocar.service.notifications.PlayerNotificationManagerHelper
import com.maestrovs.radiocar.service.player.AudioPlayerListener
import com.maestrovs.radiocar.service.player.ExoPlayerManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
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

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val binder = LocalBinder()

    private var activityStatus = ActivityStatus.VISIBLE


    override fun onCreate() {
        super.onCreate()

        EventBus.getDefault().register(this)

        Log.d("AudioPlayerService", "serviceModel = ${serviceModel}")

        exoPlayerManager.initializePlayer(object : AudioPlayerListener {
            override fun onPlayEvent(playAction: PlayAction) {
                Log.d("MainActivity22","onPlayEvent = ${playAction}")
                sendMessageToViewModel(playAction)

                if(activityStatus == ActivityStatus.INVISIBLE){
                    processAutonumnActions(playAction)
                }
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


    private fun processAutonumnActions(playAction: PlayAction){
      //  Log.d("MainActivity22","processAutonumnActions = ${playAction}")
        when(playAction){
            PlayAction.Next -> {
                serviceModel.getNextStation()?.let {
                    exoPlayerManager.onPlayUrlEvent(PlayUrlEvent(it.url,
                        it.name, null, it.favicon, PlayAction.Resume))
                }

            }

            PlayAction.Previous -> {
                 serviceModel.getPrevStation()?.let {
                    exoPlayerManager.onPlayUrlEvent(PlayUrlEvent(it.url,
                        it.name, null, it.favicon, PlayAction.Resume))
                }
            }
            PlayAction.Pause -> {

            }

            PlayAction.Resume -> {

            }
            is PlayAction.Error -> {
              Log.d("MainActivity22"," This is wrong station: find next!")
                  serviceModel.getNextStation()?.let {
                                    exoPlayerManager.onPlayUrlEvent(PlayUrlEvent(it.url,
                                        it.name, null, it.favicon, PlayAction.Resume))
                                }
            }
            else -> {}
        }
    }


    inner class LocalBinder : Binder() {
        fun getService(): AudioPlayerService = this@AudioPlayerService
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }


    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        serviceModel.clear()
        exoPlayerManager.releasePlayer()
        super.onDestroy()
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onPlayUrlEvent(event: PlayEvent) {
        if (event is UIStatusEvent) {
            Log.d("MainActivity22", "event = ${event}")
            activityStatus = event.activityStatus
            serviceScope.launch {

                if(event.activityStatus == ActivityStatus.INVISIBLE) {

                    val country = CurrentCountryManager.readCountry(this@AudioPlayerService)?.alpha2
                        ?: CurrentCountryManager.DEFAULT_COUNTRY
                    serviceModel.fetchStations(country, event.listType, event.station)
                }else{
                    val station = serviceModel.getCurrentStation()

                    station?.let {

                        EventBus.getDefault().post(PlayUrlEvent(station.url, station.name, "", station.favicon,
                            PlayAction.Resume))
                    }?:run {
                      //  EventBus.getDefault().post(PlayUrlEvent(null, null, "", null,
                       //     PlayAction.Pause))
                    }

                }
            }

        } else {
            exoPlayerManager.onPlayUrlEvent(event)
        }
    }

    private fun sendMessageToViewModel(playAction: PlayAction) {
        EventBus.getDefault().post(PlayActionEvent(playAction, exoPlayerManager.lastPlayUrlEvent))
    }
}


