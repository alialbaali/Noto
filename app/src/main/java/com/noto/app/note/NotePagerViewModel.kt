package com.noto.app.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.ScreenBrightnessLevel
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.folder.NoteItemModel
import com.noto.app.util.Comparator
import com.noto.app.util.mapToNoteItemModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotePagerViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository,
    private val folderId: Long,
    private val noteId: Long,
    private val selectedNoteIds: LongArray,
    private val isArchive: Boolean,
) : ViewModel() {

    val folder = folderRepository.getFolderById(folderId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Folder(position = 0))

    val noteIds = if (isArchive) {
        noteRepository.getArchivedNotesByFolderId(folderId)
    } else {
        noteRepository.getNotesByFolderId(folderId)
    }.combine(folder) { notes, folder ->
        notes.mapToNoteItemModel(emptyList(), emptyList())
            .sortedWith(NoteItemModel.Comparator(folder.sortingOrder, folder.sortingType))
            .map { it.note.id }
            .filter { id -> if (selectedNoteIds.isEmpty()) true else selectedNoteIds.contains(id) }
            .sortedBy { id -> selectedNoteIds.indexOf(id) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val isDoNotDisturb = settingsRepository.isDoNotDisturb
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isScreenOn = settingsRepository.isScreenOn
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isFullScreen = settingsRepository.isFullScreen
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val screenBrightnessLevel = settingsRepository.screenBrightnessLevel
        .stateIn(viewModelScope, SharingStarted.Eagerly, ScreenBrightnessLevel.System)

    private val mutableSelectedNoteId = MutableStateFlow<Long?>(noteId)
    val selectedNoteId get() = mutableSelectedNoteId.asStateFlow()

    private val mutableLastScrollPosition = MutableStateFlow(0)
    val lastScrollPosition get() = mutableLastScrollPosition.asStateFlow()

    fun selectNoteIdByIndex(index: Int) {
        mutableSelectedNoteId.value = noteIds.value.getOrNull(index)
    }

    fun selectNextNoteId() {
        val selectedIndex = noteIds.value.indexOf(selectedNoteId.value)
        mutableSelectedNoteId.value = noteIds.value.getOrNull(selectedIndex + 1)
    }

    fun selectPreviousNoteId() {
        val selectedIndex = noteIds.value.indexOf(selectedNoteId.value)
        mutableSelectedNoteId.value = noteIds.value.getOrNull(selectedIndex - 1)
    }

    fun selectFirstNoteId() {
        mutableSelectedNoteId.value = noteIds.value.firstOrNull()
    }

    fun selectLastNoteId() {
        mutableSelectedNoteId.value = noteIds.value.lastOrNull()
    }

    fun setLastScrollPosition(position: Int) {
        mutableLastScrollPosition.value = position
    }

    fun unarchiveSelectedArchivedNote() = updateSelectedNoteId {
        val note = noteRepository.getNoteById(selectedNoteId.value ?: 0L).firstOrNull()
        if (note != null) noteRepository.updateNote(note.copy(isArchived = false))
    }

    fun deleteSelectedArchivedNote() = updateSelectedNoteId {
        val note = noteRepository.getNoteById(selectedNoteId.value ?: 0L).firstOrNull()
        if (note != null) noteRepository.deleteNote(note)
    }

    private fun updateSelectedNoteId(block: suspend () -> Unit) = viewModelScope.launch {
        val currentIndex = noteIds.value.indexOf(selectedNoteId.value)
        val lastIndex = noteIds.value.lastIndex
        val nextNoteId = noteIds.value.getOrNull(currentIndex + 1)
        val previousNoteId = noteIds.value.getOrNull(currentIndex - 1)
        val noteIdsCount = noteIds.value.count()
        block()
        mutableSelectedNoteId.value = if (noteIdsCount == 1) {
            null
        } else {
            if (currentIndex == lastIndex) previousNoteId else nextNoteId
        }
    }

}