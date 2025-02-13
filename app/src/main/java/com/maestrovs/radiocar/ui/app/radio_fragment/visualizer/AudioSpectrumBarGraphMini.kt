package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun AudioSpectrumBarGraphMini(
    fftData: List<Float>,
    modifier: Modifier = Modifier
) {
    val animatedValues = fftData.map { amplitude ->
        remember { Animatable(0f) }.apply {
           /* LaunchedEffect(amplitude) {
                animateTo(amplitude, animationSpec = tween(durationMillis = 300))
            }*/
            LaunchedEffect(amplitude) {
                if (amplitude == 0f) {
                    animateTo(0f, animationSpec = tween(durationMillis = 180, easing = LinearOutSlowInEasing))
                } else {
                    animateTo(amplitude, animationSpec = tween(durationMillis = 180))
                }
            }

        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val barWidth = size.width / fftData.size
        val maxHeight = size.height
        val segmentHeight = maxHeight / 20  // Discrets

        animatedValues.forEachIndexed { index, animatable ->

            val height = (animatable.value / fftData.maxOrNull()!! * maxHeight).coerceIn(0f, maxHeight)
            val segmentCount = (height / segmentHeight).toInt()
                // Log.d("AudioSpectrumBarGraphMini", "animatable = ${animatable.value}  segmentCount = ${segmentCount}   fftData.maxOrNull()!! = ${fftData.maxOrNull()!!}")
            repeat(segmentCount) { segmentIndex ->
                val yOffset = maxHeight - (segmentIndex + 1) * segmentHeight

                val colorRatio = segmentIndex.toFloat() / segmentCount.toFloat()
                val segmentColor = Color(
                    red = (0f + (1f - colorRatio) * 0.3f),  // Менше червоного (зеленіє)
                    green = (0.5f + (1f - colorRatio) * 0.5f),  // Спад зеленого
                    blue = (1f - (1f - colorRatio) * 0.3f),  // Більше синього
                    alpha = 1f - colorRatio * 0.6f // Прозорість з висотою
                )
                drawRect(
                    color = segmentColor,
                    topLeft = Offset(index * barWidth, yOffset),
                    size = Size(barWidth - 2, segmentHeight - 2) // Додаємо зазор між сегментами
                )
            }
        }
    }
}


