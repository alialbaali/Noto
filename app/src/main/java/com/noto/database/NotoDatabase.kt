package com.noto.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.noto.note.model.Note
import com.noto.note.model.Notebook
import com.noto.todo.model.SubTodo
import com.noto.todo.model.Todo
import com.noto.todo.model.Todolist

private const val NOTO_DATABASE = "Noto Database"

@TypeConverters(
    NotoColorConverter::class,
    DateConverter::class,
    SortTypeConverter::class,
    SortMethodConverter::class
)
@Database(
    entities = [Notebook::class, Note::class, Todolist::class, Todo::class, SubTodo::class],
    version = 1,
    exportSchema = false
)
abstract class NotoDatabase : RoomDatabase() {

    abstract val notebookDao: NotebookDao

    abstract val noteDao: NoteDao

    abstract val todolistDao: TodolistDao

    abstract val todoDao: TodoDao

    abstract val subTodoDao: SubTodoDao

    companion object {

        @Volatile
        private var INSTANCE: NotoDatabase? = null

        fun getInstance(context: Context): NotoDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                NotoDatabase::class.java,
                NOTO_DATABASE
            )
                .fallbackToDestructiveMigration()
                .build()
    }
}