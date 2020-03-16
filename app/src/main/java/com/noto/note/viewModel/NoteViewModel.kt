package com.noto.note.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noto.note.model.Note
import com.noto.note.repository.NoteRepository
import kotlinx.coroutines.launch
import timber.log.Timber

internal class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    val note = MutableLiveData<Note>()

    internal fun saveNote() {
        viewModelScope.launch {
//            if ((note.value!!.noteTitle.isBlank() || note.value!!.noteBody.isBlank())) {
            if (note.value!!.noteId == 0L) {
                Timber.i("Insert Note")
                noteRepository.insertNote(note.value!!)
            } else {
                Timber.i("Update Note")
                noteRepository.updateNote(note.value!!)
//                }
            }
        }
    }

    internal fun getNoteById(notebookId: Long, noteId: Long) {
        viewModelScope.launch {
            if (noteId == 0L) {
                note.postValue(Note( notebookId = notebookId))
            } else {
                note.postValue(noteRepository.getNoteById(noteId))
            }
        }
    }

}

internal class NoteViewModelFactory(private val noteRepository: NoteRepository) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(noteRepository) as T
        }
        throw KotlinNullPointerException("Unknown ViewModel Class")
    }

}