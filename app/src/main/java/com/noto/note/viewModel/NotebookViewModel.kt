package com.noto.note.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noto.note.repository.NoteRepository
import com.noto.note.repository.NotebookRepository

internal class NotebookViewModel(
    private val notebookRepository: NotebookRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

}




internal class NotebookViewModelFactory(
    private val notebookRepository: NotebookRepository,
    private val noteRepository: NoteRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NotebookViewModel::class.java)) {
            return NotebookViewModel(notebookRepository, noteRepository) as T
        }
        throw KotlinNullPointerException("Unknown ViewModel Class")
    }
}