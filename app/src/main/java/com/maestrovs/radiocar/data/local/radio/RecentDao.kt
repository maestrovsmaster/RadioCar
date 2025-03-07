package com.maestrovs.radiocar.data.local.radio

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maestrovs.radiocar.data.entities.radio.tables.Favorites
import com.maestrovs.radiocar.data.entities.radio.tables.Recent

@Dao
interface RecentDao {

    @Query("SELECT * FROM recent")
    fun getAllRecent(): LiveData<List<Recent>>

    @Query("SELECT * FROM favorites")
    suspend fun getAllRecentList(): List<Recent>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: Recent)



    @Query("DELETE FROM recent WHERE stationuuid = :stationuuid")
    suspend fun delete(stationuuid: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stations: List<Recent>)


    @Query("DELETE FROM recent WHERE stationuuid IN (:stationUuids)")
    suspend fun deleteAllByStationUuids(stationUuids: List<String>)

    @Query("UPDATE recent SET lastPlayedTime = :time WHERE stationuuid = :stationuuid")
    fun updateLastPlayedTime(stationuuid: String, time: Long)

    @Query("SELECT * FROM recent ORDER BY lastPlayedTime DESC")
    fun getRecentStations(): List<Recent>

}