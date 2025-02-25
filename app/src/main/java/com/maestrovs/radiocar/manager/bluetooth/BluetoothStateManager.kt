package com.maestrovs.radiocar.manager.bluetooth

/**
 * Created by maestromaster$ on 15/02/2025$.
 */

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.location.Location
import android.os.Build
import android.util.Log
import com.maestrovs.radiocar.manager.radio.PlayerStateManager
import com.maestrovs.radiocar.service.bluetooth.getActiveBluetoothAudioDevice
import com.maestrovs.radiocar.service.bluetooth.getBluetoothAdapter
import com.maestrovs.radiocar.shared_managers.SettingsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlin.math.roundToInt


enum class StateSender{
    Activity, Receiver
}

object BluetoothStateManager {

    private val _bluetoothState = MutableStateFlow<Int?>(null)
    val bluetoothState = _bluetoothState.asStateFlow()

   /* fun getBluetoothEnabledState(context: Context): StateFlow<Boolean> {
        val isBluetoothEnabled = getBluetoothAdapter(context)
        _isBluetoothEnabled.value = isBluetoothEnabled?.isEnabled == true
        return _isBluetoothEnabled.asStateFlow()
    }*/

    fun setBluetoothState(context: Context, state: Int, stateSender: StateSender) {
        _bluetoothState.value = state

        if(stateSender == StateSender.Receiver) {
            manageBluetoothState(context)
        }
    }

   /* fun getCurrentBluetoothConnected(context: Context){
        getActiveBluetoothAudioDevice(context){
            Log.d("BtStatusWidget", "getCurrentBluetoothConnected: $it")
            setCurrentBluetoothDevice(context, it)
        }

    }*/

    /*private val _bluetoothDevices = MutableStateFlow<List<String>>(emptyList())
    //val bluetoothDevices = _bluetoothDevices.asStateFlow()

    fun setBluetoothDevices(context: Context,devices: List<String>) {
        _bluetoothDevices.value = devices
        manageBluetoothState(context)
    }*/

    private val _currentBluetoothDevice = MutableStateFlow<String?>(null)
    val currentBluetoothDevice = _currentBluetoothDevice.asStateFlow()

    fun setCurrentBluetoothDevice(context: Context,device: String?, stateSender: StateSender) {
        Log.d("BtStatusWidget", "setCurrentBluetoothDevice: $device")
        _currentBluetoothDevice.value = device

        if(stateSender == StateSender.Receiver) {
            //Broadcast receiver might has effect on playing, but no Activity, because activities responsibility is only to show UI
            manageBluetoothState(context)
        }
    }

   /* private val _switchedBluetoothDevice = MutableStateFlow<Pair<String, Boolean>?>(null)
    val switchedBluetoothDevice = _switchedBluetoothDevice.asStateFlow()

    fun setSwitchedBluetoothDevice(context: Context, device: String, switched: Boolean) {
        _switchedBluetoothDevice.value = Pair(device, switched)
        _currentBluetoothDevice.value = device
        manageBluetoothState(context)
    }*/



    private fun manageBluetoothState(context: Context){

        val isBluetoothEnabled = getBluetoothAdapter(context)
        _bluetoothState.value = isBluetoothEnabled?.state//isEnabled == true

        val shouldAutoplayWhenBluetoothConnectedOrDisconnected = SettingsManager.isAutoplay(context)
        if(!shouldAutoplayWhenBluetoothConnectedOrDisconnected) return
        var shouldPlay = false
        if(_bluetoothState.value == BluetoothAdapter.STATE_ON){
          //  val someDeviceWasDisabled = _switchedBluetoothDevice.value?.second == false
            val someDeviceWasEnabled = !_currentBluetoothDevice.value.isNullOrEmpty() //_switchedBluetoothDevice.value?.second == true
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
        if(shouldPlay){
            PlayerStateManager.play()
        }else{
            PlayerStateManager.pause()
        }

    }


}
