package com.maestrovs.radiocar.ui.main

import android.Manifest
import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.StrictMode
import android.util.Log
import android.util.Rational
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.maestrovs.radiocar.BuildConfig
import com.maestrovs.radiocar.R
import com.maestrovs.radiocar.data.remote.weather.WeatherWorker
import com.maestrovs.radiocar.databinding.ActivityMainBinding
import com.maestrovs.radiocar.events.ActivityStatus
import com.maestrovs.radiocar.manager.bluetooth.BluetoothStateManager
import com.maestrovs.radiocar.manager.bluetooth.StateSender
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.service.ACTION_NEXT
import com.maestrovs.radiocar.service.ACTION_PAUSE
import com.maestrovs.radiocar.service.ACTION_PLAY
import com.maestrovs.radiocar.service.ACTION_PREV
import com.maestrovs.radiocar.service.AudioPlayerService
import com.maestrovs.radiocar.service.bluetooth.BluetoothReceiverManager
import com.maestrovs.radiocar.service.bluetooth.getActiveBluetoothAudioDevice
import com.maestrovs.radiocar.service.bluetooth.getBluetoothAdapter
import com.maestrovs.radiocar.service.location.LocationManager
import com.maestrovs.radiocar.shared_managers.SettingsManager
import com.maestrovs.radiocar.ui.app.pip_screen.PiPFragment
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.LaunchViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.WeatherViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


@UnstableApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var audioPlayerService: AudioPlayerService? = null
    private var serviceBound = false


    private val scope = lifecycleScope





    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.getService()
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }


    private var binding: ActivityMainBinding? = null

    private val mainViewModel: MainViewModel by viewModels()
    private val radioViewModel: RadioViewModel by viewModels()
    private val launchViewModel: LaunchViewModel by viewModels()

    private val weatherViewModel: WeatherViewModel by viewModels()

    companion object {

        const val PERMISSIONS_REQUEST_LOCATION = 124
        const val PERMISSIONS_REQUEST_BLUETOOTH = 145
        fun callingIntent(context: Context) = Intent(context, MainActivity::class.java)
    }


    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        newConfig: Configuration
    ) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig)

        Log.d("MainActivityPip", "onPictureInPictureModeChanged: $isInPictureInPictureMode")

        val navHostFragment = findViewById<View>(R.id.nav_host_fragment_activity_main)
        val pipFragmentContainer = findViewById<View>(R.id.pip_fragment_container)

        if (isInPictureInPictureMode) {
            // Ховаємо основний UI і показуємо PiP-фрагмент
            navHostFragment.visibility = View.GONE
            pipFragmentContainer.visibility = View.VISIBLE

            supportFragmentManager.beginTransaction()
                .replace(R.id.pip_fragment_container, PiPFragment())
                .commit()
        } else {
            // Повертаємося до стандартного UI
            pipFragmentContainer.visibility = View.GONE
            navHostFragment.visibility = View.VISIBLE
        }
    }

    fun enterPiPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val params = PictureInPictureParams.Builder().setAspectRatio(Rational(16, 9))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                params.setSeamlessResizeEnabled(true)
            }
            enterPictureInPictureMode(params.build())
            //setPictureInPictureParams(params.build())
        }
    }



    fun updatePiPControls(isPlaying: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isInPictureInPictureMode) {
            val playIntent = Intent(this, AudioPlayerService::class.java).apply {
                action = if (isPlaying) ACTION_PAUSE else ACTION_PLAY
            }
            val playPendingIntent = PendingIntent.getService(
                this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val playPauseIcon =
                if (isPlaying) R.drawable.ic_pause_24dp else R.drawable.ic_play_arrow_24dp

            val actionPlay = RemoteAction(
                Icon.createWithResource(this, playPauseIcon),
                if (isPlaying) "Pause" else "Play",
                if (isPlaying) "Pause" else "Play",
                playPendingIntent
            )

            val nextIntent = Intent(this, AudioPlayerService::class.java).apply {
                action = ACTION_NEXT
            }
            val nextPendingIntent = PendingIntent.getService(
                this, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val actionNext = RemoteAction(
                Icon.createWithResource(this, R.drawable.ic_next_24png),
                "Next","Next",
                nextPendingIntent
            )

            val prevIntent = Intent(this, AudioPlayerService::class.java).apply {
                action = ACTION_PREV
            }
            val prevPendingIntent = PendingIntent.getService(
                this, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val actionPrev = RemoteAction(
                Icon.createWithResource(this, R.drawable.ic_prev24png),
                "Prev","Prev",
                prevPendingIntent
            )


            val params = PictureInPictureParams.Builder()
                .setAspectRatio(Rational(16, 9))
                .setActions(listOf(actionPrev, actionPlay, actionNext))
                .build()

            setPictureInPictureParams(params) // Оновлюємо PiP
        }
    }

   /* override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPiPMode()
            updatePiPControls(isPlayingFlow.value ?: false) // Оновлюємо PiP при вході
        }
    }*/



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivityPip", "onCreate")
        PlayerStateManager.isPlayingFlow.asLiveData().observe(this) { isPlaying ->
            updatePiPControls(isPlaying.first)
        }

        if (BuildConfig.DEBUG) {
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedClosableObjects()
                    .detectLeakedRegistrationObjects()
                    .penaltyLog()
                    //.penaltyDeath() //!! will be crash
                    .build()
            )
        }

        WeatherWorker.startWeatherWorker(this)

        // startMockLocationUpdates(this, scope) //Emulate location speed change

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)


        FirebaseApp.initializeApp(this);
        MobileAds.initialize(
            this
        ) { p0 -> Log.d("AdMob", "InitStatus = ${p0.adapterStatusMap}") }


        if (SettingsManager.isDisplayActive(this)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
        } else {
            LocationManager.startLocationUpdates(this)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                556//RECORD_AUDIO_PERMISSION_REQUEST_CODE
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT),
                    PERMISSIONS_REQUEST_BLUETOOTH
                )
            } else {
                initBluetoothLogic()
            }
        } else {
            initBluetoothLogic()
        }

        mainViewModel.mustRefreshStatus.observe(this) {
            applySettingsChanges()
        }


        lifecycleScope.launch {
            PlayerStateManager.audioSessionIdFlow.collectLatest { sessionId ->
                sessionId?.let {
                    AudioVisualizerManager.initVisualizer(it)
                }
            }
        }
    }


    private fun initBluetoothLogic() {
        val viewModelState = launchViewModel.firstTimeLaunch.value
        val stateSender = if (viewModelState) StateSender.Initial else StateSender.Activity
        getBluetoothAdapter(this)?.let {
            BluetoothReceiverManager.registerReceiver(this)

            radioViewModel.setBluetoothState(it.state)
            getActiveBluetoothAudioDevice(this@MainActivity) { dev ->
                BluetoothStateManager.setCurrentBluetoothDevice(this@MainActivity, dev, stateSender)
            }

        } ?: run {
            BluetoothStateManager.setBluetoothState(this, BluetoothAdapter.STATE_OFF, stateSender)
            radioViewModel.setBluetoothState(BluetoothAdapter.STATE_OFF)
        }
    }


    @OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()
        if (!serviceBound) {
            Intent(this, AudioPlayerService::class.java).also { intent ->
                bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.updateActivityStatus(ActivityStatus.VISIBLE)
        BluetoothReceiverManager.registerReceiver(this)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && isInPictureInPictureMode) {
            onPictureInPictureModeChanged(true, resources.configuration)
        } else {
            onPictureInPictureModeChanged(false, resources.configuration)
        }
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.updateActivityStatus(ActivityStatus.INVISIBLE)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the receiver
        BluetoothReceiverManager.unregisterReceiver(this)
    }


    override fun onDestroy() {
        super.onDestroy()
        BluetoothReceiverManager.unregisterReceiver(this)
        scope.cancel()
        if (serviceBound) {
            unbindService(serviceConnection)
            serviceBound = false
        }
        binding = null
    }


    // Handling permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationManager.startLocationUpdates(this)
            } else {
                // Permission denied, show a message or disable features that require the permission.
            }
        }

        if (requestCode == PERMISSIONS_REQUEST_BLUETOOTH) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //     bluetoothStatusReceiver = BluetoothStatusReceiver()
                initBluetoothLogic()
            }
        }
    }


    private fun applySettingsChanges() {
        keepScreenActive(SettingsManager.isDisplayActive(this))
    }

    private fun keepScreenActive(shouldKeepScreenActive: Boolean) {
        if (shouldKeepScreenActive) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }


}