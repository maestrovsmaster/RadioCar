package com.maestrovs.radiocar.service.notifications

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.ui.main.MainActivity

class NotificationMediaAdapter(private val context: Context, private val currentTitle:(()->(String))):
    PlayerNotificationManager.MediaDescriptionAdapter  {
    override fun getCurrentContentTitle(player: Player): CharSequence {
        val title = currentTitle()
        return context.getString(R.string.symbol_radio) + " $title"
    }

    override fun createCurrentContentIntent(player: Player): PendingIntent? {
        val notificationIntent =
            Intent(context, MainActivity::class.java)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )
        } else {
            PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }
    }

    override fun getCurrentContentText(player: Player): CharSequence {

        return context.getString(R.string.notification_radio_description)
    }

    override fun getCurrentLargeIcon(
        player: Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        return BitmapFactory.decodeResource(
            context.resources,
            R.drawable.ic_launcher_background
        )
    }
}