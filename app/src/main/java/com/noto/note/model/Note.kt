package com.noto.note.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    val noteId: Long,

    @ForeignKey(
        entity = Notebook::class,
        parentColumns = ["notebook_id"],
        childColumns = ["note_id"],
        onDelete = ForeignKey.CASCADE
    )
    val notebookId: Long,

    @ColumnInfo(name = "note_title")
    val noteTitle: String = "",

    @ColumnInfo(name = "note_body")
    val noteBody: String = ""
)