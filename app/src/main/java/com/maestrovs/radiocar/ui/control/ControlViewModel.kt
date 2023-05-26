package com.maestrovs.radiocar.ui.control

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse
import com.maestrovs.radiocar.data.repository.WeatherRepository
import com.maestrovs.radiocar.ui.main.Coords2d
import com.maestrovs.radiocar.ui.main.WeatherManager
import com.maestrovs.radiocar.utils.Resource
import kotlinx.coroutines.launch

class ControlViewModel @androidx.hilt.lifecycle.ViewModelInject constructor(
    private val weatherRepository: WeatherRepository,)
: ViewModel() {




    private var _weatherResponse = MutableLiveData<WeatherResponse>()
    val weatherResponse: LiveData<WeatherResponse>
        get() = _weatherResponse

    private var _weatherError = MutableLiveData<String>(null)
    val weatherError: LiveData<String>
        get() = _weatherError


    private var _currentLocation =  MutableLiveData<Location?>(null)
    val location get() = _currentLocation
    fun setLocation(newLocation: Location) {
        _currentLocation.value = newLocation
    }
   // var lat: Double = 50.411785
   // var lon: Double = 30.350417


    fun fetchWeather() {
        if(_currentLocation.value == null){
            _weatherError.value = "Location service is unavailable"
        }
        val location: Location = _currentLocation.value?: return


        val lat: Double = location.latitude
        val lon: Double = location.longitude




        viewModelScope.launch {
            val response = weatherRepository.getWeatherDataByCoordinates(lat, lon)
            when (response.status) {
                Resource.Status.SUCCESS -> {
                    response.data.let { weatherResp ->
                        weatherResp?.let {
                            _weatherResponse.value = it
                        }
                    }
                }

                Resource.Status.ERROR -> {

                    _weatherError.value = "Weather service is unavailable"
                }

                Resource.Status.LOADING -> {
                    // binding.progressBar.visibility = View.VISIBLE
                }
            }
        }
   }







}