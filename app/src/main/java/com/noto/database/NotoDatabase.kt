package com.noto.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noto.domain.Library
import com.noto.domain.NoteBlock
import com.noto.domain.Noto
import com.noto.domain.TodoBlock

private const val NOTO_DATABASE = "Noto Database"

@TypeConverters(
    NotoColorConverter::class,
    NotoIconConverter::class,
    SortTypeConverter::class,
    SortMethodConverter::class,
    BlockConverter::class,
    DateConverter::class
)
@Database(entities = [NoteBlock::class, TodoBlock::class, Noto::class, Library::class], version = 1, exportSchema = false)
abstract class NotoDatabase : RoomDatabase() {

    abstract val notoDao: NotoDao

    abstract val blockDao: BlockDao

    abstract val libraryDao: LibraryDao

    companion object {

        @Volatile
        private var INSTANCE: NotoDatabase? = null

        fun getInstance(context: Context): NotoDatabase =
            INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, NotoDatabase::class.java, NOTO_DATABASE).fallbackToDestructiveMigration().build()
    }
}