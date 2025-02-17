package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer

/**
 * Created by maestromaster$ on 17/02/2025$.
 */

object AudioVisualizerManager {
    private var audioVisualizer: AudioVisualizer? = null

    fun getVisualizer(audioSessionId: Int, step: Int = 2): AudioVisualizer {
        // Ініціалізація лише один раз для кожної сесії
        if (audioVisualizer == null) {
            audioVisualizer = AudioVisualizer(audioSessionId, step)
        }
        return audioVisualizer!!
    }

    fun releaseVisualizer() {
        audioVisualizer = null
    }
}
