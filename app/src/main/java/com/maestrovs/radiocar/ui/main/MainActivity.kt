package com.maestrovs.radiocar.ui.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.maestrovs.radiocar.databinding.ActivityMainBinding
import com.maestrovs.radiocar.service.AudioPlayerService
import com.maestrovs.radiocar.ui.radio.RadioViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {



    private var audioPlayerService: AudioPlayerService? = null
    private var serviceBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("ASD","onServiceConnected")
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.getService()

            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }



    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()




    companion object {
        fun callingIntent(context: Context) = Intent(context, MainActivity::class.java)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        audioPlayerService?.initializePlayer()


    }



    override fun onStart() {
        super.onStart()
        Intent(this, AudioPlayerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()

    }


    override fun onDestroy() {
        super.onDestroy()
    }

}