package com.noto.data

import com.noto.domain.local.NotoLocalDataSource
import com.noto.domain.model.Label
import com.noto.domain.model.Note
import com.noto.domain.model.NotoWithLabels
import com.noto.domain.model.toNotoLabel
import com.noto.domain.repository.NotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NotoRepositoryImpl(private val dataSource: NotoLocalDataSource) : NotoRepository {

    override fun getNotosByLibraryId(libraryId: Long): Flow<List<Note>> = dataSource.getNotosByLibraryId(libraryId)

    override fun getArchivedNotosByLibraryId(libraryId: Long): Flow<List<Note>> = dataSource.getArchivedNotosByLibraryId(libraryId)

    override fun getNotoById(notoId: Long): Flow<Note> = dataSource.getNotoById(notoId)

    override suspend fun createNoto(note: Note) = withContext(Dispatchers.IO) {
        dataSource.createNoto(note.copy(title = note.title.trim(), body = note.body.trim()))
    }

    override suspend fun updateNoto(note: Note) = withContext(Dispatchers.IO) {
        dataSource.updateNoto(note.copy(title = note.title.trim(), body = note.body.trim()))
    }

    override suspend fun deleteNoto(note: Note) = withContext(Dispatchers.IO) {
        dataSource.deleteNoto(note)
    }

    override suspend fun getNotoWithLabels(notoId: Long): Flow<Result<NotoWithLabels>> = dataSource.getNotoWithLabels(notoId).map { Result.success(it) }

    override suspend fun createNotoWithLabels(note: Note, labels: Set<Label>) = withContext(Dispatchers.IO) {
        val notoLabels = labels.map { it.toNotoLabel(note.id) }.toSet()
        dataSource.createNotoWithLabels(note, notoLabels)
    }

    override suspend fun updateNotoWithLabels(note: Note, labels: Set<Label>) = withContext(Dispatchers.IO) {
        val notoLabels = labels.map { it.toNotoLabel(note.id) }.toSet()
        dataSource.updateNotoWithLabels(note, notoLabels)
    }

    override suspend fun deleteNotoWithLabels(notoId: Long) = withContext(Dispatchers.IO) {
        dataSource.deleteNotoWithLabels(notoId)
    }

}
