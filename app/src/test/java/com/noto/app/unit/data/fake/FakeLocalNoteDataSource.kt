package com.noto.app.unit.data.fake

import com.noto.app.domain.model.Note
import com.noto.app.domain.source.LocalNoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalNoteDataSource : LocalNoteDataSource {

    private val notes = mutableListOf<Note>()

    override fun getNotesByLibraryId(libraryId: Long): Flow<List<Note>> = flowOf(notes.filter { it.libraryId == libraryId && !it.isArchived })

    override fun getArchivedNotesByLibraryId(libraryId: Long): Flow<List<Note>> = flowOf(notes.filter { it.libraryId == libraryId && it.isArchived })

    override fun getNoteById(noteId: Long): Flow<Note> = flowOf(notes.first { it.id == noteId })

    override suspend fun createNote(note: Note) {
        notes.add(note)
    }

    override suspend fun updateNote(note: Note) {
        val noteIndex = notes.indexOfFirst { it.id == note.id }
        notes[noteIndex] = note
    }

    override suspend fun deleteNote(note: Note) {
        notes.remove(note)
    }

    override suspend fun countLibraryNotes(libraryId: Long): Int = notes.count { it.libraryId == libraryId && !it.isArchived }
}