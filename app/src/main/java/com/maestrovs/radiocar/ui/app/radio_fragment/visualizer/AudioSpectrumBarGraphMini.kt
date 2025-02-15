package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlue

@Composable
fun AudioSpectrumBarGraphMini(
    fftData: List<Float>,
    modifier: Modifier = Modifier
) {

    val transition = updateTransition(targetState = fftData, label = "SpectrumAnimation")

    val animatedValues = if (fftData.isNotEmpty()) {
        fftData.mapIndexed { index, _ ->
            transition.animateFloat(
                transitionSpec = { tween(durationMillis = 180) },
                label = "bar_$index"
            ) { it.getOrNull(index) ?: 0f } // Захист від OutOfBounds
        }
    } else {
        emptyList()
    }


    val maxFft = remember(fftData) { fftData.maxOrNull() ?: 1f }

    Canvas(modifier = modifier.fillMaxSize()) {
        val barWidth = size.width / fftData.size
        val barHeight = size.height * 0.73f
        val shadowHeight = size.height * 0.27f
        val segmentHeight = barHeight / 10  // Дискретні рівні
        val shadowSegmentHeight = shadowHeight / 10

        animatedValues.forEachIndexed { index, animatable ->
            val height = (animatable.value / maxFft * barHeight).coerceIn(0f, barHeight)
            val shadowHeightAdj = (animatable.value / maxFft * shadowHeight).coerceIn(0f, shadowHeight)

            val segmentCount = (height / segmentHeight).toInt()

            // 🎨 Малюємо головний бар, сегментами
            repeat(segmentCount) { segmentIndex ->
                val yOffset = barHeight - (segmentIndex + 1) * segmentHeight

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

            // 🎨 Малюємо тінь нижче головного бару
            val shadowSegmentCount = (shadowHeightAdj / shadowSegmentHeight).toInt()

            repeat(shadowSegmentCount) { segmentIndex ->
                val yOffset = barHeight + (segmentIndex + 1) * shadowSegmentHeight

                val colorRatio = segmentIndex.toFloat() / shadowSegmentCount.toFloat()
                val shadowColor = Color.Gray.copy(alpha = 0.2f - colorRatio * 0.1f) // Плавне затухання

                drawRect(
                    color = shadowColor,
                    topLeft = Offset(index * barWidth, yOffset),
                    size = Size(barWidth - 2, shadowSegmentHeight - 2)
                )
            }
        }
    }
}
