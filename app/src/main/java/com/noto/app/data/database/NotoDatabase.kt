package com.noto.app.data.database

import android.content.Context
import androidx.room.*
import com.noto.app.data.source.LabelDao
import com.noto.app.data.source.LibraryDao
import com.noto.app.data.source.NoteDao
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NoteLabel

private const val NOTO_DATABASE = "Noto Database"

@TypeConverters(
    NotoColorConverter::class,
    InstantConverter::class,
    SortingMethodConverter::class,
    SortingTypeConverter::class,
    LayoutManagerConvertor::class,
)
@Database(
    entities = [Note::class, Library::class, Label::class, NoteLabel::class],
    version = 11,
    autoMigrations = [
        AutoMigration(from = 6, to = 7),
        AutoMigration(from = 7, to = 8),
        AutoMigration(from = 8, to = 9),
        AutoMigration(from = 9, to = 10),
        AutoMigration(from = 10, to = 11),
    ],
)
abstract class NotoDatabase : RoomDatabase() {

    abstract val noteDao: NoteDao

    abstract val libraryDao: LibraryDao

    abstract val labelDao: LabelDao

    companion object {

        @Volatile
        private var INSTANCE: NotoDatabase? = null

        fun getInstance(context: Context): NotoDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context)
                .also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, NotoDatabase::class.java, NOTO_DATABASE)
                .addMigrations(
//                    RemoveNotoPrefix,
//                    RenameNotosTableToNotes,
//                    RemoveLibraryPrefix,
//                    AddSortingColumns,
//                    RemoveNotoIcon,
                )
                .build()
    }
}