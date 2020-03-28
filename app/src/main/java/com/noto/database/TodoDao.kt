package com.noto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.noto.todo.model.Todo

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE todo_id= :todoId")
    fun getNotes(todoId: Long): LiveData<List<Todo>>

    @Query("SELECT * FROM todos WHERE todo_id = :todoId")
    fun getNoteById(todoId: Long): Todo

    @Insert
    fun insertNote(todo: Todo)

    @Update
    fun updateNote(todo: Todo)

    @Delete
    fun deleteNote(todo: Todo)
}