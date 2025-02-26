package com.maestrovs.radiocar.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.maestrovs.radiocar.common.Constants.BASE_RADIO_URL
import com.maestrovs.radiocar.common.Constants.BASE_WEATHER_URL
import com.maestrovs.radiocar.data.local.radio.AppDatabase
import com.maestrovs.radiocar.data.local.radio.FavoritesDao
import com.maestrovs.radiocar.data.local.radio.RecentDao
import com.maestrovs.radiocar.data.local.radio.StationDao
import com.maestrovs.radiocar.data.net.CustomLoggingInterceptor
import com.maestrovs.radiocar.data.remote.radio.StationRemoteDataSource
import com.maestrovs.radiocar.data.remote.radio.StationService
import com.maestrovs.radiocar.data.remote.weather.WeatherRemoteDataSource
import com.maestrovs.radiocar.data.remote.weather.WeatherService
import com.maestrovs.radiocar.data.repository.StationRepository
import com.maestrovs.radiocar.data.repository.StationRepositoryIml
import com.maestrovs.radiocar.data.repository.WeatherRepository
import com.maestrovs.radiocar.service.player.ExoPlayerManager
import com.maestrovs.radiocar.service.player.MediaSessionHelper2
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepository
import com.maestrovs.radiocar.ui.app.radio_fragment.ui_radio_view_model.repositories.SharedPreferencesRepositoryIml
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    //Common

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()


    //Radio

    @Singleton
    @Provides
    @Named("radio")
    fun provideRadioRetrofit(gson: Gson): Retrofit {

        val interceptor = CustomLoggingInterceptor()//HttpLoggingInterceptor()
        //interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()


        return Retrofit.Builder()
            .baseUrl(BASE_RADIO_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    fun provideStationService(@Named("radio") retrofit: Retrofit): StationService = retrofit.create(
        StationService::class.java
    )

    @Singleton
    @Provides
    fun provideStationRemoteDataSource(stationService: StationService) =
        StationRemoteDataSource(stationService)

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context) =
        AppDatabase.getDatabase(appContext)

    @Singleton
    @Provides
    fun provideStationDao(db: AppDatabase) = db.stationDao()

    @Singleton
    @Provides
    fun provideRecentDao(db: AppDatabase) = db.recentDao()

    @Singleton
    @Provides
    fun provideFavoritesDao(db: AppDatabase) = db.favoritesDao()

    @Singleton
    @Provides
    fun provideRadioRepository(
        remoteDataSource: StationRemoteDataSource,
        localDataSource: StationDao, recentSource: RecentDao, favoritesSource: FavoritesDao
    ): StationRepository =
        StationRepositoryIml(remoteDataSource, localDataSource, recentSource, favoritesSource)


    @Singleton
    @Provides
    fun provideSharedPreferencesRepository(@ApplicationContext context: Context,localDataSource: StationDao,): SharedPreferencesRepository =
        SharedPreferencesRepositoryIml(context = context, localDataSource = localDataSource)

    //Weather

    @Singleton
    @Provides
    @Named("weather")
    fun provideWeatherRetrofit(gson: Gson): Retrofit {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        return Retrofit.Builder()
            .baseUrl(BASE_WEATHER_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    fun provideWeatherService(@Named("weather") retrofit: Retrofit): WeatherService =
        retrofit.create(
            WeatherService::class.java
        )

    @Singleton
    @Provides
    fun provideWeatherRemoteDataSource(weatherService: WeatherService) =
        WeatherRemoteDataSource(weatherService)

    @Singleton
    @Provides
    fun provideWeatherRepository(
        remoteDataSource: WeatherRemoteDataSource,
    ) =
        WeatherRepository(remoteDataSource)

    @Singleton
    @Provides
    fun provideMediaSessionHelper1(
        @ApplicationContext context: Context
    ): MediaSessionHelper2 = MediaSessionHelper2(context)


    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun provideExoPlayerManager(
        @ApplicationContext context: Context,
        mediaSessionHelper: MediaSessionHelper2
    ): ExoPlayerManager = ExoPlayerManager(context, mediaSessionHelper)

}




