package com.noto.todo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noto.database.NotoColor
import com.noto.database.NotoIcon
import com.noto.database.SortMethod
import com.noto.database.SortType
import java.util.*

@Entity(tableName = "todolists")
data class Todolist(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "todolist_Id")
    val todolistId: Long = 0L,

    @ColumnInfo(name = "todolist_title")
    var todolistTitle: String = "",

    @ColumnInfo(name = "todolist_position")
    var todolistPosition: Int,

    @ColumnInfo(name = "todolist_creation_date")
    val todolistCreationDate: Date = Date(),

    @ColumnInfo(name = "todolist_modification_date")
    val todolistModificationDate: Date = todolistCreationDate,

    @ColumnInfo(name = "noto_color")
    var notoColor: NotoColor = NotoColor.GRAY,

    @ColumnInfo(name = "noto_icon")
    var notoIcon: NotoIcon = NotoIcon.LIST,

    @ColumnInfo(name = "todolist_sort_type")
    var todolistSortType: SortType = SortType.DESC,

    @ColumnInfo(name = "todolist_sort_method")
    var todolistSortMethod: SortMethod = SortMethod.CreationDate
)

