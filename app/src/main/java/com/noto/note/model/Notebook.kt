package com.noto.note.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noto.database.NotoColor

@Entity(tableName = "notebooks")
data class Notebook(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notebook_id")
    val notebookId: Long = 0L,

    @ColumnInfo(name = "notebook_title")
    var notebookTitle: String = "",

    @ColumnInfo(name = "noto_color")
    var notoColor: NotoColor = NotoColor.GRAY

)