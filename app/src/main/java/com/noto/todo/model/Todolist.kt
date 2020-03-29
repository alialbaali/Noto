package com.noto.todo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noto.database.NotoColor

@Entity(tableName = "todolists")
data class Todolist(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "todolist_Id")
    val todolistId: Long,

    @ColumnInfo(name = "todolist_title")
    var todolistTitle: String,

    @ColumnInfo(name = "noto_color")
    var notoColor: NotoColor = NotoColor.GRAY

)

