package com.noto.app.domain.source

import com.noto.app.domain.model.FolderIdWithNotesCount
import com.noto.app.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface LocalNoteDataSource {

    fun getAllNotes(): Flow<List<Note>>

    fun getAllMainNotes(): Flow<List<Note>>

    fun getNotesByFolderId(folderId: Long): Flow<List<Note>>

    fun getArchivedNotesByFolderId(folderId: Long): Flow<List<Note>>

    fun getNoteById(noteId: Long): Flow<Note>

    fun getFoldersNotesCount(): Flow<List<FolderIdWithNotesCount>>

    suspend fun createNote(note: Note): Long

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun clearNotes()
}