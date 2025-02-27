package com.maestrovs.radiocar.manager.audio_visual

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Created by maestromaster$ on 17/02/2025$.
 */
object AudioVisualizerStateManager {
    private val _spectrumData = MutableStateFlow<List<Float>>(emptyList())
    val spectrumData: StateFlow<List<Float>> = _spectrumData.asStateFlow()

    fun updateSpectrumData(newData: List<Float>) {
        _spectrumData.value = newData
    }


}


