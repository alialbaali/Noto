package com.noto.domain.repository

import com.noto.domain.model.Label
import com.noto.domain.model.Note
import com.noto.domain.model.NoteWithLabels
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getNotesByLibraryId(libraryId: Long): Flow<List<Note>>

    fun getArchivedNotesByLibraryId(libraryId: Long): Flow<List<Note>>

    fun getNoteById(notoId: Long): Flow<Note>

    suspend fun createNote(note: Note)

    suspend fun updateNote(note: Note)

    suspend fun deleteNote(note: Note)

    suspend fun getNoteWithLabels(notoId: Long): Flow<Result<NoteWithLabels>>

    suspend fun createNoteWithLabels(note: Note, labels: Set<Label>)

    suspend fun updateNoteWithLabels(note: Note, labels: Set<Label>)

    suspend fun deleteNoteWithLabels(notoId: Long)

}