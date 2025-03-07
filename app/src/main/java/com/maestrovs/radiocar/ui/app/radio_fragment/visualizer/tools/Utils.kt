package com.maestrovs.radiocar.ui.app.radio_fragment.visualizer.tools

/**
 * Created by maestromaster$ on 06/03/2025$.
 */

fun compressFfa(fftData: List<Float>, parts: Int): List<Float>{
    val compressedData = mutableListOf<Float>()

    for(i in 0 until parts){
        val start = i * (fftData.size / parts)
        val end = (i + 1) * (fftData.size / parts)

        val subList = fftData.subList(start, end)
        val mediumValueOfSublist = subList.sum() / subList.size
        compressedData.add(mediumValueOfSublist)
    }


    return compressedData
}