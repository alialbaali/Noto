package com.noto.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noto.domain.model.*

private const val NOTO_DATABASE = "Noto Database"

@TypeConverters(
    NotoColorConverter::class,
    NotoIconConverter::class,
    SortTypeConverter::class,
    SortMethodConverter::class,
    StatusConverter::class,
    TypeConverter::class
)
@Database(entities = [Noto::class, Library::class, EntityStatus::class], version = 1, exportSchema = false)
abstract class NotoDatabase : RoomDatabase() {

    abstract val notoDao: NotoDao

    abstract val libraryDao: LibraryDao

    abstract val entityStatusDao: EntityStatusDao

    companion object {

        @Volatile
        private var INSTANCE: NotoDatabase? = null

        fun getInstance(context: Context): NotoDatabase =
            INSTANCE ?: synchronized(this) { INSTANCE ?: buildDatabase(context).also { INSTANCE = it } }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, NotoDatabase::class.java, NOTO_DATABASE)
                .fallbackToDestructiveMigration()
                .build()
    }
}