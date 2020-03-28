package com.noto.todo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.noto.database.NotoColor

@Entity(tableName = "todo_lists")
data class TodoList(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "todo_list_Id")
    val todoListId: Long,

    @ColumnInfo(name = "todo_list_title")
    var todoListTitle: String,

    @ColumnInfo(name = "noto_color")
    var notoColor: NotoColor = NotoColor.GRAY


)

