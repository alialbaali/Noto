package com.noto.todo.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "todos")
data class Todo(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "todo_id")
    val todoId: Long = 0L,

    @ForeignKey(
        entity = Todolist::class,
        parentColumns = ["todolist_id"],
        childColumns = ["todo_id"],
        onDelete = ForeignKey.CASCADE
    )
    @ColumnInfo(name = "todolist_id")
    val todoListId: Long,

    @ColumnInfo(name = "todo_title")
    var todoTitle: String,

    @ColumnInfo(name = "todo_note")
    var todoNote: String

)