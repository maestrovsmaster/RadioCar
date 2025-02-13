package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Created by maestromaster$ on 12/02/2025$.
 */

@Composable
fun AudioVisualizerScreen(audioSessionId: Int) {
    val visualizer = remember { AudioVisualizer(audioSessionId, 32) }
    val spectrumData by visualizer.spectrumData


    AudioSpectrumBarGraphMini(fftData = spectrumData, modifier = Modifier.width(200.dp).height(70.dp))
}
