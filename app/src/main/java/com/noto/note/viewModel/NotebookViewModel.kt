package com.noto.note.viewModel

import androidx.lifecycle.*
import com.noto.note.model.Note
import com.noto.note.model.Notebook
import com.noto.note.repository.NoteRepository
import com.noto.note.repository.NotebookRepository
import kotlinx.coroutines.launch

internal class NotebookViewModel(
    private val notebookRepository: NotebookRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    lateinit var notes: LiveData<List<Note>>

    internal fun getNotes(notebookId: Long) {
        viewModelScope.launch {
            notes = noteRepository.getNotes(notebookId)
        }
    }

    internal fun updateNotebook(notebook: Notebook) {
        viewModelScope.launch {
            notebookRepository.updateNotebook(notebook)
        }
    }

    internal fun deleteNotebook(notebookId: Long) {
        viewModelScope.launch {
            notebookRepository.deleteNotebook(notebookId)
        }
    }
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