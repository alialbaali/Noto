package com.noto.todo.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.database.SortMethod
import com.noto.database.SortType
import com.noto.todo.model.Todolist
import com.noto.todo.repository.TodolistRepository
import com.noto.util.sortTodolistAsc
import com.noto.util.sortTodolistDesc
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TodolistListViewModel(private val todolistRepository: TodolistRepository) : ViewModel() {

    private val _todolists = MutableLiveData<List<Todolist>>()
    val todolists: LiveData<List<Todolist>> = _todolists

    private val _sortType = MutableLiveData<SortType>()
    val sortType: LiveData<SortType> = _sortType

    private val _sortMethod = MutableLiveData<SortMethod>()
    val sortMethod: LiveData<SortMethod> = _sortMethod


    init {
        getTodolists()
        getSortType()
        getSortMethod()
    }

    private fun getTodolists() {
        viewModelScope.launch {
            _todolists.postValue(sort(todolistRepository.getTodoLists()))
        }
    }

    private fun sort(list: List<Todolist>): List<Todolist> {
        return if (_sortType.value == SortType.ASC) {
            list.sortTodolistAsc(_sortMethod.value ?: SortMethod.Custom)
        } else {
            list.sortTodolistDesc(_sortMethod.value ?: SortMethod.Custom)
        }
    }

    fun saveTodolist(todolist: Todolist) {
        viewModelScope.launch {
            if (todolists.value?.any { it.todolistId == todolist.todolistId }!!) {

                todolistRepository.updateTodoList(todolist)

            } else {

                todolistRepository.insertTodoList(todolist)

                _todolists.postValue(todolistRepository.getTodoLists())
            }
        }
    }

    internal fun deleteTodolist(todolistId: Long) {
        viewModelScope.launch {
            todolistRepository.deleteTodoListById(todolistId)
        }
    }

    fun countTodos(todolistId: Long): Int {
        return runBlocking {
            todolistRepository.countTodos(todolistId)
        }
    }

    fun updateSortType() {
        if (_sortType.value == SortType.ASC) {
            todolistRepository.updateSortType(SortType.DESC)
        } else {
            todolistRepository.updateSortType(SortType.ASC)
        }
        getSortType()
        getTodolists()
    }

    fun updateSortMethod(sortMethod: SortMethod) {
        todolistRepository.updateSortMethod(sortMethod)
        getSortMethod()

        if (_sortMethod.value != SortMethod.Custom) {
            getTodolists()
        }
    }

    private fun getSortType() {
        _sortType.value = todolistRepository.getSortType()
    }

    private fun getSortMethod() {
        _sortMethod.value = todolistRepository.getSortMethod()
    }

    fun updateTodolists(todolists : List<Todolist>){
        viewModelScope.launch {
            todolistRepository.updateTodolists(todolists)
        }
    }

}