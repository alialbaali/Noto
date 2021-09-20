package com.noto.app.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.firstLineOrEmpty
import com.noto.app.util.isValid
import com.noto.app.util.takeAfterFirstLineOrEmpty
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class NoteViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val storage: LocalStorage,
    private val libraryId: Long,
    private val noteId: Long,
    private val body: String?,
) : ViewModel() {

    private val mutableNote = MutableStateFlow(
        Note(
            noteId,
            libraryId,
            position = 0,
            title = body.firstLineOrEmpty(),
            body = body.takeAfterFirstLineOrEmpty()
        )
    )
    val note get() = mutableNote.asStateFlow()

    val library = libraryRepository.getLibraryById(libraryId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, Library(position = 0))

    val font = storage.get(Constants.FontKey)
        .filterNotNull()
        .map { Font.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    private val mutableLabels = MutableStateFlow<Map<Label, Boolean>>(emptyMap())
    val labels get() = mutableLabels.asStateFlow()

    init {
        noteRepository.getNoteById(noteId)
            .onStart { emit(Note(noteId, libraryId, position = 0, title = body.firstLineOrEmpty(), body = body.takeAfterFirstLineOrEmpty())) }
            .filterNotNull()
            .onEach { mutableNote.value = it }
            .launchIn(viewModelScope)

        combine(
            labelRepository.getLabelsByLibraryId(libraryId)
                .filterNotNull(),
            noteLabelRepository.getNoteLabels()
                .filterNotNull(),
        ) { labels, noteLabels ->
            labels.sortedBy { it.position }.associateWith { label ->
                noteLabels.filter { it.noteId == note.value.id }.any { it.labelId == label.id }
            }
        }
            .onEach { mutableLabels.value = it }
            .launchIn(viewModelScope)
    }

    fun createOrUpdateNote(title: String, body: String) = viewModelScope.launch {
        val note = note.value.copy(
            title = title.trim(),
            body = body.trim(),
        )
        if (note.isValid())
            if (note.id == 0L)
                noteRepository.createNote(note).also { id ->
                    noteRepository.getNoteById(id)
                        .filterNotNull()
                        .onEach { createdNote -> mutableNote.value = createdNote }
                        .launchIn(viewModelScope)
                }
            else
                noteRepository.updateNote(note)
    }

    fun deleteNote() = viewModelScope.launch {
        noteRepository.deleteNote(note.value)
    }

    fun toggleNoteIsArchived() = viewModelScope.launch {
        noteRepository.updateNote(note.value.copy(isArchived = !note.value.isArchived))
    }

    fun toggleNoteIsPinned() = viewModelScope.launch {
        noteRepository.updateNote(note.value.copy(isPinned = !note.value.isPinned))
    }

    fun setNoteReminder(instant: Instant?) = viewModelScope.launch {
        noteRepository.updateNote(note.value.copy(reminderDate = instant))
    }

    fun moveNote(libraryId: Long) = viewModelScope.launch {
        noteRepository.updateNote(note.value.copy(libraryId = libraryId))
        val libraryLabels = labelRepository.getLabelsByLibraryId(libraryId).first()
        labels.value
            .filterValues { it }
            .keys
            .forEach { label ->
                launch {
                    if (libraryLabels.any { it.title == label.title }) {
                        val labelId = libraryLabels.first { it.title == label.title }.id
                        noteLabelRepository.createNoteLabel(NoteLabel(labelId = labelId, noteId = note.value.id))
                    } else {
                        val labelId = labelRepository.createLabel(label.copy(id = 0, libraryId = libraryId))
                        noteLabelRepository.createNoteLabel(NoteLabel(labelId = labelId, noteId = note.value.id))
                    }
                }
            }
    }

    fun copyNote(libraryId: Long) = viewModelScope.launch {
        val noteId = noteRepository.createNote(note.value.copy(id = 0, libraryId = libraryId))
        val libraryLabels = labelRepository.getLabelsByLibraryId(libraryId).first()
        labels.value
            .filterValues { it }
            .keys
            .forEach { label ->
                launch {
                    if (libraryLabels.any { it.title == label.title }) {
                        val labelId = libraryLabels.first { it.title == label.title }.id
                        noteLabelRepository.createNoteLabel(NoteLabel(labelId = labelId, noteId = noteId))
                    } else {
                        val labelId = labelRepository.createLabel(label.copy(id = 0, libraryId = libraryId))
                        noteLabelRepository.createNoteLabel(NoteLabel(labelId = labelId, noteId = noteId))
                    }
                }
            }
    }

    fun duplicateNote() = viewModelScope.launch {
        val noteId = noteRepository.createNote(note.value.copy(id = 0, reminderDate = null))
        labels.value
            .filterValues { it }
            .keys
            .forEach { label ->
                launch {
                    noteLabelRepository.createNoteLabel(NoteLabel(noteId = noteId, labelId = label.id))
                }
            }
    }

    fun selectLabel(id: Long) = viewModelScope.launch {
        if (note.value.id != 0L)
            noteLabelRepository.createNoteLabel(NoteLabel(noteId = note.value.id, labelId = id))
    }

    fun unselectLabel(id: Long) = viewModelScope.launch {
        noteLabelRepository.deleteNoteLabel(note.value.id, id)
    }
}