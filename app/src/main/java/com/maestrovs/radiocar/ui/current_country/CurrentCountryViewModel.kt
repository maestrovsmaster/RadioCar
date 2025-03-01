package com.maestrovs.radiocar.ui.current_country

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.Gson
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.LocationAPIDeserializer
import java.io.Reader

/*
data class LocationResponse(val country: String?)

class LocationAPIDeserializer : ResponseDeserializable<LocationResponse> {
    override fun deserialize(reader: Reader) = Gson().fromJson(reader, LocationResponse::class.java)
}*/

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