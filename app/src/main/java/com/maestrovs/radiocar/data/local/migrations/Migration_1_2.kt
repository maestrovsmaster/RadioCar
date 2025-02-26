package com.maestrovs.radiocar.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Created by maestromaster on 10/02/2025$.
 */

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE stations ADD COLUMN serveruuid TEXT NOT NULL DEFAULT ''")
    }
}

val MIGRATION_1_5 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE stations ADD COLUMN isRecent INTEGER NOT NULL DEFAULT 0")
    }
}

val MIGRATION_1_6 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE recent ADD COLUMN lastPlayedTime INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE favorites ADD COLUMN lastPlayedTime INTEGER NOT NULL DEFAULT 0")
    }
}