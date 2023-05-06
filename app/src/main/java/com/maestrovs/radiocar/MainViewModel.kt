package com.maestrovs.radiocar

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.maestrovs.radiocar.data.entities.Station
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.utils.Resource

class MainViewModel @androidx.hilt.lifecycle.ViewModelInject constructor(
    private val mainRepository: StationRepository
) : ViewModel() {

    fun getData(): LiveData<Resource<List<Station>>> {
        return mainRepository.getStations()
    }


}