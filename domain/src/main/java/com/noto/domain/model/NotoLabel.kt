package com.noto.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "noto_labels")
data class NoteLabel(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "noto_label_id")
    val NoteLabelId: Long = 0,

    @ColumnInfo(name = "id")
    val notoId: Long,

    @ColumnInfo(name = "label_id")
    val labelId: Long

)

fun NoteLabel.toLabel(labelTitle: String, labelColor: NotoColor) = Label(labelId, labelTitle, labelColor)

fun Label.toNoteLabel(notoId: Long) = NoteLabel(labelId = labelId, notoId =  notoId)