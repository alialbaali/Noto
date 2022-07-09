package com.noto.app.domain.repository

import com.noto.app.domain.model.NoteLabel
import kotlinx.coroutines.flow.Flow

interface NoteLabelRepository {

    fun getNoteLabelsByNoteId(noteId: Long): Flow<List<NoteLabel>>

    fun getNoteLabels(): Flow<List<NoteLabel>>

    suspend fun createNoteLabel(noteLabel: NoteLabel)

    suspend fun deleteNoteLabel(noteId: Long, labelId: Long)

    suspend fun clearNoteLabels()
}