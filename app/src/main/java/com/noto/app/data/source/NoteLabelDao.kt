package com.noto.app.data.source

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.noto.app.domain.model.NoteLabel
import com.noto.app.domain.source.LocalNoteLabelDataSource
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteLabelDao : LocalNoteLabelDataSource {

    @Query("SELECT * FROM noto_labels WHERE note_id = :noteId")
    override fun getNoteLabelsByNoteId(noteId: Long): Flow<List<NoteLabel>>

    @Insert
    override suspend fun createNoteLabel(noteLabel: NoteLabel)

    @Query("DELETE FROM noto_labels WHERE note_id = :noteId AND label_id = :labelId")
    override suspend fun deleteNoteLabel(noteId: Long, labelId: Long)

}