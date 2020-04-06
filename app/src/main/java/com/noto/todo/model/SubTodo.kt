package com.noto.todo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "sub_todos")
data class SubTodo(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "sub_todo_id")
    val subTodoId: Long = 0L,

    @ForeignKey(
        entity = Todo::class,
        parentColumns = ["todo_id"],
        childColumns = ["sub_todo_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "todo_id")
    val todoId: Long,

    @ColumnInfo(name = "sub_todo_title")
    var subTodoTitle: String = "",

    @ColumnInfo(name = "sub_todo_is_checked")
    var subTodoIsChecked: Boolean = false
)