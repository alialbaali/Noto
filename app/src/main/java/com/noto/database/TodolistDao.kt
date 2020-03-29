package com.noto.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.noto.todo.model.Todolist

@Dao
interface TodolistDao {

    @Query("SELECT * FROM todolists ORDER BY todolist_Id DESC")
    fun getTodoLists(): LiveData<List<Todolist>>

    @Query("SELECT * FROM todolists WHERE todolist_Id = :todolistId LIMIT 1")
    fun getTodoListById(todolistId: Long): Todolist

    @Insert
    fun insertTodoList(todolist: Todolist)

    @Update
    fun updateTodoList(todolist: Todolist)

    @Transaction
    @Query("DELETE FROM todolists WHERE todolist_Id = :todolistId")
    fun deleteTodoList(todolistId: Long)

}