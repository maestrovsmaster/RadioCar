package com.maestrovs.radiocar.manager.bluetooth

/**
 * Created by maestromaster$ on 15/02/2025$.
 */

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.os.Build
import android.util.Log
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.service.bluetooth.getBluetoothAdapter
import com.maestrovs.radiocar.shared_managers.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


enum class StateSender{
   Initial, Activity, Receiver
}

object BluetoothStateManager {

    private val _bluetoothState = MutableStateFlow<Int?>(null)
    val bluetoothState = _bluetoothState.asStateFlow()


    fun setBluetoothState(context: Context, state: Int, stateSender: StateSender) {
        _bluetoothState.value = state

        Log.d("BluetoothStateManager","setBluetoothState stateSender = $stateSender")

        if(stateSender == StateSender.Receiver  ) {
            manageBluetoothState(context)
        }
    }


    private val _currentBluetoothDevice = MutableStateFlow<String?>(null)
    val currentBluetoothDevice = _currentBluetoothDevice.asStateFlow()

    fun setCurrentBluetoothDevice(context: Context,device: String?, stateSender: StateSender) {
        _currentBluetoothDevice.value = device

        if(stateSender == StateSender.Receiver || stateSender == StateSender.Initial) {
            //Broadcast receiver might has effect on playing, but no Activity, because activities responsibility is only to show UI
            manageBluetoothState(context)
        }
    }


    private fun manageBluetoothState(context: Context){

        Log.d("BluetoothStateManager","manageBluetoothState")

        val isBluetoothEnabled = getBluetoothAdapter(context)
        _bluetoothState.value = isBluetoothEnabled?.state//isEnabled == true

        val shouldAutoplayWhenBluetoothConnectedOrDisconnected = SettingsManager.isAutoplay(context)
        if(!shouldAutoplayWhenBluetoothConnectedOrDisconnected) return
        var shouldPlay = false
        if(_bluetoothState.value == BluetoothAdapter.STATE_ON){
            val someDeviceWasEnabled = !_currentBluetoothDevice.value.isNullOrEmpty()
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){

                if(someDeviceWasEnabled){
                    shouldPlay = true
                }
            }else{
                //Because for elder android versions we can't check current active device, so we can check only if some device was enabled or disabled

                if(someDeviceWasEnabled){
                    shouldPlay = true
                }

            }
        }else{
            shouldPlay = false
        }
        Log.d("BluetoothStateManager","shouldPlay = $shouldPlay")
        if(shouldPlay){
            PlayerStateManager.play()
        }else{
            PlayerStateManager.pause()
        }

    }


}
