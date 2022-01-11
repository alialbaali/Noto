package com.noto.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.LibraryListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    val libraries = libraryRepository.getLibraries()
        .combine(noteRepository.getLibrariesNotesCount()) { libraries, librariesNotesCount ->
            libraries.map { library ->
                val notesCount = librariesNotesCount.firstOrNull { it.libraryId == library.id }?.notesCount ?: 0
                library to notesCount
            }
        }
        .map { UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    val archivedLibraries = libraryRepository.getArchivedLibraries()
        .combine(noteRepository.getLibrariesNotesCount()) { libraries, librariesNotesCount ->
            libraries.map { library ->
                val notesCount = librariesNotesCount.firstOrNull { it.libraryId == library.id }?.notesCount ?: 0
                library to notesCount
            }
        }
        .map { UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    val vaultedLibraries = libraryRepository.getVaultedLibraries()
        .combine(noteRepository.getLibrariesNotesCount()) { libraries, librariesNotesCount ->
            libraries.map { library ->
                val notesCount = librariesNotesCount.firstOrNull { it.libraryId == library.id }?.notesCount ?: 0
                library to notesCount
            }
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

    val layout = storage.get(Constants.LibraryListLayoutKey)
        .filterNotNull()
        .map { Layout.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Layout.Grid)

    val sortingType = storage.get(Constants.LibraryListSortingTypeKey)
        .filterNotNull()
        .map { LibraryListSortingType.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, LibraryListSortingType.CreationDate)

    val sortingOrder = storage.get(Constants.LibraryListSortingOrderKey)
        .filterNotNull()
        .map { SortingOrder.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, SortingOrder.Descending)

    val isShowNotesCount = storage.get(Constants.ShowNotesCountKey)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

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

    fun openVault() = viewModelScope.launch {
        storage.put(Constants.IsVaultOpen, true.toString())
    }

    fun closeVault() = viewModelScope.launch {
        storage.put(Constants.IsVaultOpen, false.toString())
    }
}