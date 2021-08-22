package com.noto.app.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.util.isValid
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class NoteViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val libraryId: Long,
    private val noteId: Long,
    private val body: String?,
) : ViewModel() {

    private val mutableState = MutableStateFlow(State(Library(position = 0), Note(noteId, libraryId, position = 0, body = body ?: "")))
    val state get() = mutableState.asStateFlow()

    init {
        combine(
            libraryRepository.getLibraryById(libraryId)
                .filterNotNull(),
            noteRepository.getNoteById(noteId)
                .onStart { emit(Note(noteId, libraryId, position = 0, body = body ?: "")) }
                .filterNotNull(),
        ) { library, note -> mutableState.value = State(library, note) }
            .launchIn(viewModelScope)
    }

    fun createOrUpdateNote(title: String, body: String) = viewModelScope.launch {
        val note = state.value.note.copy(
            title = title.trim(),
            body = body.trim(),
        )
        if (note.isValid())
            if (noteId == 0L)
                noteRepository.createNote(note)
            else
                noteRepository.updateNote(note)
        else
            noteRepository.deleteNote(note)
    }

    fun deleteNote() = viewModelScope.launch {
        noteRepository.deleteNote(state.value.note)
    }

    fun toggleNoteIsArchived() = viewModelScope.launch {
        noteRepository.updateNote(state.value.note.copy(isArchived = !state.value.note.isArchived))
    }

    fun toggleNoteIsStarred() = viewModelScope.launch {
        noteRepository.updateNote(state.value.note.copy(isStarred = !state.value.note.isStarred))
    }

    fun setNoteReminder(instant: Instant?) = viewModelScope.launch {
        noteRepository.updateNote(state.value.note.copy(reminderDate = instant))
    }

    fun moveNote(libraryId: Long) = viewModelScope.launch {
        noteRepository.updateNote(state.value.note.copy(libraryId = libraryId))
    }

    fun copyNote(libraryId: Long) = viewModelScope.launch {
        noteRepository.createNote(state.value.note.copy(id = 0, libraryId = libraryId))
    }

    fun duplicateNote() = viewModelScope.launch {
        noteRepository.createNote(state.value.note.copy(id = 0, reminderDate = null))
    }

    data class State(
        val library: Library,
        val note: Note,
    )
}