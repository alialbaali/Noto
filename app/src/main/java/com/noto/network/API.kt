package com.noto.network

import com.noto.database.*
import com.noto.note.repository.NoteRepository
import com.noto.note.repository.NotebookRepository
import com.noto.todo.repository.SubTodoRepository
import com.noto.todo.repository.TodolistRepository
import com.noto.todo.repository.TodoRepository

internal object DAOs {

    internal lateinit var notebookDao: NotebookDao

    internal lateinit var noteDao: NoteDao

    internal lateinit var todolistDao: TodolistDao

    internal lateinit var todoDao: TodoDao

    internal lateinit var subTodoDao: SubTodoDao

}

internal object Repos {

    internal val notebookRepository by lazy {
        NotebookRepository(DAOs.notebookDao)
    }

    internal val noteRepository by lazy {
        NoteRepository(DAOs.noteDao)
    }

    internal val todolistRepository by lazy {
        TodolistRepository(DAOs.todolistDao)
    }

    internal val todoRepository by lazy {
        TodoRepository(DAOs.todoDao)
    }

    internal val subTodoRepository by lazy {
        SubTodoRepository(DAOs.subTodoDao)
    }

}