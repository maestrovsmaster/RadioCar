package com.maestrovs.radiocar

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.maestrovs.radiocar.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory



//import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var  player: ExoPlayer


    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    companion object {
        fun callingIntent(context: Context) = Intent(context, MainActivity::class.java)
    }

    lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initPlayer()



        mainViewModel.selectedStation.observe(this){station ->
            Log.d("ASDASD"," station = ${station?.name}     url = ${station?.url}"    )
            Log.d("ASDASD"," stationDescr = ${station} ")

            stopRadio()

            station?.let {
                val radioUrl = it.url
                playRadio(radioUrl)
            }



        }

    }


    private fun initPlayer(){
         player = SimpleExoPlayer.Builder(this).build()

        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaSourceFactory = DefaultMediaSourceFactory(dataSourceFactory)
    }


    private fun playRadio(url: String) {

        val mediaItem = MediaItem.Builder()
            .setUri(Uri.parse(url))
            .build()

       // player.setMediaSource(mediaSourceFactory, true)//setMediaSourceFactory(mediaSourceFactory)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    private fun stopRadio(){
        player.stop()
    }



}