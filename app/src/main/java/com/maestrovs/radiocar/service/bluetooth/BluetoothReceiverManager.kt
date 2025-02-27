package com.maestrovs.radiocar.service.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.IntentFilter

/**
 * Created by maestromaster$ on 24/02/2025$.
 */

object BluetoothReceiverManager {

    private  var bluetoothStatusReceiver: BluetoothStatusReceiver? = null
    private var isReceiverRegistered = false



    fun registerReceiver(context: Context) {
        if (bluetoothStatusReceiver == null) {
            bluetoothStatusReceiver = BluetoothStatusReceiver()
        }
        if (!isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
            context.registerReceiver(bluetoothStatusReceiver, filter)
            isReceiverRegistered = true
        }
    }

    fun unregisterReceiver(context: Context) {
        if (isReceiverRegistered) {
            bluetoothStatusReceiver?.let {
                context.unregisterReceiver(it)
                bluetoothStatusReceiver = null
            }
            isReceiverRegistered = false
        }
    }


}