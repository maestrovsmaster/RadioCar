package com.maestrovs.radiocar.data.remote.weather

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.maestrovs.radiocar.data.repository.WeatherRepositoryIml
import com.maestrovs.radiocar.manager.location.LocationStateManager
import com.maestrovs.radiocar.shared_managers.WeatherManager.saveWeatherToCache
import java.util.concurrent.TimeUnit

/**
 * Created by maestromaster$ on 02/03/2025$.
 */

class WeatherWorker(
    val ctx: Context,
    params: WorkerParameters,
    private val weatherRepository: WeatherRepositoryIml
) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val location = LocationStateManager.locationState.value

        if (location != null) {
            val weather = weatherRepository.getWeatherDataByCoordinates(
                location.latitude, location.longitude
            )
            weather?.let {
                saveWeatherToCache(ctx, weather)
            }

        }
        return Result.success()
    }



    companion object{
        fun startWeatherWorker(context: Context) {
            val weatherWorkRequest = PeriodicWorkRequestBuilder<WeatherWorker>(
                1, TimeUnit.HOURS
            ).setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .setRequiresBatteryNotLow(true)
                    .build()
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "weather_update",
                ExistingPeriodicWorkPolicy.KEEP,
                weatherWorkRequest
            )
        }


    }
}
