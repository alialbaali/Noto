package com.noto.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration1To2 : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            ALTER TABLE notos
            ADD noto_is_archived INT NOT NULL DEFAULT 0 
        """.trimIndent())
    }

}