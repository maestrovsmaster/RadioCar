package com.maestrovs.radiocar.utils

/**
 * Created by maestromaster on 12/02/2025.
 */

import android.net.Uri
import android.util.Log
import androidx.media3.common.C
import androidx.media3.common.util.Util
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.TimeUnit

import okhttp3.*



fun isPlayableStream(url: String): Boolean {
    val type = Util.inferContentType(Uri.parse(url))

    // Якщо це "OTHER", воно працює як стрім
    if (type == C.CONTENT_TYPE_OTHER) {
        Log.d("StreamCheck", "Це нормальний стрім")
        return true
    }

    // Якщо це HLS (2), перевіряємо додатково
    if (type == C.CONTENT_TYPE_HLS) {
        Log.d("StreamCheck", "Це HLS, перевіряємо заголовки...")

        val client = OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder()
            .url(url)
            .head()
            .build()

        return try {
            val response = client.newCall(request).execute()
            val contentType = response.header("Content-Type", "")
            response.close()

            if(contentType == null){
                Log.e("StreamCheck", "Немає заголовка Content-Type")
                return false
            }

            // Якщо це правильний HLS, то тип буде `application/vnd.apple.mpegurl`
            val isValidHls = contentType?.contains("mpegurl") == true || contentType.contains("hls")
            if (!isValidHls) {
                Log.e("StreamCheck", "Цей HLS неправдивий, ігноруємо: $contentType")
            }
            isValidHls
        } catch (e: Exception) {
            Log.e("StreamCheck", "Помилка перевірки HLS", e)
            false
        }
    }

    return false
}


