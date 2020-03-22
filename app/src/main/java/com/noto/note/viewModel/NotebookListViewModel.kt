package com.noto.note.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noto.note.model.Notebook
import com.noto.note.repository.NotebookRepository
import kotlinx.coroutines.launch

internal class NotebookListViewModel(private val notebookRepository: NotebookRepository) :
    ViewModel() {

    lateinit var notebooks: LiveData<List<Notebook>>

    init {
        viewModelScope.launch {
            notebooks = notebookRepository.getNotebooks()
        }
    }

    internal fun saveNotebook(notebook: Notebook) {
        viewModelScope.launch {
            if (notebooks.value?.any {
                    it.notebookId == notebook.notebookId
                }!!) {
                notebookRepository.updateNotebook(notebook)
            } else {
                notebookRepository.insertNotebook(notebook)
            }
        }
    }
}

internal class NotebookListViewModelFactory(private val notebookDao: NotebookRepository) :
    ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotebookListViewModel::class.java)) {
            return NotebookListViewModel(notebookDao) as T
        }
        throw KotlinNullPointerException("Unknown ViewModel Class")
    }

}