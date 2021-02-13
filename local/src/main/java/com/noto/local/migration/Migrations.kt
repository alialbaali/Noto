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

object Migration2To3 : Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        database.execSQL("ALTER TABLE notos RENAME TO notes")
        database.endTransaction()
    }

}

object Migration3To4 : Migration(3, 4) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        database.execSQL("ALTER TABLE libraries RENAME COLUMN library_id TO id")
        database.execSQL("ALTER TABLE libraries RENAME COLUMN library_title TO title")
        database.execSQL("ALTER TABLE libraries RENAME COLUMN library_position TO position")
        database.execSQL("ALTER TABLE libraries RENAME COLUMN noto_color TO color")
        database.execSQL("ALTER TABLE libraries RENAME COLUMN noto_icon TO icon")
        database.execSQL("ALTER TABLE libraries RENAME COLUMN library_creation_date TO creation_date")
        database.endTransaction()
    }

}

object Migration4To5 : Migration(4, 5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        database.execSQL("ALTER TABLE libraries ADD COLUMN sorting_method INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE libraries ADD COLUMN sorting_type INTEGER NOT NULL DEFAULT 1")
        database.endTransaction()
    }

}