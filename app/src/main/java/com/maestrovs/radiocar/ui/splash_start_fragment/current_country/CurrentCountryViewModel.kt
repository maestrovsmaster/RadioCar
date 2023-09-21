package com.maestrovs.radiocar.ui.splash_start_fragment.current_country

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Reader


data class LocationResponse(val country: String?)

class LocationAPIDeserializer : ResponseDeserializable<LocationResponse> {
    override fun deserialize(reader: Reader) = Gson().fromJson(reader, LocationResponse::class.java)
}

class CurrentCountryViewModel: ViewModel() {


    private val _detectedCountry = MutableLiveData<String?>()
    val detectedCountry : LiveData<String?> = _detectedCountry

    fun detectCountryFromIp() {
        FuelManager.instance.basePath = "http://ip-api.com"
        Fuel.get("/json").responseObject(LocationAPIDeserializer()) { _, _, result ->
            when (result) {


                is com.github.kittinunf.result.Result.Success -> {
                    _detectedCountry.postValue(result.value.country ?: "Unknown")
                }

                is com.github.kittinunf.result.Result.Failure -> {
                    val exception = result.getException()

                }
            }
        }
    }


}