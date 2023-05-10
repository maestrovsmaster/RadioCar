package com.maestrovs.radiocar

import android.app.Application
import com.maestrovs.radiocar.service.AudioPlayerService
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ApplicationComponent
import javax.inject.Inject

//import dagger.hilt.android.HiltAndroidApp
//import timber.log.Timber

@HiltAndroidApp
class MainApplication : Application() {


   // @Inject
   // lateinit var audioPlayerService: AudioPlayerService

   /* @EntryPoint
    @InstallIn(ApplicationComponent::class)
    interface AudioPlayerEntryPoint {
        fun getAudioPlayerService(): AudioPlayerService
    }
*/
    override fun onCreate() {
        super.onCreate()
      //  if (BuildConfig.DEBUG) {
          //  Timber.plant(Timber.DebugTree())
      //  }


        // Initialize the AudioPlayerService
      //  val hiltEntryPoint = EntryPointAccessors.fromApplication(this, AudioPlayerEntryPoint::class.java)
      //  hiltEntryPoint.getAudioPlayerService()
    }

}