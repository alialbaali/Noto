package com.noto.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.FolderIdWithNotesCount
import com.noto.app.domain.model.FolderListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.Comparator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val sortingType = settingsRepository.sortingType
        .stateIn(viewModelScope, SharingStarted.Lazily, FolderListSortingType.CreationDate)

    val sortingOrder = settingsRepository.sortingOrder
        .stateIn(viewModelScope, SharingStarted.Lazily, SortingOrder.Descending)

    val folders = combine(
        folderRepository.getFolders(),
        noteRepository.getFolderNotesCount(),
        sortingType,
        sortingOrder,
    ) { folders, notesCount, sortingType, sortingOrder ->
        folders
            .filter { it.parentId == null }
            .mapRecursively(folders, notesCount, sortingType, sortingOrder)
            .sortedWith(Folder.Comparator(sortingOrder, sortingType))
    }
        .map { UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    val archivedFolders = combine(
        folderRepository.getArchivedFolders(),
        noteRepository.getFolderNotesCount(),
        sortingType,
        sortingOrder,
    ) { folders, notesCount, sortingType, sortingOrder ->
        folders
            .filter { it.parentId == null }
            .mapRecursively(folders, notesCount, sortingType, sortingOrder)
            .sortedWith(Folder.Comparator(sortingOrder, sortingType))
    }
        .map { UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    val vaultedFolders = combine(
        folderRepository.getVaultedFolders(),
        noteRepository.getFolderNotesCount(),
        sortingType,
        sortingOrder,
    ) { folders, notesCount, sortingType, sortingOrder ->
        folders
            .filter { it.parentId == null }
            .mapRecursively(folders, notesCount, sortingType, sortingOrder)
            .sortedWith(Folder.Comparator(sortingOrder, sortingType))
    }
        .map { UiState.Success(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, UiState.Loading)

    val isVaultOpen = settingsRepository.isVaultOpen
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val vaultPasscode = settingsRepository.vaultPasscode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val isShowNotesCount = settingsRepository.isShowNotesCount
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val allNotes = noteRepository.getAllNotes()
        .combine(folderRepository.getAllUnvaultedFolders()) { notes, folders ->
            notes.filter { note -> folders.any { folder -> folder.id == note.folderId } }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateFoldersView(sortingType: FolderListSortingType, sortingOrder: SortingOrder) = viewModelScope.launch {
        settingsRepository.updateSortingType(sortingType)
        if (sortingType == FolderListSortingType.Manual)
            settingsRepository.updateSortingOrder(SortingOrder.Ascending)
        else
            settingsRepository.updateSortingOrder(sortingOrder)
    }

    fun updateFolderPosition(folder: Folder, position: Int) = viewModelScope.launch {
        folderRepository.updateFolder(folder.copy(position = position))
    }

    fun updateFolderParentId(folder: Folder, parentId: Long?) = viewModelScope.launch {
        folderRepository.updateFolder(folder.copy(parentId = parentId))
    }

    fun openVault() = viewModelScope.launch {
        settingsRepository.updateIsVaultOpen(true)
    }

    fun closeVault() = viewModelScope.launch {
        settingsRepository.updateIsVaultOpen(false)
    }

    private fun List<Folder>.mapRecursively(
        allFolders: List<Folder>,
        foldersNotesCount: List<FolderIdWithNotesCount>,
        sortingType: FolderListSortingType,
        sortingOrder: SortingOrder,
    ): List<Pair<Folder, Int>> {
        return map { folder ->
            val notesCount = foldersNotesCount.firstOrNull { it.folderId == folder.id }?.notesCount ?: 0
            val childLibraries = allFolders
                .filter { it.parentId == folder.id }
                .mapRecursively(allFolders, foldersNotesCount, sortingType, sortingOrder)
                .sortedWith(Folder.Comparator(sortingOrder, sortingType))
            folder.copy(folders = childLibraries) to notesCount
        }
    }
}