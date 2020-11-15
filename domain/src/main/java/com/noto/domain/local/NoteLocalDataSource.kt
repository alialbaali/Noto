package com.noto.domain.local

import com.noto.domain.model.Note
import com.noto.domain.model.NoteLabel
import com.noto.domain.model.NoteWithLabels
import kotlinx.coroutines.flow.Flow

interface NoteLocalDataSource {

    fun getNotesByLibraryId(libraryId: Long): Flow<List<Note>>

    fun getArchivedNotesByLibraryId(libraryId: Long): Flow<List<Note>>

    fun getNoteById(noteId: Long): Flow<Note>

    suspend fun createNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)

    fun getNoteWithLabels(notoId: Long): Flow<NoteWithLabels>

    fun createNoteWithLabels(note: Note, notoLabels: Set<NoteLabel>)

    fun updateNoteWithLabels(note: Note, notoLabels: Set<NoteLabel>)

    fun deleteNoteWithLabels(notoId: Long)

}