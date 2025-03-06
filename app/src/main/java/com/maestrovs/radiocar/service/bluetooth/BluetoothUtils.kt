package com.maestrovs.radiocar.service.bluetooth

/**
 * Created by maestromaster$ on 24/02/2025$.
 */

import android.Manifest
import android.bluetooth.BluetoothA2dp
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass.Device.Major
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

fun getBluetoothAdapter(context: Context): BluetoothAdapter? {
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    return bluetoothManager?.adapter
}


fun getDeviceNameIfAudio(intent: Intent, context: Context): String? {

    val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
    } else {
        @Suppress("DEPRECATION")
        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
    }

    if (device == null) return null
    return getBluetoothDeviceNameIfAudio(device, context)

}

fun getBluetoothDeviceNameIfAudio(device: BluetoothDevice, context: Context): String? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null //  Permission not granted, return null
        }
    }
    val deviceClassMaj = device.bluetoothClass?.majorDeviceClass

    // Filter only audio devices
    return if (deviceClassMaj == Major.AUDIO_VIDEO) {
        device.name ?: ""
    } else {
        null // Return null if is not audio
    }
}

fun getBluetoothState(intent: Intent): Int? {
    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
    Log.d("BluetoothStatusReceiver", "Bluetooth state changed: $state")
    return state
}

fun isBluetoothEnabled(intent: Intent): Boolean? {
    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
    Log.d("BluetoothStatusReceiver", "Bluetooth state changed: $state")
    return when (state) {
        BluetoothAdapter.STATE_ON -> true  // Bluetooth enabled
        BluetoothAdapter.STATE_OFF -> false // Bluetooth disabled

        else -> null // Bluetooth state unknown
    }
}


fun checkIsConnectedAudioBluetoothDevices(
    context: Context,
    onConnectedDevices: (List<String>) -> Unit
) {


    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return  //  Permission not granted, return null
        }
    }

    val bluetoothAdapter = getBluetoothAdapter(context) ?: return

    val isBluetoothEnabled = bluetoothAdapter.isEnabled
    if (isBluetoothEnabled) {


        // Get a list of connected devices
        bluetoothAdapter.getProfileProxy(
            context,
            object : BluetoothProfile.ServiceListener {
                override fun onServiceDisconnected(profile: Int) {}
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {

                    val connectedDevices = proxy.connectedDevices

                    val audioDevicesNames = mutableListOf<String>()
                    if (connectedDevices.isNotEmpty()) {
                        for (device in connectedDevices) {
                            val deviceName = getBluetoothDeviceNameIfAudio(device, context)
                            if (deviceName != null) {
                                audioDevicesNames.add(deviceName)
                            }

                        }
                    }
                    Log.d("BluetoothStateManager", "Connected devices: ${audioDevicesNames}")
                    onConnectedDevices(audioDevicesNames)





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


fun getActiveBluetoothAudioDevice(context: Context, callback: (String?) -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e("BtStatusWidget", "--Missing BLUETOOTH_CONNECT permission")
            callback(null)
            return
        }
    }

    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
    val adapter = bluetoothManager?.adapter

    if (adapter == null || !adapter.isEnabled) {
        Log.d("BtStatusWidget", "--Bluetooth is disabled")
        callback(null)
        return
    }

    adapter.getProfileProxy(context, object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            Log.d("BluetoothStatusReceiver", "--BluetoothProfile is $profile")

            if (profile == BluetoothProfile.A2DP) {
                val a2dp = proxy as BluetoothA2dp

                // Додаємо ще одну перевірку дозволу перед викликом `getConnectedDevices()`
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("BtStatusWidget", "--No permission to access Bluetooth devices")
                    callback(null)
                    return
                }

                val connectedDevices = a2dp.connectedDevices // Отримуємо підключені A2DP пристрої
                val activeDevice = connectedDevices.firstOrNull() // Беремо перший активний пристрій, якщо є

                callback(activeDevice?.name ?: "")
            }
            adapter.closeProfileProxy(profile, proxy)
        }

        override fun onServiceDisconnected(profile: Int) {
         //   callback(null)
        }
    }, BluetoothProfile.A2DP)

}


