package com.maestrovs.radiocar.data.net

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by maestromaster$ on 20/02/2025$.
 */
class CustomLoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.nanoTime()

        Log.d("CustomInterceptor", "Sending request to ${request.url} \nHeaders: ${request.headers}")

        val response = chain.proceed(request)
        val endTime = System.nanoTime()
        val duration = (endTime - startTime) / 1e6

        val responseBody = response.peekBody(Long.MAX_VALUE)
        Log.d("CustomInterceptor", "Received response from ${response.request.url} in ${duration}ms \n" +
                "Status Code: ${response.code} \nHeaders: ${response.headers} \nBody: ${responseBody.string()}")

        return response
    }
}