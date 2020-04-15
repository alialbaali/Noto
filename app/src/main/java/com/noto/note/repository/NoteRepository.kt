package com.noto.note.repository

import androidx.lifecycle.LiveData
import com.noto.database.NoteDao
import com.noto.note.model.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(private val noteDao: NoteDao) {

    suspend fun getNotes(notebookId: Long): List<Note> {
        return withContext(Dispatchers.IO) {
            noteDao.getNotes(notebookId)
        }
    }

    suspend fun getNoteById(noteId: Long): Note {
        return withContext(Dispatchers.IO) {
            noteDao.getNoteById(noteId)
        }
    }

    suspend fun insertNote(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.insertNote(note)
        }
    }

    suspend fun updateNote(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.updateNote(note)
        }
    }

    suspend fun deleteNote(note: Note) {
        withContext(Dispatchers.IO) {
            noteDao.deleteNote(note)
        }
    }
}
