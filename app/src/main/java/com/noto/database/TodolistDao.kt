package com.noto.database

import androidx.room.*
import com.noto.todo.model.Todolist

@Dao
interface TodolistDao {

    @Query("SELECT * FROM todolists")
    suspend fun getTodoLists(): List<Todolist>

    @Query("SELECT * FROM todolists WHERE todolist_Id = :todolistId LIMIT 1")
    suspend fun getTodoListById(todolistId: Long): Todolist

    @Insert
    suspend fun insertTodoList(todolist: Todolist)

    @Update
    suspend fun updateTodoList(todolist: Todolist)

    @Transaction
    @Query("DELETE FROM todolists WHERE todolist_Id = :todolistId")
    suspend fun deleteTodoList(todolistId: Long)

    @Query("SELECT COUNT(*) FROM todos WHERE todolist_id = :todolistId AND is_checked = 0")
    suspend fun countTodos(todolistId: Long): Int

    @Update
    suspend fun updateTodolists(todolists: List<Todolist>)

}