package com.noto.note.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noto.note.repository.NoteRepository

internal class NoteViewModel(private val noteRepository: NoteRepository) : ViewModel() {

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