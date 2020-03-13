package com.noto.network

import com.noto.database.NoteDao
import com.noto.database.NotebookDao
import com.noto.note.repository.NoteRepository
import com.noto.note.repository.NotebookRepository

internal object DAOs {
    internal lateinit var notebookDao: NotebookDao
    internal lateinit var noteDao: NoteDao
}

internal object Repos {
    internal val notebookRepository by lazy {
        NotebookRepository(DAOs.notebookDao)
    }
    internal val noteRepository by lazy {
        NoteRepository(DAOs.noteDao)
    }
}