package com.maestrovs.radiocar.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.maestrovs.radiocar.enums.bluetooth.BT_Status
import com.maestrovs.radiocar.ui.settings.SettingsManager

/**
 * Created by maestromaster on 12/02/2025.
 */

class BluetoothStatusReceiver : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("BluetoothDevice", "onReceive intent?.action = ${intent?.action}")

        val action = intent?.action ?: return
        when (action) {
            BluetoothAdapter.ACTION_STATE_CHANGED -> {
                when (intent.getIntExtra(
                    BluetoothAdapter.EXTRA_STATE,
                    BluetoothAdapter.ERROR
                )) {
                   //// BluetoothAdapter.STATE_ON -> mainViewModel.setBluetoothStatus(BT_Status.Enabled)
                   //// BluetoothAdapter.STATE_OFF -> mainViewModel.setBluetoothStatus(BT_Status.Disable)
                }
            }

            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
              ///  mainViewModel.setBluetoothStatus(BT_Status.ConnectedDevice)
              //  if (SettingsManager.isAutoplay(this@MainActivity)) {
                    //   mainViewModel.playCurrentStationState()
               // }
            }

            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                val device: BluetoothDevice? =
                    intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
               /// mainViewModel.setBluetoothStatus(BT_Status.DisconnectedDevice)
               /// mainViewModel.stopCurrentStationState()
            }
        }
    }
}