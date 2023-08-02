package com.maestrovs.radiocar.ui.control

import android.content.Context
import com.maestrovs.radiocar.common.SharedManager
import android.location.Location

object WeatherManager {

    private val LAST_LOCATION_LAT = "LAST_LOCATION_LAT"
    private val LAST_LOCATION_LON = "LAST_LOCATION_LON"


    public fun setCurrentLocationCoords(context: Context, coords: Coords2d) {
        SharedManager.writeStringOption(context, LAST_LOCATION_LAT, coords.lat.toString())
        SharedManager.writeStringOption(context, LAST_LOCATION_LON, coords.lon.toString())

    }

    public fun getLastCoords(context: Context): Coords2d? {
        val lat = try {
             SharedManager.readStringOptions(context, LAST_LOCATION_LAT)?.toDouble() ?: kotlin.run {
                 null
             }
        } catch (e: Exception) {
            null
        }

        val lon = try {
            SharedManager.readStringOptions(context, LAST_LOCATION_LON)?.toDouble() ?: kotlin.run {
                null
            }
        } catch (e: Exception) {
            null
        }

        return if(lat != null && lon != null){
            Coords2d(lat, lon)
        }else{
            null
        }

    }




}

data class Coords2d(val lat: Double, val lon: Double)

fun Location.getCoords2d() = Coords2d(this.latitude, this.longitude)
