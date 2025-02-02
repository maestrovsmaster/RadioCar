package com.maestrovs.radiocar.ui.main

import android.Manifest
import android.Manifest.permission.BLUETOOTH_CONNECT
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.FirebaseApp
import com.maestrovs.radiocar.databinding.ActivityMainBinding
import com.maestrovs.radiocar.enums.bluetooth.BT_Status
import com.maestrovs.radiocar.events.ActivityStatus
import com.maestrovs.radiocar.service.AudioPlayerService
import com.maestrovs.radiocar.ui.settings.SettingsManager
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity"


    private var audioPlayerService: AudioPlayerService? = null
    private var serviceBound = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var bluetoothStatusReceiver: BroadcastReceiver


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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }


        if (bluetoothAdapter != null) {

            val isBluetoothEnabled = bluetoothAdapter.isEnabled
            when (isBluetoothEnabled) {
                true -> mainViewModel.setBluetoothStatus(BT_Status.Enabled)
                false -> mainViewModel.setBluetoothStatus(BT_Status.Disable)
            }
            registerBluetoothStatusReceiver()

            checkConnectedBluetoothDevices()

        }


        mainViewModel.mustRefreshStatus.observe(this) {
            applySettingsChanges()
        }

            /*
        if (!CurrentCountryManager.isAskCountry(this)) {
            launchCountryPickerDialog { country: CPCountry? ->
                val newSelectedCountry = country ?: return@launchCountryPickerDialog
                Log.d("Country", "CountryCode = $newSelectedCountry")
                // binding.tvSelectedCountry.text =  "${newSelectedCountry.flagEmoji} ${newSelectedCountry.name}"
                CurrentCountryManager.writeCountry(this, newSelectedCountry)
                mainViewModel.setMustRefreshStatus()
            }
            CurrentCountryManager.setAskedCountryTrue(this)
        }*/
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


    private fun registerBluetoothStatusReceiver() {
        bluetoothStatusReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("BluetoothDevice", "onReceive intent?.action = ${intent?.action}")

                val action = intent?.action ?: return
                when (action) {
                    BluetoothAdapter.ACTION_STATE_CHANGED -> {
                        when (intent.getIntExtra(
                            BluetoothAdapter.EXTRA_STATE,
                            BluetoothAdapter.ERROR
                        )) {
                            BluetoothAdapter.STATE_ON -> mainViewModel.setBluetoothStatus(BT_Status.Enabled)
                            BluetoothAdapter.STATE_OFF -> mainViewModel.setBluetoothStatus(BT_Status.Disable)
                        }
                    }

                    BluetoothDevice.ACTION_ACL_CONNECTED -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        mainViewModel.setBluetoothStatus(BT_Status.ConnectedDevice)
                        if (SettingsManager.isAutoplay(this@MainActivity)) {
                            //   mainViewModel.playCurrentStationState()
                        }
                    }

                    BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                        val device: BluetoothDevice? =
                            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        mainViewModel.setBluetoothStatus(BT_Status.DisconnectedDevice)
                        mainViewModel.stopCurrentStationState()
                    }
                }
            }
        }

        // Register the broadcast receiver
        // val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        val intentFilter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        }
        registerReceiver(bluetoothStatusReceiver, intentFilter)
    }


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
                registerBluetoothStatusReceiver()
            }
        }
    }

    /*override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            ExitDialog(this).show()
            false
        } else super.onKeyDown(keyCode, event)
    }*/


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