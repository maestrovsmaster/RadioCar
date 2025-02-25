package com.maestrovs.radiocar.ui.main

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.maestrovs.radiocar.databinding.ActivityMainBinding
import com.maestrovs.radiocar.events.ActivityStatus
import com.maestrovs.radiocar.manager.bluetooth.BluetoothStateManager
import com.maestrovs.radiocar.manager.bluetooth.StateSender
import com.maestrovs.radiocar.manager.location.startMockLocationUpdates
import com.maestrovs.radiocar.service.AudioPlayerService
import com.maestrovs.radiocar.service.bluetooth.BluetoothReceiverManager
import com.maestrovs.radiocar.service.bluetooth.getActiveBluetoothAudioDevice
import com.maestrovs.radiocar.service.bluetooth.getBluetoothAdapter
import com.maestrovs.radiocar.service.location.LocationManager
import com.maestrovs.radiocar.shared_managers.SettingsManager
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.LaunchViewModel
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.RadioViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel


@UnstableApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var audioPlayerService: AudioPlayerService? = null
    private var serviceBound = false


    private val scope = CoroutineScope(Dispatchers.Default)


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


    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()
    private val radioViewModel: RadioViewModel by viewModels()
    private val launchViewModel: LaunchViewModel by viewModels()

    companion object {

         const val PERMISSIONS_REQUEST_LOCATION = 124
         const val PERMISSIONS_REQUEST_BLUETOOTH = 145
        fun callingIntent(context: Context) = Intent(context, MainActivity::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

       // startMockLocationUpdates(scope)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }


    private fun initBluetoothLogic() {
        val viewModelState = launchViewModel.firstTimeLaunch.value
        val stateSender = if(viewModelState) StateSender.Initial else StateSender.Activity
        getBluetoothAdapter(this)?.let {
            BluetoothReceiverManager.registerReceiver(this)

                radioViewModel.setBluetoothState(it.state)
            getActiveBluetoothAudioDevice(this@MainActivity){ dev ->
                BluetoothStateManager.setCurrentBluetoothDevice(this@MainActivity,dev, stateSender)
            }

        }?:run {
            BluetoothStateManager.setBluetoothState(this, BluetoothAdapter.STATE_OFF, stateSender)
            radioViewModel.setBluetoothState(BluetoothAdapter.STATE_OFF)
        }
    }


    @OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()
        Intent(this, AudioPlayerService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.updateActivityStatus(ActivityStatus.VISIBLE)
        BluetoothReceiverManager.registerReceiver(this)
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