package com.noto.todo.repository

import androidx.lifecycle.LiveData
import com.noto.database.TodoDao
import com.noto.todo.model.Todo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TodoRepository(private val todoDao: TodoDao) {

    suspend fun getTodos(todoListId: Long): LiveData<List<Todo>> {
        return withContext(Dispatchers.Main) {
            todoDao.getTodos(todoListId)
        }
    }

    suspend fun getTodoById(todoId: Long): Todo {
        return withContext(Dispatchers.IO) {
            todoDao.getTodoById(todoId)
        }
    }

    suspend fun insertTodo(todo: Todo) {
        withContext(Dispatchers.IO) {
            todoDao.insertTodo(todo)
        }
    }

    suspend fun updateTodo(todo: Todo) {
        withContext(Dispatchers.IO) {
            todoDao.updateTodo(todo)
        }
    }

    suspend fun deleteTodo(todo: Todo) {
        withContext(Dispatchers.IO) {
            todoDao.deleteTodo(todo)
        }
    }

}