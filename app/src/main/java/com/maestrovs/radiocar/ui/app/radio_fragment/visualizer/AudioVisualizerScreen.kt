package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.maestrovs.radiocar.manager.audio_visual.AudioVisualizerStateManager
import com.maestrovs.radiocar.ui.app.ui.theme.baseBlue

/**
 * Created by maestromaster$ on 12/02/2025$.
 */

@Composable
fun AudioVisualizerScreen() {

    val spectrumData by AudioVisualizerStateManager.spectrumData.collectAsState()

    AudioSpectrumBarGraphMini(fftData = spectrumData, modifier = Modifier.width(200.dp).height(70.dp))
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




