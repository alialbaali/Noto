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

    val library = libraryRepository.getLibraryById(libraryId)
        .stateIn(viewModelScope, SharingStarted.Lazily, Library(position = 0))

    private val mutableNote = MutableStateFlow(Note(noteId, libraryId, position = 0, body = body ?: ""))
    val note get() = mutableNote.asStateFlow()

    init {
        if (noteId != 0L)
            noteRepository.getNoteById(noteId)
                .onEach { mutableNote.value = it }
                .launchIn(viewModelScope)
    }

    fun createOrUpdateNote(title: String, body: String) = viewModelScope.launch {
        val note = note.value.copy(
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

    fun deleteNote() = viewModelScope.launch { noteRepository.deleteNote(note.value) }

    fun toggleNoteIsArchived() = viewModelScope.launch {
        noteRepository.updateNote(note.value.copy(isArchived = !note.value.isArchived))
    }

    fun toggleNoteIsStarred() = viewModelScope.launch {
        noteRepository.updateNote(note.value.copy(isStarred = !note.value.isStarred))
    }

    fun setNoteReminder(instant: Instant?) = viewModelScope.launch {
        noteRepository.updateNote(note.value.copy(reminderDate = instant))
    }

    fun moveNote(libraryId: Long) = viewModelScope.launch {
        noteRepository.updateNote(note.value.copy(libraryId = libraryId))
    }

    fun copyNote(libraryId: Long) = viewModelScope.launch {
        noteRepository.createNote(note.value.copy(id = 0, libraryId = libraryId))
    }

    fun duplicateNote() = viewModelScope.launch {
        noteRepository.createNote(note.value.copy(id = 0, reminderDate = null))
    }
}