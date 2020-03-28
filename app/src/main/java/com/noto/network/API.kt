package com.noto.network

import com.noto.database.NoteDao
import com.noto.database.NotebookDao
import com.noto.database.TodoDao
import com.noto.database.TodoListDao
import com.noto.note.repository.NoteRepository
import com.noto.note.repository.NotebookRepository
import com.noto.todo.repository.TodoListRepository
import com.noto.todo.repository.TodoRepository

internal object DAOs {

    internal lateinit var notebookDao: NotebookDao

    internal lateinit var noteDao: NoteDao

    internal lateinit var todoListDao: TodoListDao

    internal lateinit var todoDao: TodoDao

}

internal object Repos {

    internal val notebookRepository by lazy {
        NotebookRepository(DAOs.notebookDao)
    }

    internal val noteRepository by lazy {
        NoteRepository(DAOs.noteDao)
    }

    internal val todoListRepository by lazy {
        TodoListRepository(DAOs.todoListDao)
    }

    internal val todoRepository by lazy {
        TodoRepository(DAOs.todoDao)
    }

}