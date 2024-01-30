package com.maestrovs.radiocar.service

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.remote.radio.StationRemoteDataSource
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.utils.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlayerServiceModel @Inject  constructor(
    //private val repository: StationRepository,
    @ApplicationContext private val context: Context,
    private val remoteDataSource: StationRemoteDataSource
    ) {




    var currentStation: Station? = null

   // private var _stations = MutableLiveData<Resource<List<Station>>>(null)
    //val stations: LiveData<Resource<List<Station>>> = _stations

    suspend fun fetchStations(countryCode: String): Resource<List<Station>> {
        // Логіка для отримання наступної станції та її відтворення
        // val stations = repository.fetchStations() // Отримуємо список станцій
        // Вибір наступної станції на основі currentStation
        // Відтворення станції
        return remoteDataSource.getStations(countryCode,100)
            //repository.getStations(countryCode).value



    }

    // Інша логіка, подібна до того, що можна було б знайти в ViewModel
}
