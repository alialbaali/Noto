package com.noto.todo.repository

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.noto.database.TodolistDao
import com.noto.todo.model.Todolist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodolistRepository(private val sharedPreferences: SharedPreferences, private val todolistDao: TodolistDao) {

    suspend fun getTodoLists(): LiveData<List<Todolist>> {
        return withContext(Dispatchers.Main) {
            todolistDao.getTodoLists()
        }
    }

    suspend fun getTodoListById(todoListId: Long): Todolist {
        return withContext(Dispatchers.IO) {
            todolistDao.getTodoListById(todoListId)
        }
    }

    suspend fun insertTodoList(todolist: Todolist) {
        withContext(Dispatchers.IO) {
            todolistDao.insertTodoList(todolist)
        }
    }

    suspend fun updateTodoList(todolist: Todolist) {
        withContext(Dispatchers.IO) {
            todolistDao.updateTodoList(todolist)
        }
    }

    suspend fun deleteTodoListById(todoListId: Long) {
        withContext(Dispatchers.IO) {
            todolistDao.deleteTodoList(todoListId)
        }
    }


}