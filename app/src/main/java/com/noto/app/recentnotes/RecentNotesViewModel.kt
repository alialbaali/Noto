package com.noto.app.recentnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.Font
import com.noto.app.domain.repository.*
import com.noto.app.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class RecentNotesViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {
    private val mutableNotes = MutableStateFlow<UiState<Map<LocalDate, List<NoteWithLabels>>>>(UiState.Loading)
    val notes get() = mutableNotes.asStateFlow()

    private val mutableNotesVisibility = MutableStateFlow(emptyMap<LocalDate, Boolean>())
    val notesVisibility get() = mutableNotesVisibility.asStateFlow()

    val font = settingsRepository.font
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    private val mutableIsSearchEnabled = MutableStateFlow(false)
    val isSearchEnabled get() = mutableIsSearchEnabled.asStateFlow()

    private val mutableSearchTerm = MutableStateFlow("")
    val searchTerm get() = mutableSearchTerm.asStateFlow()

    val isRememberScrollingPosition = settingsRepository.isRememberScrollingPosition
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val scrollingPosition = settingsRepository.recentNotesScrollingPosition
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    init {
        combine(
            folderRepository.getFolders(),
            noteRepository.getAllMainNotes(),
            labelRepository.getAllLabels(),
            noteLabelRepository.getNoteLabels(),
            searchTerm,
        ) { folders, notes, labels, noteLabels, searchTerm ->
            mutableNotesVisibility.value = notes
                .map { it.accessDate.toLocalDate() }
                .associateWith { notesVisibility.value[it] ?: true }
            mutableNotes.value = notes
                .filter { note -> folders.any { folder -> folder.id == note.folderId } }
                .filterRecentlyAccessed()
                .mapWithLabels(labels, noteLabels)
                .filterContent(searchTerm)
                .groupBy { noteWithLabels -> noteWithLabels.first.accessDate.toLocalDate() }
                .filterValues { it.isNotEmpty() }
                .mapValues { it.value.sortedByDescending { it.first.accessDate } }
                .toSortedMap(compareByDescending { it })
                .let { UiState.Success(it) }
        }.launchIn(viewModelScope)
    }

    fun toggleVisibilityForDate(date: LocalDate) {
        mutableNotesVisibility.value = notesVisibility.value.mapValues {
            if (it.key == date)
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

    fun updateScrollingPosition(scrollingPosition: Int) = viewModelScope.launch {
        settingsRepository.updateRecentNotesScrollingPosition(scrollingPosition)
    }
}