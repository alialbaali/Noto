package com.noto.app.recentnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.Font
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.*
import kotlinx.coroutines.flow.*
import kotlinx.datetime.LocalDate

class RecentNotesViewModel(
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val storage: LocalStorage,
) : ViewModel() {
    private val mutableNotes = MutableStateFlow<UiState<Map<LocalDate, List<NoteWithLabels>>>>(UiState.Loading)
    val notes get() = mutableNotes.asStateFlow()

    private val mutableNotesVisibility = MutableStateFlow(emptyMap<LocalDate, Boolean>())
    val notesVisibility get() = mutableNotesVisibility.asStateFlow()

    val font = storage.get(Constants.FontKey)
        .filterNotNull()
        .map { Font.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    val isCollapseToolbar = storage.getOrNull(Constants.CollapseToolbar)
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val mutableIsSearchEnabled = MutableStateFlow(false)
    val isSearchEnabled get() = mutableIsSearchEnabled.asStateFlow()

    private val mutableSearchTerm = MutableStateFlow("")
    val searchTerm get() = mutableSearchTerm.asStateFlow()

    init {
        combine(
            noteRepository.getAllMainNotes(),
            labelRepository.getAllLabels(),
            noteLabelRepository.getNoteLabels(),
            searchTerm.map { it.trim() },
        ) { notes, labels, noteLabels, searchTerm ->
            mutableNotesVisibility.value = notes.mapNotNull { it.accessDate?.toLocalDate() }.map {
                val isVisible = notesVisibility.value[it] ?: true
                it to isVisible
            }.toMap()
            mutableNotes.value = notes
                .filterRecentlyAccessed()
                .mapWithLabels(labels, noteLabels)
                .filterContent(searchTerm)
                .groupBy { noteWithLabels -> noteWithLabels.first.accessDate?.toLocalDate() }
                .filterNotNullKeys()
                .filterValues { it.isNotEmpty() }
                .mapValues { it.value.sortedByDescending { it.first.accessDate } }
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
}