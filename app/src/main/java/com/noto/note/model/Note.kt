package com.noto.note.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.noto.database.SortMethod
import com.noto.database.SortType
import java.util.*

@Entity(tableName = "notes")
data class Note(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_id")
    val noteId: Long = 0L,

    @ForeignKey(
        entity = Notebook::class,
        parentColumns = ["notebook_id"],
        childColumns = ["note_id"],
        onDelete = ForeignKey.CASCADE
    )
    val notebookId: Long,

    @ColumnInfo(name = "note_title")
    var noteTitle: String = "",

    @ColumnInfo(name = "note_body")
    var noteBody: String = "",

    @ColumnInfo(name = "note_position")
    var notePosition: Int,

    @ColumnInfo(name = "note_creation_date")
    val noteCreationDate: Date = Date(),

    @ColumnInfo(name = "note_modification_date")
    val noteModificationDate: Date = noteCreationDate
)