package com.noto.todo.repository

import androidx.lifecycle.LiveData
import com.noto.database.TodoListDao
import com.noto.todo.model.TodoList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoListRepository(private val todoListDao: TodoListDao) {

    suspend fun getTodoLists(): LiveData<List<TodoList>> {
        return withContext(Dispatchers.Main) {
            todoListDao.getTodoLists()
        }
    }

    suspend fun getTodoListById(todoListId: Long): TodoList {
        return withContext(Dispatchers.IO) {
            todoListDao.getTodoListById(todoListId)
        }
    }

    suspend fun insertTodoList(todoList: TodoList) {
        withContext(Dispatchers.IO) {
            todoListDao.insertTodoList(todoList)
        }
    }

    suspend fun updateTodoList(todoList: TodoList) {
        withContext(Dispatchers.IO) {
            todoListDao.updateTodoList(todoList)
        }
    }

    suspend fun deleteTodoListById(todoListId: Long) {
        withContext(Dispatchers.IO) {
            todoListDao.deleteTodoList(todoListId)
        }
    }


}