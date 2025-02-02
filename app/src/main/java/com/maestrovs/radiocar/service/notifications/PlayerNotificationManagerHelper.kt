package com.maestrovs.radiocar.service.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.media.session.MediaSessionCompat
import androidx.core.app.NotificationCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService.startForeground

import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.service.AudioPlayerService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class PlayerNotificationManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val mediaSession: MediaSessionCompat = MediaSessionCompat(context, "AudioService")

    @UnstableApi
    fun showNotification(isPlaying: Boolean): Notification {
        // Правильний `PendingIntent` для Play
        val playIntent = Intent(context, AudioPlayerService::class.java).apply {
            action = "ACTION_PLAY"
        }
        val playPendingIntent = PendingIntent.getService(
            context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Правильний `PendingIntent` для Pause
        val pauseIntent = Intent(context, AudioPlayerService::class.java).apply {
            action = "ACTION_PAUSE"
        }
        val pausePendingIntent = PendingIntent.getService(
            context, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Вибір іконки в залежності від стану плеєра
        val playPauseIcon = if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play_xml
        val playPauseAction = if (isPlaying) pausePendingIntent else playPendingIntent

        return NotificationCompat.Builder(context, "audio_player_channel")
            .setSmallIcon(R.drawable.ic_play_png) // Використовуй коректну іконку!
            .setContentTitle("Now Playing")
            .setContentText("")//Track name here
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken) // ✅ Обов’язково
                    .setShowActionsInCompactView(0)
            )
            .addAction(
                NotificationCompat.Action(
                    playPauseIcon, "Play/Pause", playPauseAction
                )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH) // ✅ Щоб кнопки точно не ховалися
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC) // ✅ Дозволяє показувати медіаконтролери
            .setOngoing(true)
            .build()
    }
}



    /*fun showNotification() {
        val mediaSession = MediaSessionCompat(context, "AudioService")
        val builder = NotificationCompat.Builder(context, "audio_player_channel")
            //.setSmallIcon(R.drawable.ic_music_note)
            .setContentTitle("Now Playing")
            .setContentText(player.currentMediaItem?.mediaMetadata?.title ?: "Unknown Track")
            .setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0)
            )
            .addAction(
                NotificationCompat.Action(
                    R.drawable.pause_to_play,
                    "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context, PlaybackStateCompat.ACTION_PAUSE
                    )
                )
            )
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        notificationManager?.notify(1, builder.build())
    }*/
//}



/*
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
}*/