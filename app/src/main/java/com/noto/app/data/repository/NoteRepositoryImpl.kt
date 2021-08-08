package com.noto.app.data.repository

import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalNoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(private val dataSource: LocalNoteDataSource) : NoteRepository {

    override fun getNotesByLibraryId(libraryId: Long): Flow<List<Note>> = dataSource.getNotesByLibraryId(libraryId)

    override fun getArchivedNotesByLibraryId(libraryId: Long): Flow<List<Note>> = dataSource.getArchivedNotesByLibraryId(libraryId)

    override fun getNoteById(notoId: Long): Flow<Note> = dataSource.getNoteById(notoId)

    override suspend fun createNote(note: Note) = withContext(Dispatchers.IO) {
        dataSource.createNote(note.copy(title = note.title, body = note.body))
    }

    override suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        dataSource.updateNote(note.copy(title = note.title, body = note.body))
    }

    override suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        dataSource.deleteNote(note)
    }
}
