package com.maestrovs.radiocar.data.remote.radio

import com.maestrovs.radiocar.data.entities.radio.Station
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StationService {

    @GET("/json/stations/search")
    suspend fun getStationsExt(
        @Query("countrycode") country: String = "UA",
        @Query("name") name: String = "",
        @Query("tag") tag: String = "",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 200
    ): Response<List<Station>>

    @GET("/json/stations/search")
    suspend fun getStations(
        @Query("countrycode") country: String = "UA",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 200
    ): Response<List<Station>>

    @GET("/json/stations/byname/{searchterm}")
    suspend fun getStationsByName(
        @Path("searchterm") searchterm: String,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 200
    ): Response<List<Station>>

}