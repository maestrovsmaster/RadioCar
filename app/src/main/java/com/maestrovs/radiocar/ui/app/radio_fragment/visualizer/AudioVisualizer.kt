package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer

import android.media.audiofx.Visualizer
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import java.lang.Math.log1p
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Created by maestromaster$ on 12/02/2025$.
 */

class AudioVisualizer(private val audioSessionId: Int, private val  step: Int = 2) {
    private var visualizer: Visualizer? = null

    init {
        try {
            visualizer = Visualizer(audioSessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1] // Максимальна доступна роздільна здатність
                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(
                        visualizer: Visualizer?, waveform: ByteArray?, samplingRate: Int
                    ) {}

                    override fun onFftDataCapture(
                        visualizer: Visualizer?, fft: ByteArray?, samplingRate: Int
                    ) {
                        fft?.let { processFFT(it) }
                    }
                }, Visualizer.getMaxCaptureRate() / 3, false, true)
                enabled = true
            }
            visualizer?.enabled = true
        } catch (e: RuntimeException) {
            Log.e("Visualizer", "Не вдалося ініціалізувати Visualizer: ${e.message} audioSessionId = ${audioSessionId}")
        }

    }

    private val _spectrumData = mutableStateOf<List<Float>>(emptyList())
    val spectrumData: State<List<Float>> = _spectrumData



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
        _spectrumData.value = magnitudes
    }

}
