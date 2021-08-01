package com.noto.app.data.repository

import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NoteWithLabels
import com.noto.app.domain.model.toNoteLabel
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.NoteLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NoteRepositoryImpl(private val dataSource: NoteLocalDataSource) : NoteRepository {

    override fun getNotesByLibraryId(libraryId: Long): Flow<List<Note>> = dataSource.getNotesByLibraryId(libraryId)

    override fun getArchivedNotesByLibraryId(libraryId: Long): Flow<List<Note>> = dataSource.getArchivedNotesByLibraryId(libraryId)

    override fun getNoteById(notoId: Long): Flow<Note> = dataSource.getNoteById(notoId)

    override suspend fun createNote(note: Note) = withContext(Dispatchers.IO) {
        dataSource.createNote(note.copy(title = note.title.trim(), body = note.body.trim()))
    }

    override suspend fun updateNote(note: Note) = withContext(Dispatchers.IO) {
        dataSource.updateNote(note.copy(title = note.title.trim(), body = note.body.trim()))
    }

    override suspend fun deleteNote(note: Note) = withContext(Dispatchers.IO) {
        dataSource.deleteNote(note)
    }

    override suspend fun getNoteWithLabels(notoId: Long): Flow<Result<NoteWithLabels>> = dataSource.getNoteWithLabels(notoId).map { Result.success(it) }

    override suspend fun createNoteWithLabels(note: Note, labels: Set<Label>) = withContext(Dispatchers.IO) {
        val notoLabels = labels.map { it.toNoteLabel(note.id) }.toSet()
        dataSource.createNoteWithLabels(note, notoLabels)
    }

    override suspend fun updateNoteWithLabels(note: Note, labels: Set<Label>) = withContext(Dispatchers.IO) {
        val notoLabels = labels.map { it.toNoteLabel(note.id) }.toSet()
        dataSource.updateNoteWithLabels(note, notoLabels)
    }

    override suspend fun deleteNoteWithLabels(notoId: Long) = withContext(Dispatchers.IO) {
        dataSource.deleteNoteWithLabels(notoId)
    }

}
