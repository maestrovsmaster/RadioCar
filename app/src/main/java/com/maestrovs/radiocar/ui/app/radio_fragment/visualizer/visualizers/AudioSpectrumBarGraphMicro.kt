package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.visualizers
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.tools.compressFfa
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlue

@Composable
fun AudioSpectrumBarGraphMicro(
    fftData: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = baseBlue
) {

    val fftDataCompressed = if(fftData.size == 0 ) emptyList() else compressFfa(fftData, 4)

    val transition = updateTransition(targetState = fftDataCompressed, label = "SpectrumAnimation")

    val animatedValues = if (fftDataCompressed.isNotEmpty()) {
        fftDataCompressed.mapIndexed { index, _ ->
            transition.animateFloat(
                transitionSpec = { tween(durationMillis = 180) },
                label = "bar_$index"
            ) { it.getOrNull(index) ?: 0f }
        }
    } else {
        emptyList()
    }

    val maxFft = remember(fftDataCompressed) { fftDataCompressed.maxOrNull() ?: 1f }

    Canvas(modifier = modifier.fillMaxSize()) {
        val barWidth = size.width / fftDataCompressed.size
        val barHeight = size.height
        val segmentHeight = barHeight / 5


        animatedValues.forEachIndexed { index, animatable ->
            val height = (animatable.value / maxFft * barHeight).coerceIn(0f, barHeight)

            val segmentCount = (height / segmentHeight).toInt()


            repeat(segmentCount) { segmentIndex ->
                val yOffset = barHeight - (segmentIndex + 1) * segmentHeight

                drawRect(
                    color = color,
                    topLeft = Offset(index * barWidth, yOffset),
                    size = Size(barWidth - 2, segmentHeight - 2)
                )
            }
        }
    }
}


