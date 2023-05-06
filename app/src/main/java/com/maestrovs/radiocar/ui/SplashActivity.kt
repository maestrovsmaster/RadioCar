package com.maestrovs.radiocar.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.maestrovs.radiocar.MainActivity
import com.maestrovs.radiocar.R


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Handler().postDelayed({
            startActivity(MainActivity.callingIntent(this))
            overridePendingTransition(R.anim.splash_fade_in, R.anim.splash_fade_out);
            finish()
        }, 20)




    }




}