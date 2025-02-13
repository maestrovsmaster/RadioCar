package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun AudioSpectrumBarGraph(fftData: List<Float>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val barWidth = size.width / fftData.size
        val maxHeight = size.height

        fftData.forEachIndexed { index, amplitude ->
            val height = (amplitude / fftData.maxOrNull()!! * maxHeight).coerceIn(0f, maxHeight)
            drawRect(
                color = Color.Green,
                topLeft = Offset(index * barWidth, maxHeight - height),
                size = Size(barWidth - 2, height) // -2 для проміжків між барами
            )
        }
    }
}

