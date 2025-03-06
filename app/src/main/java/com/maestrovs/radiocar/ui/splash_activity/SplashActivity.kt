package com.maestrovs.radiocar.ui.splash_activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import com.maestrovs.radiocar.ui.main.MainActivity
import com.maestrovs.radiocar.R


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {


    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("BluetoothStatusReceiver", "SplashActivity onCreate")

        Handler().postDelayed({
            startActivity(MainActivity.callingIntent(this))
            overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
            finish()
        }, 20)




    }




}