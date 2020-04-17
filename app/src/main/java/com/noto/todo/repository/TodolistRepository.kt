package com.noto.todo.repository

import android.content.SharedPreferences
import com.noto.database.SortMethod
import com.noto.database.SortType
import com.noto.database.TodolistDao
import com.noto.todo.model.Todolist
import com.noto.util.getValue
import com.noto.util.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TODOLIST_LIST_SORT_TYPE_KEY = "todolist_list_sort_type"
private const val TODOLIST_LIST_SORT_METHOD_KEY = "todolist_list_sort_method"

class TodolistRepository(private val sharedPreferences: SharedPreferences, private val todolistDao: TodolistDao) {

    suspend fun getTodoLists(): List<Todolist> {
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

    suspend fun countTodos(todoListId: Long): Int {
        return withContext(Dispatchers.IO) {
            todolistDao.countTodos(todoListId)
        }
    }

    fun updateSortType(sortType: SortType) {
        sharedPreferences.setValue(TODOLIST_LIST_SORT_TYPE_KEY, sortType.name)
    }

    fun updateSortMethod(sortMethod: SortMethod) {
        sharedPreferences.setValue(TODOLIST_LIST_SORT_METHOD_KEY, sortMethod.name)
    }

    fun getSortType(): SortType {
        var value = sharedPreferences.getValue(TODOLIST_LIST_SORT_TYPE_KEY) as String?

        if (value.isNullOrBlank()) {
            updateSortType(SortType.ASC)
            value = SortType.ASC.name
        }

        return SortType.valueOf(value)
    }

    fun getSortMethod(): SortMethod {
        var value = sharedPreferences.getValue(TODOLIST_LIST_SORT_METHOD_KEY) as String?

        if (value.isNullOrBlank()) {
            updateSortMethod(SortMethod.CreationDate)
            value = SortMethod.CreationDate.name
        }

        return SortMethod.valueOf(value)
    }

    suspend fun updateTodolists(todolists: List<Todolist>) {
        withContext(Dispatchers.IO) {
            todolistDao.updateTodolists(todolists)
        }
    }

}