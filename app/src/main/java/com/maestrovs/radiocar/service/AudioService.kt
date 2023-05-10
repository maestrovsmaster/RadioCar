package com.maestrovs.radiocar.service

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.core.net.toUri
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Log
import com.google.android.exoplayer2.util.Util
import dagger.Provides
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@AndroidEntryPoint
class AudioPlayerService  : Service() {

    private val binder = LocalBinder()
    private var exoPlayer: SimpleExoPlayer? = null


    override fun onCreate() {
        super.onCreate()
        initializePlayer()
        EventBus.getDefault().register(this)
    }

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
         //if(exoPlayer == null) initializePlayer()
         Log.d("ASD","Play exoPlayer = $exoPlayer")
        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .build()

        // player.setMediaSource(mediaSourceFactory, true)//setMediaSourceFactory(mediaSourceFactory)
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.play()
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
        playUrl(event.url)
    }
}


