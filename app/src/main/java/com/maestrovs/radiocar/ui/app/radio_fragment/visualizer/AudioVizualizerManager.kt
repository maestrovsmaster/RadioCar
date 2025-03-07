package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer

import android.media.audiofx.Visualizer
import android.util.Log
import com.maestrovs.radiocar.manager.audio_visual.AudioVisualizerStateManager
import java.lang.Math.log1p
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by maestromaster$ on 17/02/2025$.
 */
object AudioVisualizerManager {
    private var visualizer: Visualizer? = null
    private var currentSessionId: Int = -1

    val step = 20

    fun initVisualizer(audioSessionId: Int) {
        if (audioSessionId == currentSessionId) return

        release()

        try {
            visualizer = Visualizer(audioSessionId).apply {
                val captureSizes = Visualizer.getCaptureSizeRange()
                val maxSize = captureSizes?.getOrNull(1) ?: 256
                captureSize = minOf(maxSize, 1024)

                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int
                    ) {}

                    override fun onFftDataCapture(
                        visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int
                    ) {
                        if (fft == null || fft.isEmpty()) return
                        processFFT(fft)
                    }
                }, Visualizer.getMaxCaptureRate() / 4, false, true)

                enabled = true
            }

            currentSessionId = audioSessionId

        } catch (e: RuntimeException) {
            Log.e("Visualizer", "Не вдалося ініціалізувати Visualizer: ${e.message} audioSessionId = $audioSessionId")
        }
    }

    fun release() {
        visualizer?.release()
        visualizer = null
        currentSessionId = -1
    }

    private fun processFFT(fft: ByteArray, smoothFactor: Float = 0.5f) {
        val magnitudes = mutableListOf<Float>()
        val fftSize = fft.size / 2

        val cutDiapazonKoef = 0.2
        val startDiapazon = (0 + fftSize * cutDiapazonKoef).toInt()
        val endDiapazon = (fftSize - fftSize * cutDiapazonKoef).toInt()

        for (i in startDiapazon until endDiapazon step step) {
            val real = fft[i].toFloat()
            val imaginary = fft[i + 1].toFloat()
            var magnitude = sqrt(real * real + imaginary * imaginary)

            // Нормалізація частоти (0.0 = низькі частоти, 1.0 = високі)
            val normalizedFreq = i.toFloat() / fftSize

            // Корекція: підсилюємо ВЧ, зменшуємо НЧ
            val correctionFactor = (1f - normalizedFreq).pow(smoothFactor)  // Згладжування
            magnitude *= correctionFactor

            // Логарифмічне згладжування (запобігає занадто сильним басам)
            magnitude = log1p(magnitude.toDouble()).toFloat()

            magnitudes.add(magnitude)
        }
        //_spectrumData.value = magnitudes

        AudioVisualizerStateManager.updateSpectrumData(magnitudes)
    }
}

