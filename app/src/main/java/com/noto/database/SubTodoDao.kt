package com.noto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.noto.todo.model.SubTodo

interface SubTodoDao {

    @Insert
    fun insertSubTodo(subTodo: SubTodo)

    @Update
    fun updateSubTodo(subTodo: SubTodo)

    @Delete
    fun deleteSubTodo(subTodo: SubTodo)

    @Query("SELECT * FROM sub_todos WHERE todo_id = :todoId")
    fun getSubTodos(todoId: Long): LiveData<List<SubTodo>>

    @Query("SELECT * FROM sub_todos WHERE sub_todo_id = :subTodoId ")
    fun getSubTodoById(subTodoId: Long): SubTodo

}