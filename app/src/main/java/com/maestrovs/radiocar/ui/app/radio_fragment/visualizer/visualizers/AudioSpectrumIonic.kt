package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.visualizers
import android.util.Log
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
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.tools.compressFfa
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlue

@Composable
fun AudioSpectrumIonic(
    fftData: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = baseBlue
) {

    val fftDataCompressed = if (fftData.size == 0) emptyList() else compressFfa(fftData, 4)
    if(fftDataCompressed.size <4 ) return

    Log.d("AudioSpectrumIonic", "fftDataCompressed: $fftDataCompressed")

   // val transition = updateTransition(targetState = fftDataCompressed, label = "SpectrumAnimation")

   /* val animatedValues = if (fftDataCompressed.isNotEmpty()) {
        fftDataCompressed.mapIndexed { index, _ ->
            transition.animateFloat(
                transitionSpec = { tween(durationMillis = 180) },
                label = "bar_$index"
            ) { it.getOrNull(index) ?: 0f }
        }
    } else {
        emptyList()
    }*/

    //val maxFft = remember(fftDataCompressed) { fftDataCompressed.maxOrNull() ?: 1f }

    val max = 1f //fftDataCompressed.sum()/fftDataCompressed.size

    Canvas(modifier = modifier.fillMaxSize()) {
        val barWidth = size.width
        val barHeight = size.height


        val baseKoef = 0.1f
        val blueKoef = 0.8f
        val redKoef = 0.4f


        val colorLow = Color(
            red = 0.4f,
            green = 0.7f,
            blue = (fftDataCompressed[0]+blueKoef),
            alpha = max.toFloat()
        )
        val colorMidLow = Color(
            red = 0.9f,
            green = 1f/(fftDataCompressed[1]+baseKoef),
            blue = 1f/(fftDataCompressed[0]+baseKoef),
            alpha = max.toFloat()
        )
        val colorMidHigh = Color(
            red = redKoef*1f/(fftDataCompressed[2]+baseKoef),
            green = 1f/(fftDataCompressed[2]+baseKoef),
            blue = blueKoef,
            alpha = max.toFloat()
        )
        val colorHigh = Color(
            red = redKoef*1f/(fftDataCompressed[3]+baseKoef),
            green = 0.7f,
            blue = blueKoef,
            alpha = max.toFloat()
        )


        // Створюємо лінійний градієнт на 4 точки (кутові)
        val shader = LinearGradientShader(
            colors = listOf(
                colorLow, colorMidLow, colorMidHigh, colorHigh
            ),
            from = Offset(0f, 0f),
            to = Offset(barWidth, 0f),
            //  colorStops = listOf(0f, 0.5f, 0.5f, 1f) // Плавний перехід
        )

        drawRect(
            brush = ShaderBrush(shader),
            topLeft = Offset.Zero,
            size = Size(barWidth, barHeight)
        )




       // val colorMax = Color(0xE11C1B1B)
        val colorMin = Color(0x0C252528)


        val colorMax = Color(
            red = 0f,
            green = 0f,
            blue = 0f,
            alpha = max.toFloat()
        )


        // Створюємо лінійний градієнт на 4 точки (кутові)
        val shader2 = LinearGradientShader(
            colors = listOf(
                colorMax, colorMin
            ),
            from = Offset(0f, 0f),
            to = Offset(0f, barHeight),
            //  colorStops = listOf(0f, 0.5f, 0.5f, 1f) // Плавний перехід
        )

       /* drawRect(
            brush = ShaderBrush(shader2),
            topLeft = Offset.Zero,
            size = Size(barWidth, barHeight)
        )*/
    }

}


@Preview
@Composable
fun AudioSpectrumIonicPreview(){
    //Write random changing values loop
    AudioSpectrumIonic(fftData = listOf(0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f))

}


