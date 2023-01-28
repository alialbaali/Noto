package com.noto.app.note

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.ScreenBrightnessLevel
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.sorted
import kotlinx.coroutines.flow.*

class NotePagerViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository,
    private val folderId: Long,
    private val noteId: Long,
    private val selectedNoteIds: LongArray,
) : ViewModel() {

    val folder = folderRepository.getFolderById(folderId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Eagerly, Folder(position = 0))

    val noteIds = noteRepository.getNotesByFolderId(folderId)
        .combine(folder) { notes, folder -> notes.sorted(folder.sortingType, folder.sortingOrder) }
        .map {
            it.map { note -> note.id }
                .filter { id -> if (selectedNoteIds.isEmpty()) true else selectedNoteIds.contains(id) }
                .sortedBy { id -> selectedNoteIds.indexOf(id) }
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val isDoNotDisturb = settingsRepository.isDoNotDisturb
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val isScreenOn = settingsRepository.isScreenOn
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val isFullScreen = settingsRepository.isFullScreen
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val screenBrightnessLevel = settingsRepository.screenBrightnessLevel
        .stateIn(viewModelScope, SharingStarted.Eagerly, ScreenBrightnessLevel.System)

    private val mutableSelectedId = MutableStateFlow(noteId)
    val selectedId get() = mutableSelectedId.asStateFlow()

    private val mutableLastScrollPosition = MutableStateFlow(0)
    val lastScrollPosition get() = mutableLastScrollPosition.asStateFlow()

    fun selectIdByIndex(index: Int) {
        mutableSelectedId.value = noteIds.value[index]
    }

    fun selectNextId() {
        val selectedIndex = noteIds.value.indexOf(selectedId.value)
        val id = noteIds.value.getOrNull(selectedIndex + 1)
        if (id != null) mutableSelectedId.value = id
    }

    fun selectPreviousId() {
        val selectedIndex = noteIds.value.indexOf(selectedId.value)
        val id = noteIds.value.getOrNull(selectedIndex - 1)
        if (id != null) mutableSelectedId.value = id
    }

    fun selectFirstId() {
        mutableSelectedId.value = noteIds.value.first()
    }

    fun selectLastId() {
        mutableSelectedId.value = noteIds.value.last()
    }

    fun setLastScrollPosition(position: Int) {
        mutableLastScrollPosition.value = position
    }

}