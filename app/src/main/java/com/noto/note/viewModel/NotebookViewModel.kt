package com.noto.note.viewModel

import androidx.lifecycle.*
import com.noto.note.model.Note
import com.noto.note.model.Notebook
import com.noto.note.repository.NoteRepository
import com.noto.note.repository.NotebookRepository
import kotlinx.coroutines.launch

class NotebookViewModel(
    private val notebookRepository: NotebookRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _notes = MutableLiveData<List<Note>>()
    val notes: LiveData<List<Note>>
        get() = _notes

    internal fun getNotes(notebookId: Long) {
        viewModelScope.launch {
            _notes.postValue(noteRepository.getNotes(notebookId))
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