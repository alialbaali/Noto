package com.noto.app.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_labels")
data class NoteLabel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "note_id")
    val noteId: Long,

    @ColumnInfo(name = "label_id")
    val labelId: Long
)

fun NoteLabel.toLabel(libraryId: Long, labelTitle: String, labelColor: NotoColor) = Label(labelId, libraryId, labelTitle, labelColor)

fun Label.toNoteLabel(notoId: Long) = NoteLabel(labelId = id, noteId = notoId)