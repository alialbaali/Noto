package com.noto.note.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noto.note.model.Notebook
import com.noto.note.repository.NotebookRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class NotebookListViewModel(private val notebookRepository: NotebookRepository) :
    ViewModel() {

    internal val notebooks = MutableLiveData<List<Notebook>>()

    val notebook = MutableLiveData<Notebook>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            notebooks.postValue(notebookRepository.getNotebooks())
        }
    }

    internal fun saveNotebook() {
        viewModelScope.launch {
            if (notebooks.value?.any {
                    it.notebookId == notebook.value?.notebookId
                }!!) {
                notebookRepository.updateNotebook(notebook.value!!)
            } else {
                notebookRepository.insertNotebook(notebook.value!!)
            }
            notebooks.postValue(notebookRepository.getNotebooks())
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