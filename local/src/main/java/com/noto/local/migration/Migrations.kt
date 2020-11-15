package com.noto.local.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migration1To2 : Migration(1, 2) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        database.execSQL("ALTER TABLE notos RENAME COLUMN noto_id TO id")
        database.execSQL("ALTER TABLE notos RENAME COLUMN noto_title TO title")
        database.execSQL("ALTER TABLE notos RENAME COLUMN noto_body TO body")
        database.execSQL("ALTER TABLE notos RENAME COLUMN noto_is_archived TO is_archived")
        database.execSQL("ALTER TABLE notos RENAME COLUMN noto_is_starred TO is_starred")
        database.execSQL("ALTER TABLE notos RENAME COLUMN noto_position TO position")
        database.execSQL("ALTER TABLE notos RENAME COLUMN noto_creation_date TO creation_date")
        database.execSQL("ALTER TABLE notos RENAME COLUMN noto_reminder TO reminder_date")
        database.endTransaction()
    }

}