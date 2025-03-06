package com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maestrovs.radiocar.data.entities.weather.WeatherResponse
import com.maestrovs.radiocar.data.repository.WeatherRepository
import com.maestrovs.radiocar.data.repository.WeatherRepositoryIml
import com.maestrovs.radiocar.manager.location.LocationStateManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by maestromaster$ on 02/03/2025$.
 */


@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository
) : ViewModel() {

    private val _weather = MutableStateFlow(weatherRepository.getCachedWeather())
    val weather: StateFlow<WeatherResponse> = _weather

    init {
        observeLocationUpdates()
    }

    fun initWeather(context: Context) {
        LocationStateManager.initLocation(context)
        observeLocationUpdates()
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            combine(
                LocationStateManager.locationWeatherState,
                LocationStateManager.locationAvailability
            ) { location, available ->
                Pair(location, available)
            }.collect { (location, available) ->
                if(location == null) return@collect
                if (available && location.latitude != 0.0 && location.longitude != 0.0) {
                    refreshWeather(location.latitude, location.longitude)
                }
            }
        }
    }

    fun refreshWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            val updatedWeather = weatherRepository.getWeatherDataByCoordinates(lat, lon)

            updatedWeather?.let {
                _weather.value = updatedWeather
            }

        }
    }
}

