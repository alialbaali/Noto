package com.noto.app.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.util.isValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class NoteViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val libraryId: Long,
    private val noteId: Long,
    private val body: String?,
) : ViewModel() {

    private val mutableLibrary = MutableStateFlow(Library(position = 0))
    val library get() = mutableLibrary.asStateFlow()

    private val mutableNote = MutableStateFlow(Note(noteId, libraryId, position = 0, body = body ?: ""))
    val note get() = mutableNote.asStateFlow()

    init {
        libraryRepository.getLibraryById(libraryId)
            .onEach { mutableLibrary.value = it }
            .launchIn(viewModelScope)

        if (noteId != 0L)
            noteRepository.getNoteById(noteId)
                .onEach { mutableNote.value = it }
                .launchIn(viewModelScope)
    }

    fun createOrUpdateNote(title: String, body: String) = viewModelScope.launch {
        val note = note.value.copy(
            title = title,
            body = body,
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

    fun duplicateNote() = viewModelScope.launch {
        noteRepository.createNote(note.value.copy(id = 0, reminderDate = null))
    }

}