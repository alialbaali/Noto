package com.noto.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noto.note.model.Note
import com.noto.note.model.Notebook
import com.noto.note.model.NotebookColorConverter

@TypeConverters(NotebookColorConverter::class)
@Database(
    entities = [Notebook::class, Note::class],
    version = 1,
    exportSchema = false
)
 abstract class AppDatabase : RoomDatabase() {

     abstract val notebookDao: NotebookDao
     abstract val noteDao: NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


         fun getInstance(context: Context): AppDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context,
                            AppDatabase::class.java,
                            "AppDatabase"
                        )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}