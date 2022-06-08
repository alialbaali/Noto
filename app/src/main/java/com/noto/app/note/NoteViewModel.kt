package com.noto.app.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.*
import com.noto.app.util.firstLineOrEmpty
import com.noto.app.util.isValid
import com.noto.app.util.takeAfterFirstLineOrEmpty
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class NoteViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
    private val folderId: Long,
    private val noteId: Long,
    private val body: String?,
    private var labelsIds: LongArray,
) : ViewModel() {

    private val mutableNote = MutableStateFlow(Note(noteId, folderId, position = 0))
    val note get() = mutableNote.asStateFlow()

    val folder = folderRepository.getFolderById(folderId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, Folder(position = 0))

    val font = settingsRepository.font
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    private val mutableLabels = MutableStateFlow<Map<Label, Boolean>>(emptyMap())
    val labels get() = mutableLabels.asStateFlow()

    val isDoNotDisturb = settingsRepository.isDoNotDisturb
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isScreenOn = settingsRepository.isScreenOn
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isFullScreen = settingsRepository.isFullScreen
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    init {
        noteRepository.getNoteById(noteId)
            .also {
                viewModelScope.launch {
                    it.firstOrNull()?.let {
                        noteRepository.updateNote(it.copy(accessDate = Clock.System.now()))
                    }
                }
            }
            .onStart { emit(Note(noteId, folderId, position = 0, title = body.firstLineOrEmpty(), body = body.takeAfterFirstLineOrEmpty())) }
            .filterNotNull()
            .onEach { mutableNote.value = it }
            .launchIn(viewModelScope)

        combine(
            labelRepository.getLabelsByFolderId(folderId)
                .filterNotNull(),
            noteLabelRepository.getNoteLabels()
                .filterNotNull(),
        ) { labels, noteLabels ->
            labels.sortedBy { it.position }.associateWith { label ->
                noteLabels.filter { it.noteId == note.value.id }.any { it.labelId == label.id } || labelsIds.any { label.id == it }
            }
        }
            .onEach {
                mutableLabels.value = it
                labelsIds = longArrayOf() // Setting this value to empty array so it can be used only once.
            }
            .launchIn(viewModelScope)
    }

    fun createOrUpdateNote(title: String, body: String) = viewModelScope.launch {
        val note = note.value.copy(
            title = title.trim(),
            body = body.trim(),
            accessDate = Clock.System.now(),
        )
        if (note.isValid)
            if (note.id == 0L)
                noteRepository.createNote(note).also { id ->
                    noteRepository.getNoteById(id)
                        .filterNotNull()
                        .onEach { createdNote -> mutableNote.value = createdNote }
                        .launchIn(viewModelScope)

                    labels.value.filterValues { it }
                        .keys
                        .forEach { label -> noteLabelRepository.createNoteLabel(NoteLabel(noteId = id, labelId = label.id)) }
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

    fun moveNote(folderId: Long) = viewModelScope.launch {
        noteRepository.updateNote(note.value.copy(folderId = folderId))
        val folderLabels = labelRepository.getLabelsByFolderId(folderId).first()
        labels.value
            .filterValues { it }
            .keys
            .forEach { label ->
                launch {
                    if (folderLabels.any { it.title == label.title }) {
                        val labelId = folderLabels.first { it.title == label.title }.id
                        noteLabelRepository.createNoteLabel(NoteLabel(labelId = labelId, noteId = note.value.id))
                    } else {
                        val labelId = labelRepository.createLabel(label.copy(id = 0, folderId = folderId))
                        noteLabelRepository.createNoteLabel(NoteLabel(labelId = labelId, noteId = note.value.id))
                    }
                }
            }
    }

    fun copyNote(folderId: Long) = viewModelScope.launch {
        val noteId = noteRepository.createNote(note.value.copy(id = 0, folderId = folderId))
        val folderLabels = labelRepository.getLabelsByFolderId(folderId).first()
        labels.value
            .filterValues { it }
            .keys
            .forEach { label ->
                launch {
                    if (folderLabels.any { it.title == label.title }) {
                        val labelId = folderLabels.first { it.title == label.title }.id
                        noteLabelRepository.createNoteLabel(NoteLabel(labelId = labelId, noteId = noteId))
                    } else {
                        val labelId = labelRepository.createLabel(label.copy(id = 0, folderId = folderId))
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
        if (note.value.id == 0L)
            mutableLabels.value = mutableLabels.value.mapValues { if (it.key.id == id) true else it.value }
        else
            noteLabelRepository.createNoteLabel(NoteLabel(noteId = note.value.id, labelId = id))

    }

    fun unselectLabel(id: Long) = viewModelScope.launch {
        if (note.value.id == 0L)
            mutableLabels.value = mutableLabels.value.mapValues { if (it.key.id == id) false else it.value }
        else
            noteLabelRepository.deleteNoteLabel(note.value.id, id)
    }
}