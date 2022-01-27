package com.noto.app.allnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.*
import com.noto.app.util.*
import kotlinx.coroutines.flow.*

class AllNotesViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val mutableNotes = MutableStateFlow<UiState<Map<Folder, List<NoteWithLabels>>>>(UiState.Loading)
    val notes get() = mutableNotes.asStateFlow()

    private val mutableNotesVisibility = MutableStateFlow(emptyMap<Folder, Boolean>())
    val notesVisibility get() = mutableNotesVisibility.asStateFlow()

    val font = settingsRepository.font
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    val isCollapseToolbar = settingsRepository.isCollapseToolbar
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val mutableIsSearchEnabled = MutableStateFlow(false)
    val isSearchEnabled get() = mutableIsSearchEnabled.asStateFlow()

    private val mutableSearchTerm = MutableStateFlow("")
    val searchTerm get() = mutableSearchTerm.asStateFlow()

    init {
        combine(
            folderRepository.getFolders(),
            noteRepository.getAllMainNotes(),
            labelRepository.getAllLabels(),
            noteLabelRepository.getNoteLabels(),
            searchTerm.map { it.trim() },
        ) { folders, notes, labels, noteLabels, searchTerm ->
            mutableNotesVisibility.value = folders.map {
                val isVisible = notesVisibility.value[it] ?: true
                it to isVisible
            }.toMap()
            mutableNotes.value = notes
                .filter { note -> folders.any { folder -> folder.id == note.folderId } }
                .mapWithLabels(labels, noteLabels)
                .filterContent(searchTerm)
                .groupBy { noteWithLabels ->
                    folders.firstOrNull { folder ->
                        folder.id == noteWithLabels.first.folderId
                    }
                }
                .filterNotNullKeys()
                .filterValues { it.isNotEmpty() }
                .mapValues { it.value.sorted(it.key.sortingType, it.key.sortingOrder) }
                .toList()
                .sortedBy { it.first.position }
                .sortedByDescending { it.first.isGeneral }
                .toMap()
                .let { UiState.Success(it) }
        }.launchIn(viewModelScope)
    }

    fun toggleVisibilityForFolder(folderId: Long) {
        mutableNotesVisibility.value = notesVisibility.value.mapValues {
            if (it.key.id == folderId)
                !it.value
            else
                it.value
        }
    }

    fun enableSearch() {
        mutableIsSearchEnabled.value = true
    }

    fun disableSearch() {
        mutableIsSearchEnabled.value = false
        setSearchTerm("")
    }

    fun setSearchTerm(searchTerm: String) {
        mutableSearchTerm.value = searchTerm
    }

    fun expandAll() {
        mutableNotesVisibility.value = notesVisibility.value.mapValues { true }
    }

    fun collapseAll() {
        mutableNotesVisibility.value = notesVisibility.value.mapValues { false }
    }
}
