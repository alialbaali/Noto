package com.noto.app.data.repository

import com.noto.app.domain.model.NoteLabel
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.source.LocalNoteLabelDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NoteLabelRepositoryImpl(
    private val source: LocalNoteLabelDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoteLabelRepository {

    override fun getNoteLabelsByNoteId(noteId: Long): Flow<List<NoteLabel>> = source.getNoteLabelsByNoteId(noteId)

    override fun getNoteLabels(): Flow<List<NoteLabel>> = source.getNoteLabels()

    override suspend fun createNoteLabel(noteLabel: NoteLabel) = withContext(dispatcher) {
        source.createNoteLabel(noteLabel)
    }

    override suspend fun deleteNoteLabel(noteId: Long, labelId: Long) = withContext(dispatcher) {
        source.deleteNoteLabel(noteId, labelId)
    }
}