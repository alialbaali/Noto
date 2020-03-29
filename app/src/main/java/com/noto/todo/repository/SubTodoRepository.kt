package com.noto.todo.repository

import androidx.lifecycle.LiveData
import com.noto.database.SubTodoDao
import com.noto.todo.model.SubTodo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SubTodoRepository(private val subTodoDao: SubTodoDao) {

    suspend fun insertSubTodo(subTodo: SubTodo) {
        withContext(Dispatchers.IO) {
            subTodoDao.insertSubTodo(subTodo)
        }
    }

    suspend fun updateSubTodo(subTodo: SubTodo) {
        withContext(Dispatchers.IO) {
            subTodoDao.updateSubTodo(subTodo)
        }
    }

    suspend fun deleteSubTodo(subTodo: SubTodo) {
        withContext(Dispatchers.IO) {
            subTodoDao.deleteSubTodo(subTodo)
        }
    }

    suspend fun getSubTodos(todoId: Long): LiveData<List<SubTodo>> {
        return withContext(Dispatchers.Main) {
            subTodoDao.getSubTodos(todoId)
        }
    }

    suspend fun getSubTodoById(subTodoId: Long): SubTodo {
        return withContext(Dispatchers.IO) {
            subTodoDao.getSubTodoById(subTodoId)
        }
    }

}