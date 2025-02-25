package com.maestrovs.radiocar.service.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.maestrovs.radiocar.manager.bluetooth.BluetoothStateManager
import com.maestrovs.radiocar.manager.bluetooth.StateSender

/**
 * Created by maestromaster on 12/02/2025.
 */

class BluetoothStatusReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        when (action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {

                Log.d("BluetoothStatusReceiver", "Bluetooth state changedintent : $intent")
                val isEnabled = isBluetoothEnabled(intent)
                Log.d("BluetoothStatusReceiver", "Bluetooth state changed: $isEnabled")
                BluetoothStateManager.setBluetoothState(context!!, if(isEnabled == true) BluetoothAdapter.STATE_ON else BluetoothAdapter.STATE_OFF, StateSender.Receiver)//setBluetoothEnabled(context!!,isEnabled == true)

                if (isEnabled == true) {
                    context?.let {

                        // Register the broadcast receiver again
                        val filter = IntentFilter().apply {
                            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                        }
                        it.registerReceiver(this, filter)

                       checkBluetoothDevices(it)
                    }
                }
            }

            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                getDeviceNameIfAudio(intent, context!!)?.let { deviceName ->
                    BluetoothStateManager.setBluetoothState(context, BluetoothAdapter.STATE_ON, StateSender.Receiver)
                    BluetoothStateManager.setCurrentBluetoothDevice(context, deviceName, StateSender.Receiver)
                }
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                getDeviceNameIfAudio(intent, context!!)?.let { deviceName ->
                    BluetoothStateManager.setBluetoothState(context, BluetoothAdapter.STATE_OFF, StateSender.Receiver)
                    BluetoothStateManager.setCurrentBluetoothDevice(context, "", StateSender.Receiver)
                }
            }
        }
    }


    private fun checkBluetoothDevices(context: Context ){
        /*checkIsConnectedAudioBluetoothDevices(context){
             BluetoothStateManager.setBluetoothDevices(context,it)
        }*/
        getActiveBluetoothAudioDevice(context){
            BluetoothStateManager.setCurrentBluetoothDevice(context,it, StateSender.Receiver)
        }
    }





}