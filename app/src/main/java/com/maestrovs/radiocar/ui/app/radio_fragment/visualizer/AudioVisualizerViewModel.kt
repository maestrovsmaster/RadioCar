package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * Created by maestromaster$ on 17/02/2025$.
 */

class AudioVisualizerViewModel : ViewModel() {
    private val _spectrumData = MutableStateFlow<List<Float>>(emptyList())
    val spectrumData: StateFlow<List<Float>> = _spectrumData

    fun updateSpectrumData(data: List<Float>) {
        _spectrumData.value = data
    }
}
