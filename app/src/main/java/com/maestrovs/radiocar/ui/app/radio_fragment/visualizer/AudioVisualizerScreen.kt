package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.manager.audio_visual.AudioVisualizerStateManager
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.visualizers.AudioSpectrumBarGraphMicro
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.visualizers.AudioSpectrumBarGraphMini
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.visualizers.AudioSpectrumBarGraphTiny
import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.visualizers.AudioSpectrumIonic
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlue

/**
 * Created by maestromaster$ on 12/02/2025$.
 */

@Composable
fun AudioVisualizerScreen(modifier: Modifier = Modifier) {

    val spectrumData by AudioVisualizerStateManager.spectrumData.collectAsState()

    AudioSpectrumBarGraphMini(fftData = spectrumData, modifier = modifier,)
}

@Composable
fun AudioVisualizerScreenTiny(modifier: Modifier = Modifier) {

    val spectrumData by AudioVisualizerStateManager.spectrumData.collectAsState()

    AudioSpectrumBarGraphTiny(fftData = spectrumData, modifier = modifier)
}

@Composable
fun AudioVisualizerScreenMicro(modifier: Modifier = Modifier, color: Color = baseBlue) {

    val spectrumData by AudioVisualizerStateManager.spectrumData.collectAsState()

    AudioSpectrumBarGraphMicro(fftData = spectrumData, modifier = modifier, color = color)
}

@Composable
fun AudioVisualizerScreenIonic(modifier: Modifier = Modifier) {
    val spectrumData by AudioVisualizerStateManager.spectrumData.collectAsState()
    AudioSpectrumIonic(fftData = spectrumData, modifier = modifier)
}




