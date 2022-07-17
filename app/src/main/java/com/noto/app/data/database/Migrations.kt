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

    @RenameColumn(tableName = "libraries", fromColumnName = "sorting", toColumnName = "sorting_type")
    class RenameNoteListSortingTypeColumn : AutoMigrationSpec

    @RenameColumn(tableName = "libraries", fromColumnName = "is_set_new_note_cursor_on_title", toColumnName = "new_note_cursor_position")
    class RenameIsSetNewNoteCursorOnTitle : AutoMigrationSpec

    @RenameTable(fromTableName = "libraries", toTableName = "folders")
    @RenameColumn.Entries(
        RenameColumn(tableName = "notes", fromColumnName = "library_id", toColumnName = "folder_id"),
        RenameColumn(tableName = "labels", fromColumnName = "library_id", toColumnName = "folder_id"),
    )
    class RenameLibraryToFolder : AutoMigrationSpec

    object SetAccessDateToCreationDate : Migration(30, 31) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("""
                CREATE TABLE notes_tmp(
                   id INTEGER NOT NULL PRIMARY KEY, 
                   folder_id INTEGER NOT NULL REFERENCES folders(id) ON DELETE CASCADE,
                   title TEXT NOT NULL,
                   body TEXT NOT NULL,
                   position INTEGER NOT NULL,
                   creation_date TEXT NOT NULL,
                   is_pinned INTEGER NOT NULL DEFAULT 0,
                   is_archived INTEGER NOT NULL DEFAULT 0,
                   reminder_date TEXT DEFAULT NULL,
                   is_vaulted INTEGER NOT NULL DEFAULT 0,
                   access_date TEXT NOT NULL DEFAULT 'creation_date',
                   scrolling_position INTEGER NOT NULL DEFAULT 0
                );
                """.trimIndent())
            database.execSQL(
                """INSERT INTO notes_tmp 
                    |SELECT 
                    | id, folder_id, title, body, position, creation_date,
                    | is_pinned, is_archived, reminder_date,
                    | is_vaulted,
                    | CASE WHEN access_date IS NULL THEN creation_date ELSE access_date END,
                    | scrolling_position FROM notes;""".trimMargin()
            )
            database.execSQL("""DROP TABLE notes;""")
            database.execSQL("""ALTER TABLE notes_tmp RENAME TO notes;""")
        }
    }
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