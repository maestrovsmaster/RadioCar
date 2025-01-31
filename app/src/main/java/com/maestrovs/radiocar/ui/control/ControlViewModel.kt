package com.maestrovs.radiocar.ui.control

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.common.CurrentCountryManager
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse
import com.maestrovs.radiocar.data.remote.weather.WeatherService
import com.maestrovs.radiocar.data.repository.WeatherRepository
import com.maestrovs.radiocar.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControlViewModel @Inject constructor(
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


    var lastLocation: Coords2d? = null

    var countryName: String = CurrentCountryManager.DEFAULT_CITY


    fun fetchWeather() {

        Log.d("Weather","fetchWeather $lastLocation")

        val location: Location? = _currentLocation.value


        val lat: Double? = location?.latitude?:lastLocation?.lat
        val lon: Double? = location?.longitude?:lastLocation?.lon

        viewModelScope.launch {
            val response = if(lat != null && lon != null) {
                weatherRepository.getWeatherDataByCoordinates(lat, lon)
            }else{
                Log.d("Weather","fetchWeather $countryName")
                weatherRepository.getWeatherDataByCity(countryName)
            }
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