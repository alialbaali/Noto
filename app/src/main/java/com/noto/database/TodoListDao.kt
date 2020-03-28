package com.noto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.noto.todo.model.TodoList

@Dao
interface TodoListDao {

    @Query("SELECT * FROM todo_lists ORDER BY todo_list_Id DESC")
    fun getTodoLists(): LiveData<List<TodoList>>

    @Query("SELECT * FROM todo_lists WHERE todo_list_Id = :todoListId LIMIT 1")
    fun getTodoListById(todoListId: Long): TodoList

    @Insert
    fun insertNotebook(todoList: TodoList)

    @Update
    fun updateNotebook(todoList: TodoList)

    @Transaction
    @Query("DELETE FROM todo_lists WHERE todo_list_Id = :todoListId")
    fun deleteNotebook(todoListId: Long)

}