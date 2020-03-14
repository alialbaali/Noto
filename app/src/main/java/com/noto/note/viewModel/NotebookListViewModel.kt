package com.noto.note.viewModel

import androidx.lifecycle.*
import com.noto.note.model.Notebook
import com.noto.note.repository.NotebookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.launch

internal class NotebookListViewModel(private val notebookRepository: NotebookRepository) :
    ViewModel() {

    lateinit var notebooks : LiveData<List<Notebook>>

    val notebook = MutableLiveData<Notebook>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            notebooks = notebookRepository.getNotebooks()
        }
    }

    internal fun insertNote() {
        viewModelScope.launch {
            notebookRepository.insertNotebook(notebook.value!!)
        }
    }
}

internal class NotebookListViewModelFactory(private val notebookRepository: NotebookRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotebookListViewModel::class.java)) {
            return NotebookListViewModel(notebookRepository) as T
        }
        throw KotlinNullPointerException("Unknown ViewModel Class")
    }

}