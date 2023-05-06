package com.maestrovs.radiocar.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maestrovs.radiocar.data.entities.Station

@Dao
interface StationDao {

    @Query("SELECT * FROM stations")
    fun getAllStations() : LiveData<List<Station>>

    @Query("SELECT * FROM stations WHERE stationuuid = :stationuuid")
    fun getStation(stationuuid: Int): LiveData<Station>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stations: List<Station>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: Station)


}