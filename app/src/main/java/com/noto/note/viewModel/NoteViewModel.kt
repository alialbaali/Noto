package com.noto.note.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.noto.note.model.Note
import com.noto.note.repository.NoteRepository
import kotlinx.coroutines.launch

internal class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {

    val note = MutableLiveData<Note>()

    internal fun saveNote() {
        viewModelScope.launch {
            if ((note.value!!.noteTitle.isNotBlank() || note.value!!.noteBody.isNotBlank())) {

                if (note.value!!.noteId == 0L) {
                    noteRepository.insertNote(note.value!!)
                } else {
                    noteRepository.updateNote(note.value!!)
                }

            }
        }
    }

    internal fun getNoteById(notebookId: Long, noteId: Long) {
        viewModelScope.launch {
            if (noteId == 0L) {
                note.postValue(Note(notebookId = notebookId))
            } else {
                note.postValue(noteRepository.getNoteById(noteId))
            }
        }
    }


    internal fun deleteNote() {
        viewModelScope.launch {
            if (note.value!!.noteId != 0L) {
                noteRepository.deleteNote(note.value!!)
            } else {
                note.value!!.noteTitle = ""
                note.value!!.noteBody = ""
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