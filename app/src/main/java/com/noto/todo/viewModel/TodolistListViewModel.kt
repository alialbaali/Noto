package com.noto.todo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noto.todo.model.Todolist
import com.noto.todo.repository.TodolistRepository
import kotlinx.coroutines.launch

internal class TodolistListViewModel(private val todolistRepository: TodolistRepository) :
    ViewModel() {

    lateinit var todolists: LiveData<List<Todolist>>

    init {
        viewModelScope.launch {
            todolists = todolistRepository.getTodoLists()
        }
    }

    internal fun saveTodolist(todolist: Todolist) {
        viewModelScope.launch {
            if (todolists.value?.any {
                    it.todolistId == todolist.todolistId
                }!!) {
                todolistRepository.updateTodoList(todolist)
            } else {
                todolistRepository.insertTodoList(todolist)
            }
        }
    }

    internal fun deleteTodolist(todolistId: Long) {
        viewModelScope.launch {
            todolistRepository.deleteTodoListById(todolistId)
        }
    }

}