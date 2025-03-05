package com.maestrovs.radiocar.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.manager.radio.PlaylistManager
import com.maestrovs.radiocar.service.ACTION_NEXT
import com.maestrovs.radiocar.service.ACTION_PAUSE
import com.maestrovs.radiocar.service.ACTION_PLAY
import com.maestrovs.radiocar.service.ACTION_PREV

/**
 * Created by maestromaster$ on 04/03/2025$.
 */

class PipActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            ACTION_PLAY -> {
                PlayerStateManager.play()
            }
            ACTION_PAUSE -> {
                PlayerStateManager.pause()
            }
            ACTION_PREV -> {
                PlaylistManager.prev()
            }
            ACTION_NEXT -> {
                PlaylistManager.next()
            }
        }
    }
}
