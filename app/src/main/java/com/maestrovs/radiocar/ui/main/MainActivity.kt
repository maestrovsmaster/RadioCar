package com.maestrovs.radiocar.ui.main

import android.Manifest
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothProfile
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.maestrovs.radiocar.bluetooth.BluetoothStatusReceiver
import com.maestrovs.radiocar.databinding.ActivityMainBinding
import com.maestrovs.radiocar.enums.bluetooth.BT_Status
import com.maestrovs.radiocar.events.ActivityStatus
import com.maestrovs.radiocar.service.AudioPlayerService
import com.maestrovs.radiocar.ui.settings.SettingsManager
import dagger.hilt.android.AndroidEntryPoint


@UnstableApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var audioPlayerService: AudioPlayerService? = null
    private var serviceBound = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private  var bluetoothStatusReceiver: BluetoothStatusReceiver? = null


    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Log.d("AudioPlayerService", "onServiceConnected")
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.getService()
            serviceBound = true
           // audioPlayerService?.initializePlayer()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            serviceBound = false
        }
    }


    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels()

    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()


    companion object {

        private const val PERMISSIONS_REQUEST_LOCATION = 124
        private const val PERMISSIONS_REQUEST_BLUETOOTH = 145
        fun callingIntent(context: Context) = Intent(context, MainActivity::class.java)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity22","MainActivity_onCreate")

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
            startLocationUpdates()
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


        if (bluetoothAdapter != null) {

            val isBluetoothEnabled = bluetoothAdapter.isEnabled
            when (isBluetoothEnabled) {
                true -> mainViewModel.setBluetoothStatus(BT_Status.Enabled)
                false -> mainViewModel.setBluetoothStatus(BT_Status.Disable)
            }
            bluetoothStatusReceiver = BluetoothStatusReceiver()

            checkConnectedBluetoothDevices()
        }

        mainViewModel.mustRefreshStatus.observe(this) {
            applySettingsChanges()
        }
    }


    private fun checkConnectedBluetoothDevices() {

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_DENIED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(BLUETOOTH_CONNECT),
                    PERMISSIONS_REQUEST_BLUETOOTH
                )
                return
            }
        }

        val isBluetoothEnabled = bluetoothAdapter.isEnabled
        if (isBluetoothEnabled) {
            // Get a list of connected devices
            bluetoothAdapter.getProfileProxy(
                applicationContext,
                object : BluetoothProfile.ServiceListener {
                    override fun onServiceDisconnected(profile: Int) {}
                    override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {

                        val connectedDevices = proxy.connectedDevices

                        if (connectedDevices.size > 0) {
                            if (SettingsManager.isAutoplay(this@MainActivity)) {
                                Handler(Looper.getMainLooper()).postDelayed({
                                    mainViewModel.playCurrentStationState()
                                }, 1000)
                            }
                        }
                        // Don't forget to close the proxy!
                        bluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET, proxy)
                    }
                },
                BluetoothProfile.HEADSET
            )
        } else {
            println("Bluetooth is not enabled.")

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
        Log.d("MainActivity22","MainActivity_onResume")
        mainViewModel.updateActivityStatus(ActivityStatus.VISIBLE)
    }

    override fun onStop() {
        super.onStop()
        mainViewModel.updateActivityStatus(ActivityStatus.INVISIBLE)
    }


    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(bluetoothStatusReceiver)
    }


    private fun startLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.create().apply {
            interval = 1000 // Update interval in milliseconds
            fastestInterval = 500 // Fastest update interval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                for (location in locationResult.locations) {
                    mainViewModel.updateLacationAndSpeed(location)
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
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
                startLocationUpdates()
            } else {
                // Permission denied, show a message or disable features that require the permission.
            }
        }

        if (requestCode == PERMISSIONS_REQUEST_BLUETOOTH) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bluetoothStatusReceiver = BluetoothStatusReceiver()
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