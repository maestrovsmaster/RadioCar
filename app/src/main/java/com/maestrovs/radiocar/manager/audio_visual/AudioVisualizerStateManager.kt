package com.maestrovs.radiocar.manager.audio_visual

import com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.AudioVisualizer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Created by maestromaster$ on 17/02/2025$.
 */
object AudioVisualizerStateManager {
    private val _spectrumData = MutableStateFlow<List<Float>>(emptyList())
    val spectrumData: StateFlow<List<Float>> = _spectrumData.asStateFlow()

    fun updateSpectrumData(newData: List<Float>) {
        _spectrumData.value = newData
    }

   // private var visualizer: AudioVisualizer? = null
  //  private var job: Job? = null

    /*fun initVisualizer(audioSessionId: Int, step: Int = 2, scope: CoroutineScope) {
      //  if (visualizer?.audioSessionId == audioSessionId) return // Якщо той самий ID – не ініціалізуємо вдруге

       // visualizer = AudioVisualizer(audioSessionId, step)

        // Скасовуємо попередню колекцію
        job?.cancel()

        job = scope.launch {
          /*  visualizer!!.spectrumData
                .collectLatest { data -> // collectLatest скасовує попередні колекції
                    _spectrumData.value = data
                }*/
        }
    }*/
}


