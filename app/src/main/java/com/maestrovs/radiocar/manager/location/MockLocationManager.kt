package com.maestrovs.radiocar.manager.location

import android.location.Location
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Created by maestromaster$ on 24/02/2025$.
 */




 fun startMockLocationUpdates(scope: CoroutineScope) {
    scope.launch {
        val baseLat = 51.5074 // Лондон, наприклад
        val baseLon = -0.1278
        var speed = 0f
        val maxSpeed = 120f // Максимальна швидкість у км/год
        var latitude = baseLat
        var longitude = baseLon

        while (true) {
            // Етап 1: Розгін до 100 км/год протягом 10 секунд
            while (speed < 100f) {
                speed += Random.nextFloat() * 5
                latitude += 0.0001
                longitude += 0.0001
                updateMockLocation(latitude, longitude, speed)
                delay(1000)
            }

            // Етап 2: Їзда на швидкості 100-120 км/год протягом 20 секунд
            repeat(20) {
                speed = 100f + Random.nextFloat() * 20
                latitude += 0.0002
                longitude += 0.0002
                updateMockLocation(latitude, longitude, speed)
                delay(1000)
            }

            // Етап 3: Зниження швидкості до 50 км/год протягом 5 секунд
            while (speed > 50f) {
                speed -= Random.nextFloat() * 5
                latitude += 0.00005
                longitude += 0.00005
                updateMockLocation(latitude, longitude, speed)
                delay(1000)
            }

            // Етап 4: Їзда на 50 км/год протягом 15 секунд
            repeat(15) {
                speed = 50f + Random.nextFloat() * 5
                latitude += 0.0001
                longitude += 0.0001
                updateMockLocation(latitude, longitude, speed)
                delay(1000)
            }

            // Етап 5: Зупинка протягом 5 секунд
            repeat(5) {
                speed = 0f
                updateMockLocation(latitude, longitude, speed)
                delay(1000)
            }
        }
    }
}

private fun updateMockLocation(lat: Double, lon: Double, speed: Float) {
    val mockLocation = Location("mock").apply {
        latitude = lat
        longitude = lon
        this.speed = (speed / 3.6f) // Переводимо в м/с
    }
    LocationStateManager.updateLocation(mockLocation)
}
