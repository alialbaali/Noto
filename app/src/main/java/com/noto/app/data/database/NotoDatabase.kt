package com.noto.app.data.database

import android.content.Context
import androidx.room.*
import com.noto.app.data.source.FolderDao
import com.noto.app.data.source.LabelDao
import com.noto.app.data.source.NoteDao
import com.noto.app.data.source.NoteLabelDao
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NoteLabel

private const val NOTO_DATABASE = "Noto Database"

@TypeConverters(
    NotoColorConverter::class,
    InstantConverter::class,
    LayoutConvertor::class,
    SortingTypeConverter::class,
    SortingOrderConverter::class,
    GroupingConvertor::class,
    NewNoteCursorPositionConvertor::class,
    GroupingOrderConverter::class,
    FilteringTypeConverter::class,
    OpenNotesInConverter::class,
)
@Database(
    entities = [Folder::class, Note::class, Label::class, NoteLabel::class],
    version = 32,
    autoMigrations = [
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
        AutoMigration(from = 11, to = 12),
        AutoMigration(from = 12, to = 13),
        AutoMigration(from = 13, to = 14, spec = Migrations.RenameIsStarredColumn::class),
        AutoMigration(from = 14, to = 15, spec = Migrations.DeleteSortingTypeAndSortingMethodColumns::class),
        AutoMigration(from = 15, to = 16, spec = Migrations.DeleteLabelAndNoteLabelTables::class),
        AutoMigration(from = 16, to = 17),
        AutoMigration(from = 17, to = 18),
        AutoMigration(from = 18, to = 19, spec = Migrations.RenameNoteLabelsTable::class),
        AutoMigration(from = 19, to = 20),
        AutoMigration(from = 20, to = 21, spec = Migrations.RenameLayoutManagerColumn::class),
        AutoMigration(from = 21, to = 22, spec = Migrations.RenameNoteListSortingTypeColumn::class),
        /** Add isVaulted properties to [Folder] and [Note] */
        AutoMigration(from = 22, to = 23),
        /** Add accessDate property to [Note] */
        AutoMigration(from = 23, to = 24),
        /** Rename isSetNewNoteCursorOnTitle property to newNoteCursorPosition in [Folder] */
        AutoMigration(from = 24, to = 25, spec = Migrations.RenameIsSetNewNoteCursorOnTitle::class),
        /** Add parentId property to [Folder] */
        AutoMigration(from = 25, to = 26),
        /** Rename Library to [Folder] */
        AutoMigration(from = 26, to = 27, spec = Migrations.RenameLibraryToFolder::class),
        /** Add [GroupingOrder] to [Folder] */
        AutoMigration(from = 27, to = 28),
        /** Add [Folder.scrollingPosition] and [Note.scrollingPosition] */
        AutoMigration(from = 28, to = 29),
        /** Add [FilteringType] to [Folder] */
        AutoMigration(from = 29, to = 30),
//        /** Add [Folder.openNotesIn] */
        AutoMigration(from = 31, to = 32),
    ],
)
abstract class NotoDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao

    abstract val folderDao: FolderDao

    abstract val labelDao: LabelDao

    abstract val noteLabelDao: NoteLabelDao

    companion object {

        @Volatile
        private var INSTANCE: NotoDatabase? = null

        fun getInstance(context: Context): NotoDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context)
                .also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) = Room
            .databaseBuilder(context.applicationContext, NotoDatabase::class.java, NOTO_DATABASE)
            .addMigrations(Migrations.SetAccessDateToCreationDate)
            .build()
    }
}