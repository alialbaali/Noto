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
        dataSource.createNote(note.copy(title = note.title, body = note.body))
    }

    override suspend fun updateNote(note: Note) = withContext(dispatcher) {
        dataSource.updateNote(note.copy(title = note.title, body = note.body))
    }

    override suspend fun deleteNote(note: Note) = withContext(dispatcher) {
        dataSource.deleteNote(note)
    }

    override suspend fun countLibraryNotes(libraryId: Long): Int = withContext(dispatcher) {
        dataSource.countLibraryNotes(libraryId)
    }
}
