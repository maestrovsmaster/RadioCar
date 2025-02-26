package com.maestrovs.radiocar.data.local.radio

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maestrovs.radiocar.data.entities.radio.Station
import kotlinx.coroutines.flow.Flow

@Dao
interface StationDao {

    @Query("SELECT * FROM stations")
    fun getAllStations(): LiveData<List<Station>>

    @Query("SELECT * FROM stations WHERE countrycode = :countryCode")
    fun getStationsByCountryCode(countryCode: String): LiveData<List<Station>>

    @Query("SELECT * FROM stations WHERE countrycode = :countryCode")
    fun getStationsByCountryCodeFlow(countryCode: String): Flow<List<Station>>

    @Query(
        """
    SELECT stations.*, 
           (favorites.stationuuid IS NOT NULL) AS isFavorite,
           (recent.stationuuid IS NOT NULL) AS isRecent
    FROM stations
    LEFT JOIN favorites ON stations.stationuuid = favorites.stationuuid
    LEFT JOIN recent ON stations.stationuuid = recent.stationuuid
    WHERE countrycode = :countryCode
"""
    )
    fun getStationsByCountryWithFavouriteStatusFlow(countryCode: String): Flow<List<Station>>


    @Query(
        """
        SELECT stations.* FROM stations
        INNER JOIN recent ON stations.stationuuid = recent.stationuuid
        ORDER BY recent.lastPlayedTime DESC
    """
    )
    fun getRecentStationDetailsByLastTime(): Flow<List<Station>>


    @Query(
        """
        SELECT stations.* FROM stations
        INNER JOIN favorites ON stations.stationuuid = favorites.stationuuid
        ORDER BY favorites.lastPlayedTime DESC
    """
    )
    fun getFavoriteStationDetailsByLastTime(): Flow<List<Station>>


    @Query("SELECT * FROM stations WHERE name LIKE '%' || :searchterm || '%'")
    fun getStationsByNameFlow(searchterm: String): Flow<List<Station>>


    //     SELECT Item.id, Item.name, (Favourite.id IS NOT NULL) as isFavorite
    @Query(
        """
        SELECT stations.*, (favorites.stationuuid IS NOT NULL) as isFavorite
        FROM stations
        LEFT JOIN favorites ON stations.stationuuid = favorites.stationuuid
    """
    )
    fun getAllStationsWithFavouriteStatus(): LiveData<List<Station>>

    @Query(
        """
        SELECT stations.*, (favorites.stationuuid IS NOT NULL) as isFavorite
        FROM stations
        LEFT JOIN favorites ON stations.stationuuid = favorites.stationuuid
    """
    )
    fun getAllStationsWithFavouriteStatusFlow(): Flow<List<Station>>


    @Query("SELECT * FROM stations WHERE stationuuid = :stationuuid")
    fun getStation(stationuuid: String): LiveData<Station>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stations: List<Station>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(station: Station)

    @Query(
        """
    SELECT stations.* , (favorites.stationuuid IS NOT NULL) as isFavorite
    FROM stations 
    INNER JOIN recent ON stations.stationuuid = recent.stationuuid LEFT JOIN favorites ON stations.stationuuid = favorites.stationuuid
    ORDER BY recent.id DESC
    """
    )
    fun getRecentStations(): LiveData<List<Station>>

    @Query(
        """
    SELECT stations.* , (favorites.stationuuid IS NOT NULL) as isFavorite
    FROM stations 
    INNER JOIN recent ON stations.stationuuid = recent.stationuuid LEFT JOIN favorites ON stations.stationuuid = favorites.stationuuid
    ORDER BY recent.id DESC
    """
    )
    fun getRecentStationsFlow(): Flow<List<Station>>

    @Query(
        """
    SELECT stations.* , (favorites.stationuuid IS NOT NULL) as isFavorite
    FROM stations 
    INNER JOIN favorites ON stations.stationuuid = favorites.stationuuid
    ORDER BY favorites.id DESC
"""
    )
    fun getFavoritesStations(): LiveData<List<Station>>


    @Query(
        """
    SELECT stations.* , (favorites.stationuuid IS NOT NULL) as isFavorite
    FROM stations 
    INNER JOIN favorites ON stations.stationuuid = favorites.stationuuid
    ORDER BY favorites.id DESC
"""
    )
    fun getFavoritesStationsFlow(): Flow<List<Station>>


    @Query("SELECT * FROM stations WHERE stationuuid IN (:ids)")
    fun getStationsByIds(ids: List<String>): List<Station>


}