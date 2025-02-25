package com.maestrovs.radiocar.service.location

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Looper
import androidx.annotation.OptIn
import androidx.core.app.ActivityCompat
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.maestrovs.radiocar.manager.location.LocationStateManager
import com.maestrovs.radiocar.ui.main.MainActivity.Companion.PERMISSIONS_REQUEST_LOCATION

/**
 * Created by maestromaster$ on 25/02/2025$.
 */

object LocationManager {

      var fusedLocationClient: FusedLocationProviderClient? = null
      var locationCallback: LocationCallback? = null

    @OptIn(UnstableApi::class)
     fun startLocationUpdates(activity: Activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)

        val locationRequest = LocationRequest.create().apply {
            interval = 1000 // Update interval in milliseconds
            fastestInterval = 500 // Fastest update interval
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                for (location in locationResult.locations) {
                    LocationStateManager.updateLocation(location)
                    //Log.d("LocationManager", "Location: ${location.latitude}, ${location.longitude}")
                }
            }

            override fun onLocationAvailability(locationAvailability: com.google.android.gms.location.LocationAvailability) {
                super.onLocationAvailability(locationAvailability)
                //Log.d("LocationManager", "LocationAvailability: ${locationAvailability.isLocationAvailable}")
                LocationStateManager.updateLocationAvailability(locationAvailability.isLocationAvailable)
            }
        }

        if (ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_LOCATION
            )
            return
        }
        locationCallback?.let {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        }

    }
}