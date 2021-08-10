package com.noto.app.data.repository

import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalNoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(
    private val dataSource: LocalNoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : NoteRepository {

    override fun getNotesByLibraryId(libraryId: Long): Flow<List<Note>> = dataSource.getNotesByLibraryId(libraryId)

    override fun getArchivedNotesByLibraryId(libraryId: Long): Flow<List<Note>> = dataSource.getArchivedNotesByLibraryId(libraryId)

    override fun getNoteById(noteId: Long): Flow<Note> = dataSource.getNoteById(noteId)

    override suspend fun createNote(note: Note) = withContext(dispatcher) {
        dataSource.createNote(note)
    }

    override suspend fun updateNote(note: Note) = withContext(dispatcher) {
        dataSource.updateNote(note)
    }

    override suspend fun deleteNote(note: Note) = withContext(dispatcher) {
        dataSource.deleteNote(note)
    }

    override suspend fun countNotesByLibraryId(libraryId: Long): Int = withContext(dispatcher) {
        dataSource.countNotesByLibraryId(libraryId)
    }

    override suspend fun clearNotes() = dataSource.clearNotes()
}
