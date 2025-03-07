package com.maestrovs.radiocar.service.bluetooth

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.maestrovs.radiocar.ui.main.MainActivity

/**
 * Created by maestromaster$ on 06/03/2025$.
 */

class BluetoothAutoLaunchReceiver : BroadcastReceiver() {

    // Тут вкажи MAC-адресу твого Bluetooth-пристрою (QITERSTAR)
    private val targetMacAddress = "00:11:22:33:44:55" // Замінити на реальну MAC-адресу

    @OptIn(UnstableApi::class)
    override fun onReceive(context: Context?, intent: Intent?) {
         Log.d("BluetoothAutoLaunch", "onReceive")
        if (intent?.action == BluetoothDevice.ACTION_ACL_CONNECTED) {
            val device: BluetoothDevice? = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)

            if (device != null) {
                Log.d("BluetoothAutoLaunch", "Підключено:  (${device.address})")

                // Перевіряємо, чи MAC-адреса збігається з потрібною
               // if (device.address.equals(targetMacAddress, ignoreCase = true)) {
                    Log.d("BluetoothAutoLaunch", "Це потрібний пристрій! Запускаємо додаток...")

                    val launchIntent = Intent(context, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context?.startActivity(launchIntent)
               // }
            }
        }
    }
}
