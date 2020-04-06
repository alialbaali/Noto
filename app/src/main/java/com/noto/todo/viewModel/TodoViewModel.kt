package com.noto.todo.viewModel

import android.content.res.ColorStateList
import androidx.lifecycle.*
import com.noto.database.NotoColor
import com.noto.todo.model.SubTodo
import com.noto.todo.model.Todo
import com.noto.todo.repository.SubTodoRepository
import com.noto.todo.repository.TodoRepository
import kotlinx.coroutines.launch

class TodoViewModel(
    private val todoRepository: TodoRepository,
    private val subTodoRepository: SubTodoRepository
) : ViewModel() {
    val todo = MutableLiveData<Todo>()

    lateinit var subTodos: LiveData<List<SubTodo>>

    val subTodo = MutableLiveData<SubTodo>()

    val notoColor = MutableLiveData<NotoColor>()

    fun saveTodo() {
        viewModelScope.launch {
            if (todo.value!!.todoTitle.isNotBlank()) {
                if (todo.value!!.todoId == 0L) {
                    todoRepository.insertTodo(todo.value!!)
                } else {
                    todoRepository.updateTodo(todo.value!!)
                }
            }
        }
    }

    fun getTodoById(todolistId: Long, todoId: Long) {
        viewModelScope.launch {
            if (todoId == 0L) {
                todo.postValue(Todo(todoListId = todolistId))
            } else {
                todo.postValue(todoRepository.getTodoById(todoId))
            }
        }
    }

    fun getSubTodos(todoId: Long) {
        viewModelScope.launch {
            subTodos = subTodoRepository.getSubTodos(todoId)
        }
    }

    fun deleteSubTodo(subTodo: SubTodo) {
        viewModelScope.launch {
            subTodoRepository.deleteSubTodo(subTodo)
        }
    }

    fun saveSubTodo() {
        viewModelScope.launch {
            subTodoRepository.insertSubTodo(subTodo.value!!)
        }
    }

    fun updateSubTodos() {
        viewModelScope.launch {
            subTodoRepository.updateSubTodos(subTodos.value!!)
        }
    }
}

class TodoViewModelFactory(
    private val todoRepository: TodoRepository,
    private val subTodoRepository: SubTodoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            return TodoViewModel(todoRepository, subTodoRepository) as T
        }
        throw KotlinNullPointerException("Unknown ViewModel Class")
    }
}