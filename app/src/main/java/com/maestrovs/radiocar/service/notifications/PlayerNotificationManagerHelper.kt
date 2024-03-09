package com.maestrovs.radiocar.service.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.service.player.ExoPlayerManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerNotificationManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {

     private lateinit var playerNotificationManager: PlayerNotificationManager
    fun initialize(exoPlayerManager: ExoPlayerManager,
                   notificationListener: PlayerNotificationManager.NotificationListener
                   ){

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
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        playerNotificationManager = com.google.android.exoplayer2.ui.PlayerNotificationManager.Builder(context,
            channelId, channelId.toString(),
            NotificationMediaAdapter(context) {
                exoPlayerManager.lastPlayUrlEvent?.let {
                    it.name ?: ""
                } ?: run { "" }
            }
        ).setNotificationListener(notificationListener).build()

        playerNotificationManager.setPlayer(exoPlayerManager.exoPlayer)
        playerNotificationManager.setUseStopAction(true)
        playerNotificationManager.setColor(
            ResourcesCompat.getColor(
                context.resources,
                R.color.blue,
                null
            )
        )
        playerNotificationManager.setUseNextAction(false)
        playerNotificationManager.setUsePreviousAction(false)
    }
}