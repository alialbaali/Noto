package com.noto.note.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noto.note.repository.NotebookRepository

internal class NotebookListViewModel(private val notebookRepository: NotebookRepository) :
    ViewModel() {

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