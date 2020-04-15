package com.noto.todo.viewModel

import androidx.lifecycle.*
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

    val todo = MutableLiveData<Todo>()

    fun getTodos(todolistId: Long) {
        viewModelScope.launch {
            todos = todoRepository.getTodos(todolistId)
        }
    }

    fun deleteTodolist(todolistId: Long) {
        viewModelScope.launch {
            todolistRepository.deleteTodoListById(todolistId)
        }
    }

    fun updateTodolist(todolist: Todolist) {
        viewModelScope.launch {
            todolistRepository.updateTodoList(todolist)
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            todoRepository.updateTodo(todo)
        }
    }

    fun insertTodo() {
        viewModelScope.launch {
            todoRepository.insertTodo(todo.value!!)
        }
    }
}