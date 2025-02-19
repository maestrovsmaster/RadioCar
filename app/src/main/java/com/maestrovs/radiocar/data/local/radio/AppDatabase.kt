package com.maestrovs.radiocar.data.local.radio

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.maestrovs.radiocar.data.entities.radio.Station
import com.maestrovs.radiocar.data.entities.radio.tables.Favorites
import com.maestrovs.radiocar.data.entities.radio.tables.Recent
import com.maestrovs.radiocar.data.local.migrations.MIGRATION_1_2
import com.maestrovs.radiocar.data.local.migrations.MIGRATION_1_5

@Database(entities = [Station::class, Recent::class, Favorites::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun stationDao(): StationDao

    abstract fun recentDao(): RecentDao

    abstract fun favoritesDao(): FavoritesDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase =
            instance ?: synchronized(this) { instance ?: buildDatabase(context).also { instance = it } }

        private fun buildDatabase(appContext: Context) =
            Room.databaseBuilder(appContext, AppDatabase::class.java, "radio_car_db")
                .addMigrations(MIGRATION_1_2) // version = 4,
                .addMigrations(MIGRATION_1_5)// version = 5,
                .fallbackToDestructiveMigration()
                .build()
    }

}