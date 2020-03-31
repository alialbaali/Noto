package com.noto.todo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noto.todo.model.Todo
import com.noto.todo.model.Todolist
import com.noto.todo.repository.TodoRepository
import com.noto.todo.repository.TodolistRepository
import kotlinx.coroutines.launch

class TodolistViewModel(
    private val todolistRepository: TodolistRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {

    lateinit var todos: LiveData<List<Todo>>

    fun getTodos(todolistId: Long) {
        viewModelScope.launch {
            todos = todoRepository.getTodos(todolistId)
        }
    }

    fun deleteTodolist(todolistId: Long){
        viewModelScope.launch {
            todolistRepository.deleteTodoListById(todolistId)
        }
    }

    fun updateTodolist(todolist: Todolist){
        viewModelScope.launch {
            todolistRepository.updateTodoList(todolist)
        }
    }
}

class TodolistViewModelFactory(
    private val todolistRepository: TodolistRepository,
    private val todoRepository: TodoRepository
) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodolistViewModel::class.java)) {
            return TodolistViewModel(todolistRepository, todoRepository) as T
        }
        throw KotlinNullPointerException("Unknown ViewModel Class")
    }

}