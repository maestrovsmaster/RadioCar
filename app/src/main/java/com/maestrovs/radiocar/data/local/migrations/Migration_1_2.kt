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