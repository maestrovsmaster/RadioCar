package com.maestrovs.radiocar.di

import android.content.Context
import com.maestrovs.radiocar.data.local.AppDatabase
import com.maestrovs.radiocar.data.local.StationDao
import com.maestrovs.radiocar.data.remote.StationRemoteDataSource
import com.maestrovs.radiocar.data.remote.StationService
import com.maestrovs.radiocar.data.repository.StationRepository
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.maestrovs.radiocar.common.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson) : Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideCharacterService(retrofit: Retrofit): StationService = retrofit.create(
        StationService::class.java)

    @Singleton
    @Provides
    fun provideCharacterRemoteDataSource(stationService: StationService) = StationRemoteDataSource(stationService)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) = AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideCharacterDao(db: AppDatabase) = db.stationDao()

    @Singleton
    @Provides
    fun provideRepository(remoteDataSource: StationRemoteDataSource,
                          localDataSource: StationDao
    ) =
        StationRepository(remoteDataSource, localDataSource)




}