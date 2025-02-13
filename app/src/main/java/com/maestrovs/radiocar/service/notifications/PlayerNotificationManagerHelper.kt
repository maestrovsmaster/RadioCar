package com.maestrovs.radiocar.service.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.Icon
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.compose.material3.Icon
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.offline.DownloadService.startForeground
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.service.ACTION_NEXT
import com.maestrovs.radiocar.service.ACTION_PAUSE
import com.maestrovs.radiocar.service.ACTION_PLAY
import com.maestrovs.radiocar.service.ACTION_PREV
import com.maestrovs.radiocar.service.ACTION_STOP
import com.maestrovs.radiocar.service.AudioPlayerService
import com.maestrovs.radiocar.service.NotificationStatus
import com.maestrovs.radiocar.utils.getBitmapFromDrawable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


class PlayerNotificationManagerHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val mediaSession: MediaSessionCompat =
        MediaSessionCompat(context, "AudioService").apply {
            isActive = true
        }

    @UnstableApi
    fun showNotification(
        notificationStatus: NotificationStatus,
        stationName: String?,
        imageBitmap: Bitmap?,
        songMetadata: String? = null
    ): Notification {

        val stopIntent =
            Intent(context, AudioPlayerService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(
            context,
            4,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val nextIntent =
            Intent(context, AudioPlayerService::class.java).apply { action = ACTION_NEXT }
        val nextPendingIntent = PendingIntent.getService(
            context,
            2,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val prevIntent =
            Intent(context, AudioPlayerService::class.java).apply { action = ACTION_PREV }
        val prevPendingIntent = PendingIntent.getService(
            context,
            3,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )


        val playIntent =
            Intent(context, AudioPlayerService::class.java).apply { action = ACTION_PLAY }
        val playPendingIntent = PendingIntent.getService(
            context,
            0,
            playIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val pauseIntent =
            Intent(context, AudioPlayerService::class.java).apply { action = ACTION_PAUSE }
        val pausePendingIntent = PendingIntent.getService(
            context,
            1,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val playPauseIcon = when (notificationStatus) {
            NotificationStatus.None -> R.drawable.ic_stop
            NotificationStatus.Play -> R.drawable.ic_pause
            NotificationStatus.Pause -> R.drawable.ic_play_xml
        }
        val playPauseAction = when (notificationStatus) {
            NotificationStatus.None -> pausePendingIntent
            NotificationStatus.Play -> pausePendingIntent
            NotificationStatus.Pause -> playPendingIntent
        }
        val playPauseText = when (notificationStatus) {
            NotificationStatus.None -> ""
            NotificationStatus.Play -> "Pause"
            NotificationStatus.Pause -> "Play"
        }

        val title = "Radio Car ${stationName ?: ""}"
        val subtitle = songMetadata ?: ""


        val builder = NotificationCompat.Builder(context, "audio_player_channel")
            .setSmallIcon(R.drawable.ic_play_png)
            .setContentTitle(title)
            .setContentText(subtitle)

            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)

        if(imageBitmap != null){
            builder.setLargeIcon(imageBitmap)
        }


        builder.addAction(
            NotificationCompat.Action(
                playPauseIcon, playPauseText, playPauseAction
            )
        )

        builder.addAction(
            NotificationCompat.Action(
                R.drawable.ic_prev, "Prev", prevPendingIntent
            )
        )





        builder.addAction(
            NotificationCompat.Action(
                R.drawable.ic_next, "Next", nextPendingIntent
            )
        )



      /*  builder.addAction(
            NotificationCompat.Action(
                R.drawable.ic_stop, "Stop", stopPendingIntent
            )
        )*/



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            builder.setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.sessionToken)
                    .setShowActionsInCompactView(0)
            )
        }

        return builder.build()

    }
}