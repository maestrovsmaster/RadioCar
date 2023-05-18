package com.maestrovs.radiocar.ui.control

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse
import com.maestrovs.radiocar.data.repository.WeatherRepository
import com.maestrovs.radiocar.utils.Resource
import kotlinx.coroutines.launch

class ControlViewModel @androidx.hilt.lifecycle.ViewModelInject constructor(
    private val weatherRepository: WeatherRepository,)
: ViewModel() {




    private var _weatherResponse = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse>
        get() = _weatherResponse


    var lat: Double = 50.411785
    var lon: Double = 30.350417


    fun fetchWeather() {
        viewModelScope.launch {
            val response = weatherRepository.getWeatherDataByCoordinates(lat, lon)
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    // binding.progressBar.visibility = View.GONE

                    response.data.let { weatherResp ->
                        weatherResp?.let {
                            _weatherResponse.value = it
                        }

                        Log.d("WeatherR","weatherResponse = $weatherResponse")

                    }
                }

                Resource.Status.ERROR -> {
                    // binding.progressBar.visibility = View.GONE
                    Log.d("WeatherR","weatherResponse err")
                }

                Resource.Status.LOADING -> {
                    // binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
   }







}