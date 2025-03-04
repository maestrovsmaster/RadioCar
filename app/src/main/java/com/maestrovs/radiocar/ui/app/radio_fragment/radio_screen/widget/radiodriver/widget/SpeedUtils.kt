package com.maestrovs.radiocar.ui.app.radio_fragment.radio_screen.widget.radiodriver.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import com.maestrovs.radiocar.data.remote.weather.convertCelsiumToFahrenheit
import com.maestrovs.radiocar.shared_managers.SettingsManager
import com.maestrovs.radiocar.shared_managers.SpeedUnit
import com.maestrovs.radiocar.shared_managers.TemperatureUnit

/**
 * Created by maestromaster$ on 04/03/2025$.
 */
 fun convertToMph(kmh: Float) = kmh*0.6213711922

fun getTSpeedWithUnit(context: Context, speed: Float?): Pair<Float?, SpeedUnit>{



    val unit = SettingsManager.getSpeedUnit(context)

    if(speed == null) return Pair(null, SpeedUnit.kmh)

    return  when (unit) {
        SpeedUnit.kmh -> Pair(speed, SpeedUnit.kmh)
        SpeedUnit.mph -> Pair(convertToMph(speed).toFloat(), SpeedUnit.mph)

    }
}


fun calculateSpeedColor(context: Context, speed: Float?): Color{
    return if(speed == null) Color(0xFFEAE9E9)
    else if(speed > 0 && speed < 50) Color(0xFFFCFCFC)
    else if (speed >= 50 && speed < 90) Color(0xFFFAEFC2)
    else if(speed >=90 && speed < 120) Color(0xFFF5A340)
    else if(speed >=120) Color(0xFFE53935)
    else Color(0xFFF1F0F0)

}

fun calculateLottieSpeedAnimation(speed: Float?): Float {

    val minSpeed = 8f
    val maxSpeed = 100f
    val minAnimSpeed = 0.2f
    val maxAnimSpeed = 4.0f

    val koef = 0.8f

    if (speed == null || speed <= minSpeed) return 0.0f



    val clampedSpeed = speed.coerceIn(minSpeed, maxSpeed)

    return minAnimSpeed + (clampedSpeed / maxSpeed) * (maxAnimSpeed - minAnimSpeed) * koef
}
