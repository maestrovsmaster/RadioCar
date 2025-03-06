package com.maestrovs.radiocar

import android.app.Application
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import androidx.work.Configuration
import com.maestrovs.radiocar.service.bluetooth.BluetoothReceiverManager


@HiltAndroidApp
class MainApplication : Application() {


    override fun onCreate() {
        super.onCreate()

        BluetoothReceiverManager.registerReceiver(this)

      /*  val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .build()

        WorkManager.initialize(this, config)*/
    }

}