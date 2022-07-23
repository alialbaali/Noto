package com.noto.app.data.repository

import com.noto.app.domain.model.FolderIdWithNotesCount
import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalNoteDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(
    private val dataSource: LocalNoteDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> = dataSource.getAllNotes().flowOn(dispatcher)

    override fun getAllMainNotes(): Flow<List<Note>> = dataSource.getAllMainNotes().flowOn(dispatcher)

    override fun getNotesByFolderId(folderId: Long): Flow<List<Note>> =
        dataSource.getNotesByFolderId(folderId).flowOn(dispatcher)

    override fun getArchivedNotesByFolderId(folderId: Long): Flow<List<Note>> =
        dataSource.getArchivedNotesByFolderId(folderId).flowOn(dispatcher)

    override fun getNoteById(noteId: Long): Flow<Note> = dataSource.getNoteById(noteId).flowOn(dispatcher)

    override fun getFolderNotesCount(): Flow<List<FolderIdWithNotesCount>> =
        dataSource.getFoldersNotesCount().flowOn(dispatcher)

    override suspend fun createNote(note: Note) = withContext(dispatcher) {
        val position = getNotePosition(note.folderId)
        dataSource.createNote(note.copy(position = position))
    }

    override suspend fun updateNote(note: Note) = withContext(dispatcher) {
        dataSource.updateNote(note)
    }

    override suspend fun deleteNote(note: Note) = withContext(dispatcher) {
        dataSource.deleteNote(note)
    }

    override suspend fun clearNotes() = withContext(dispatcher) {
        dataSource.clearNotes()
    }

    private suspend fun getNotePosition(folderId: Long) = withContext(dispatcher) {
        dataSource.getNotesByFolderId(folderId)
            .filterNotNull()
            .first()
            .count()
    }
}
