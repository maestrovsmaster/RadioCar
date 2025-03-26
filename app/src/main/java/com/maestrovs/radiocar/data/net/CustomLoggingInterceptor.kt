package com.maestrovs.radiocar.data.net

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by maestromaster$ on 20/02/2025$.
 */
class CustomLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val requestWithUserAgent = originalRequest.newBuilder()
            .header("User-Agent", "RadioCar/2.0 (radiocar.tunes@gmail.com)")
            .build()

        val startTime = System.nanoTime()
        Log.d("CustomInterceptor", "Sending request to ${requestWithUserAgent.url} \nHeaders: ${requestWithUserAgent.headers}")

        val response = chain.proceed(requestWithUserAgent)
        val endTime = System.nanoTime()
        val duration = (endTime - startTime) / 1e6

        val responseBody = response.peekBody(Long.MAX_VALUE)
        Log.d(
            "CustomInterceptor",
            "Received response from ${response.request.url} in ${duration}ms \n" +
                    "Status Code: ${response.code} \nHeaders: ${response.headers} \nBody: ${responseBody.string()}"
        )

        return response
    }
}