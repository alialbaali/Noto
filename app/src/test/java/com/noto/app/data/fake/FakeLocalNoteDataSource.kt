package com.noto.app.data.fake

import com.noto.app.domain.model.Note
import com.noto.app.domain.source.LocalNoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class FakeLocalNoteDataSource : LocalNoteDataSource {
    private val notes = MutableStateFlow<MutableList<Note>>(mutableListOf())

    override fun getNotesByLibraryId(libraryId: Long): Flow<List<Note>> = notes
        .map { it.filter { it.libraryId == libraryId && !it.isArchived } }

    override fun getArchivedNotesByLibraryId(libraryId: Long): Flow<List<Note>> = notes
        .map { it.filter { it.libraryId == libraryId && it.isArchived } }

    override fun getNoteById(noteId: Long): Flow<Note> = notes
        .map { it.first { it.id == noteId } }

    override suspend fun createNote(note: Note) {
        notes.value.add(note)
    }

    override suspend fun updateNote(note: Note) {
        val noteIndex = notes.value.indexOfFirst { it.id == note.id }
        notes.value[noteIndex] = note
    }

    override suspend fun deleteNote(note: Note) {
        notes.value.remove(note)
    }

    override suspend fun countNotesByLibraryId(libraryId: Long): Int = notes
        .map { it.count { it.libraryId == libraryId && !it.isArchived } }
        .first()

    override suspend fun clearNotes() = notes.value.clear()
}