package com.maestrovs.radiocar.service.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
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
       // Log.d("BluetoothStatusReceiver", "BluetoothStatusReceiver onReceive")

        val action = intent?.action ?: return
        when (action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {



                val isEnabled = isBluetoothEnabled(intent)

                //Log.d("BluetoothStatusReceiver", "ACTION_STATE_CHANGED isEnabled: $isEnabled")

                BluetoothStateManager.setBluetoothState(context!!, if(isEnabled == true) BluetoothAdapter.STATE_ON else BluetoothAdapter.STATE_OFF, StateSender.Receiver)

                if (isEnabled == true) {
                    context?.let {

                        // Register the broadcast receiver again
                        val filter = IntentFilter().apply {
                            addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                            addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                        }
                      //  it.registerReceiver(this, filter) //should be deleted

                       checkBluetoothDevices(it)
                    }
                }
            }

            BluetoothDevice.ACTION_ACL_CONNECTED -> {
               // Log.d("BluetoothStatusReceiver", "ACTION_ACL_CONNECTED")



                getDeviceNameIfAudio(intent, context!!)?.let { deviceName ->
                    BluetoothStateManager.setBluetoothState(context, BluetoothAdapter.STATE_ON, StateSender.Receiver)
                    BluetoothStateManager.setCurrentBluetoothDevice(context, deviceName, StateSender.Receiver)
                }
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                //Log.d("BluetoothStatusReceiver", "ACTION_ACL_DISCONNECTED")
                getDeviceNameIfAudio(intent, context!!)?.let { deviceName ->
                    BluetoothStateManager.setBluetoothState(context, BluetoothAdapter.STATE_OFF, StateSender.Receiver)
                    BluetoothStateManager.setCurrentBluetoothDevice(context, "", StateSender.Receiver)
                }
            }
        }
    }


    private fun checkBluetoothDevices(context: Context ){

        getActiveBluetoothAudioDevice(context){
            Log.d("BluetoothStatusReceiver", "getActiveBluetoothAudioDevice: $it")
            BluetoothStateManager.setCurrentBluetoothDevice(context,it, StateSender.Receiver)
        }
    }








}