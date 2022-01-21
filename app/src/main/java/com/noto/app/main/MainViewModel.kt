package com.noto.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.sorted
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    val sortingType = storage.get(Constants.LibraryListSortingTypeKey)
        .filterNotNull()
        .map { LibraryListSortingType.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, LibraryListSortingType.CreationDate)

    val sortingOrder = storage.get(Constants.LibraryListSortingOrderKey)
        .filterNotNull()
        .map { SortingOrder.valueOf(it) }
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

    val isVaultOpen = storage.get(Constants.IsVaultOpen)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val vaultPasscode = storage.getOrNull(Constants.VaultPasscode)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isBioAuthEnabled = storage.getOrNull(Constants.IsBioAuthEnabled)
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isShowNotesCount = storage.get(Constants.ShowNotesCountKey)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val allNotes = noteRepository.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateLayout(value: Layout) = viewModelScope.launch {
        storage.put(Constants.LibraryListLayoutKey, value.toString())
    }

    fun updateSortingType(value: LibraryListSortingType) = viewModelScope.launch {
        storage.put(Constants.LibraryListSortingTypeKey, value.toString())
        if (value == LibraryListSortingType.Manual)
            updateSortingOrder(SortingOrder.Ascending)
    }

    fun updateSortingOrder(value: SortingOrder) = viewModelScope.launch {
        storage.put(Constants.LibraryListSortingOrderKey, value.toString())
    }

    fun updateLibraryPosition(library: Library, position: Int) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.copy(position = position))
    }

    fun updateLibraryParentId(library: Library, parentId: Long?) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.copy(parentId = parentId))
    }

    fun openVault() = viewModelScope.launch {
        storage.put(Constants.IsVaultOpen, true.toString())
    }

    fun closeVault() = viewModelScope.launch {
        storage.put(Constants.IsVaultOpen, false.toString())
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