package com.noto.note.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noto.database.NotoColor
import com.noto.database.NotoIcon
import com.noto.database.SortMethod
import com.noto.database.SortType
import java.util.*

@Entity(tableName = "notebooks")
data class Notebook(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "notebook_id")
    val notebookId: Long = 0L,

    @ColumnInfo(name = "notebook_title")
    var notebookTitle: String = "",

    @ColumnInfo(name = "notebook_position")
    var notebookPosition: Int,

    @ColumnInfo(name = "notebook_creation_date")
    val notebookCreationDate: Date = Date(),

    @ColumnInfo(name = "notebook_modification_date")
    val notebookModificationDate: Date = notebookCreationDate,

    @ColumnInfo(name = "noto_color")
    var notoColor: NotoColor = NotoColor.GRAY,

    @ColumnInfo(name = "noto_icon")
    var notoIcon: NotoIcon = NotoIcon.NOTEBOOK,

    @ColumnInfo(name = "notebook_sort_type")
    var notebookSortType: SortType = SortType.DESC,

    @ColumnInfo(name = "notebook_sort_method")
    var notebookSortMethod: SortMethod = SortMethod.CreationDate

)