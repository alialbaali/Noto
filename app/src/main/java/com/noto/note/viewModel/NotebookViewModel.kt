package com.noto.note.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noto.note.model.Note
import com.noto.note.model.Notebook
import com.noto.note.repository.NoteRepository
import com.noto.note.repository.NotebookRepository
import kotlinx.coroutines.launch

internal class NotebookViewModel(
    private val notebookRepository: NotebookRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    internal val notes = MutableLiveData<List<Note>>()

    val notebook = MutableLiveData<Notebook>()

    internal fun getNotes(notebookId: Long) {
        viewModelScope.launch {
            notes.postValue(noteRepository.getNotes(notebookId))
        }
    }

    internal fun getNotebookById(notebookId: Long): Notebook {
        var notebook = Notebook()
        viewModelScope.launch {
            notebook = notebookRepository.getNotebookById(notebookId)
        }
        return notebook
    }

    internal fun updateNotebook() {
        viewModelScope.launch {
            notebookRepository.updateNotebook(notebook.value!!)
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