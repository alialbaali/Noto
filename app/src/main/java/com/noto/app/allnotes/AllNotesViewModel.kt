package com.noto.app.allnotes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Library
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.NoteWithLabels
import com.noto.app.util.mapWithLabels
import kotlinx.coroutines.flow.*

class AllNotesViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    private val mutableNotes = MutableStateFlow<UiState<Map<Library, List<NoteWithLabels>>>>(UiState.Loading)
    val notes get() = mutableNotes.asStateFlow()

    private val mutableNotesVisibility = MutableStateFlow(emptyMap<Library, Boolean>())
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

    init {
        combine(
            libraryRepository.getAllLibraries(),
            noteRepository.getAllNotes(),
            labelRepository.getAllLabels(),
            noteLabelRepository.getNoteLabels(),
        ) { libraries, notes, labels, noteLabels ->
            mutableNotesVisibility.value = libraries.map {
                val isVisible = notesVisibility.value[it] ?: true
                it to isVisible
            }.toMap()
            mutableNotes.value = notes
                .mapWithLabels(labels, noteLabels)
                .groupBy { noteWithLabels ->
                    libraries.firstOrNull { library ->
                        library.id == noteWithLabels.first.libraryId
                    }
                }
                .filterNotNullKeys()
                .let { UiState.Success(it) }
        }.launchIn(viewModelScope)
    }

    fun toggleIsSearchEnabled() {
        mutableIsSearchEnabled.value = !mutableIsSearchEnabled.value
    }

    fun toggleVisibilityForLibrary(libraryId: Long) {
        mutableNotesVisibility.value = notesVisibility.value.mapValues {
            if (it.key.id == libraryId)
                !it.value
            else
                it.value
        }
    }

    private fun <K, V> Map<K?, V>.filterNotNullKeys() = filterKeys { it != null } as Map<K, V>
}