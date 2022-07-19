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
            database.beginTransaction()
            try {
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
                   access_date TEXT     NOT NULL DEFAULT 'creation_date',
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
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }
        }
    }
}
