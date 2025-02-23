package com.maestrovs.radiocar.data.local.radio

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maestrovs.radiocar.data.entities.radio.tables.Favorites
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): LiveData<List<Favorites>>

    @Query("SELECT * FROM favorites")
    suspend fun getAllFavoritesList(): List<Favorites>

    @Query("SELECT * FROM favorites")
    fun getAllFavoritesFlow(): Flow<List<Favorites>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: Favorites)


    @Query("DELETE FROM favorites WHERE stationuuid = :stationuuid")
    suspend fun delete(stationuuid: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stations: List<Favorites>)

    @Query("DELETE FROM favorites WHERE stationuuid IN (:stationUuids)")
    suspend fun deleteAllByStationUuids(stationUuids: List<String>)

}