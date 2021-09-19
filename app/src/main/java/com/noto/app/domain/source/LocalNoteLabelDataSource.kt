package com.noto.app.domain.source

import com.noto.app.domain.model.NoteLabel
import kotlinx.coroutines.flow.Flow

interface LocalNoteLabelDataSource {

    fun getNoteLabelsByNoteId(noteId: Long): Flow<List<NoteLabel>>

    suspend fun createNoteLabel(noteLabel: NoteLabel)

    suspend fun deleteNoteLabel(noteId: Long, labelId: Long)

}