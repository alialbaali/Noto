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
            if ((note.value?.noteTitle?.isNotBlank() == true || note.value?.noteBody?.isNotBlank() == true)) {
                Timber.i("Not Blank")
                if (note.value?.noteId == 0L) {
                Timber.i("Inserted")
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
                note.postValue(Note(notebookId = notebookId, notePosition = noteRepository.getNotes(notebookId).size))
            } else {
                note.postValue(noteRepository.getNoteById(noteId))
            }
        }
    }


    internal fun deleteNote() {
        viewModelScope.launch {
            if (note.value?.noteId != 0L) {
                noteRepository.deleteNote(note.value!!)
            } else {
                note.value = null
            }
        }
    }

}