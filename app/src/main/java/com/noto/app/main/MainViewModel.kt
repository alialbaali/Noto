package com.noto.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.LibraryIdWithNotesCount
import com.noto.app.domain.model.LibraryListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.sorted
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val sortingType = settingsRepository.sortingType
        .stateIn(viewModelScope, SharingStarted.Lazily, LibraryListSortingType.CreationDate)

    val sortingOrder = settingsRepository.sortingOrder
        .stateIn(viewModelScope, SharingStarted.Lazily, SortingOrder.Descending)

    val libraries = combine(
        libraryRepository.getLibraries(),
        noteRepository.getLibrariesNotesCount(),
        sortingType,
        sortingOrder,
    ) { libraries, notesCount, sortingType, sortingOrder ->
        libraries
            .filter { it.parentId == null }
            .mapRecursively(libraries, notesCount, sortingType, sortingOrder)
            .sorted(sortingType, sortingOrder)
    }
        .map { UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    val archivedLibraries = combine(
        libraryRepository.getArchivedLibraries(),
        noteRepository.getLibrariesNotesCount(),
        sortingType,
        sortingOrder,
    ) { libraries, notesCount, sortingType, sortingOrder ->
        libraries
            .filter { it.parentId == null }
            .mapRecursively(libraries, notesCount, sortingType, sortingOrder)
            .sorted(sortingType, sortingOrder)
    }
        .map { UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    val vaultedLibraries = combine(
        libraryRepository.getVaultedLibraries(),
        noteRepository.getLibrariesNotesCount(),
        sortingType,
        sortingOrder,
    ) { libraries, notesCount, sortingType, sortingOrder ->
        libraries
            .filter { it.parentId == null }
            .mapRecursively(libraries, notesCount, sortingType, sortingOrder)
            .sorted(sortingType, sortingOrder)
    }
        .map { UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    val isVaultOpen = settingsRepository.isVaultOpen
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val vaultPasscode = settingsRepository.vaultPasscode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isBioAuthEnabled = settingsRepository.isBioAuthEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isShowNotesCount = settingsRepository.isShowNotesCount
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val allNotes = noteRepository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSortingType(value: LibraryListSortingType) = viewModelScope.launch {
        settingsRepository.updateSortingType(value)
        if (value == LibraryListSortingType.Manual)
            updateSortingOrder(SortingOrder.Ascending)
    }

    fun updateSortingOrder(value: SortingOrder) = viewModelScope.launch {
        settingsRepository.updateSortingOrder(value)
    }

    fun updateLibraryPosition(library: Library, position: Int) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.copy(position = position))
    }

    fun updateLibraryParentId(library: Library, parentId: Long?) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.copy(parentId = parentId))
    }

    fun openVault() = viewModelScope.launch {
        settingsRepository.updateIsVaultOpen(true)
    }

    fun closeVault() = viewModelScope.launch {
        settingsRepository.updateIsVaultOpen(false)
    }

    private fun List<Library>.mapRecursively(
        allLibraries: List<Library>,
        librariesNotesCount: List<LibraryIdWithNotesCount>,
        sortingType: LibraryListSortingType,
        sortingOrder: SortingOrder,
    ): List<Pair<Library, Int>> {
        return map { library ->
            val notesCount = librariesNotesCount.firstOrNull { it.libraryId == library.id }?.notesCount ?: 0
            val childLibraries = allLibraries
                .filter { it.parentId == library.id }
                .mapRecursively(allLibraries, librariesNotesCount, sortingType, sortingOrder)
                .sorted(sortingType, sortingOrder)
                .sortedByDescending { it.first.isPinned }
            library.copy(libraries = childLibraries) to notesCount
        }
    }
}