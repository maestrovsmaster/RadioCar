package com.maestrovs.radiocar.data.local.radio

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maestrovs.radiocar.data.entities.radio.tables.Favorites

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): LiveData<List<Favorites>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: Favorites)


    @Query("DELETE FROM favorites WHERE stationuuid = :stationuuid")
    suspend fun delete(stationuuid: String)


}