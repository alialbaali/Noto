package com.noto.app.data.database

import androidx.room.DeleteColumn
import androidx.room.DeleteTable
import androidx.room.RenameColumn
import androidx.room.RenameTable
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {
    @DeleteColumn.Entries(
        DeleteColumn(tableName = "libraries", columnName = "sorting_type"),
        DeleteColumn(tableName = "libraries", columnName = "sorting_method"),
    )
    class DeleteSortingTypeAndSortingMethodColumns : AutoMigrationSpec

    @RenameColumn(tableName = "notes", fromColumnName = "is_starred", toColumnName = "is_pinned")
    class RenameIsStarredColumn : AutoMigrationSpec

    @DeleteTable.Entries(
        DeleteTable(tableName = "labels"),
        DeleteTable(tableName = "noto_labels"),
    )
    class DeleteLabelAndNoteLabelTables : AutoMigrationSpec

    @RenameTable(fromTableName = "noto_labels", toTableName = "note_labels")
    class RenameNoteLabelsTable : AutoMigrationSpec

    @RenameColumn(tableName = "libraries", fromColumnName = "layout_manager", toColumnName = "layout")
    class RenameLayoutManagerColumn : AutoMigrationSpec
}

object RemoveNotoPrefix : Migration(1, 2) {

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

object RenameNotosTableToNotes : Migration(2, 3) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        database.execSQL("ALTER TABLE notos RENAME TO notes")
        database.endTransaction()
    }

}

object RemoveLibraryPrefix : Migration(3, 4) {

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

object AddSortingColumns : Migration(4, 5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        database.execSQL("ALTER TABLE libraries ADD COLUMN sorting_method INTEGER NOT NULL DEFAULT 1")
        database.execSQL("ALTER TABLE libraries ADD COLUMN sorting_type INTEGER NOT NULL DEFAULT 1")
        database.endTransaction()
    }

}

object RemoveNotoIcon : Migration(5, 6) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.beginTransaction()
        database.execSQL("CREATE TEMPORARY TABLE libraries_backup(id INTEGER NOT NULL PRIMARY KEY, title TEXT NOT NULL, position INTEGER NOT NULL, color INTEGER NOT NULL, creation_date TEXT NOT NULL, sorting_type INTEGER NOT NULL, sorting_method INTEGER NOT NULL);")
        database.execSQL("INSERT INTO libraries_backup SELECT id, title, position, color, creation_date, sorting_type, sorting_method FROM libraries;")
        database.execSQL("DROP TABLE libraries;")
        database.execSQL("CREATE TABLE libraries(id INTEGER NOT NULL PRIMARY KEY, title TEXT NOT NULL, position INTEGER NOT NULL, color INTEGER NOT NULL, creation_date TEXT NOT NULL, sorting_type INTEGER NOT NULL, sorting_method INTEGER NOT NULL);")
        database.execSQL("INSERT INTO libraries SELECT id, title, position, color, creation_date, sorting_type, sorting_method FROM libraries_backup;")
        database.execSQL("DROP TABLE libraries_backup;")
        database.endTransaction()
    }
}