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
import java.time.ZonedDateTime

class NoteViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val libraryId: Long,
    private val noteId: Long,
) : ViewModel() {

    private val mutableNote = MutableStateFlow(Note(libraryId = libraryId, position = 0))
    val note get() = mutableNote.asStateFlow()

    private val mutableLibrary = MutableStateFlow(Library(position = 0))
    val library get() = mutableLibrary.asStateFlow()

    init {
        libraryRepository.getLibraryById(libraryId)
            .onEach { mutableLibrary.value = it }
            .launchIn(viewModelScope)

        noteRepository.getNoteById(noteId)
            .onEach { mutableNote.value = it }
            .launchIn(viewModelScope)
    }

    fun postNote(libraryId: Long) = viewModelScope.launch {
        val position = noteRepository.getNotesByLibraryId(libraryId)
            .first()
            .count()

        mutableNote.value = Note(libraryId = libraryId, position = position)
    }

    fun setNotoReminder(zonedDateTime: ZonedDateTime?) {
        viewModelScope.launch {
            noteRepository.updateNote(note.value.copy(reminderDate = zonedDateTime))
        }
    }

    fun createNote() = viewModelScope.launch {
        if (note.value.isValid()) noteRepository.createNote(note.value)
    }

    fun updateNote() = viewModelScope.launch {
        if (note.value.isValid()) noteRepository.updateNote(note.value) else deleteNoto()
    }

    fun setNotoArchived(value: Boolean) {
        viewModelScope.launch {
            noteRepository.updateNote(note.value.copy(isArchived = value))
        }
    }

    fun deleteNoto() = viewModelScope.launch { noteRepository.deleteNote(note.value) }

    fun setNoteTitle(title: String) {
        viewModelScope.launch {
            noteRepository.updateNote(note.value.copy(title = title))
        }
    }

    fun setNoteBody(body: String) {
        viewModelScope.launch {
            noteRepository.updateNote(note.value.copy(body = body))
        }
    }

    fun toggleNotoStar() {
        viewModelScope.launch {
            noteRepository.updateNote(note.value.copy(isStarred = !note.value.isStarred))
        }
    }

}