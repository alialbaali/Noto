package com.noto.app.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.*
import com.noto.app.util.NoteWithLabels
import com.noto.app.util.filterContent
import com.noto.app.util.forEachRecursively
import com.noto.app.util.mapWithLabels
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FolderViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
    private val folderId: Long,
) : ViewModel() {

    val folder = combine(
        folderRepository.getFolderById(folderId)
            .filterNotNull()
            .onStart { emit(Folder(folderId, position = 0)) },
        folderRepository.getAllFolders(),
    ) { folder, folders ->
        mutableNotoColors.value = notoColors.value.mapTrueIfSameColor(folder.color)
        folder.mapRecursively(folders)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Folder(folderId, position = 0))

    private val mutableNotes = MutableStateFlow<UiState<List<NoteWithLabels>>>(UiState.Loading)
    val notes get() = mutableNotes.asStateFlow()

    private val mutableArchivedNotes = MutableStateFlow<UiState<List<NoteWithLabels>>>(UiState.Loading)
    val archivedNotes get() = mutableArchivedNotes.asStateFlow()

    private val mutableLabels = MutableStateFlow(emptyMap<Label, Boolean>())
    val labels get() = mutableLabels.asStateFlow()

    val font = settingsRepository.font
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    private val mutableNotoColors = MutableStateFlow(NotoColor.values().associateWith { false }.toList())
    val notoColors get() = mutableNotoColors.asStateFlow()

    private val mutableIsSearchEnabled = MutableStateFlow(false)
    val isSearchEnabled get() = mutableIsSearchEnabled.asStateFlow()

    private val mutableSearchTerm = MutableStateFlow("")
    val searchTerm get() = mutableSearchTerm.asStateFlow()

    init {
        combine(
            noteRepository.getNotesByFolderId(folderId)
                .filterNotNull(),
            noteRepository.getArchivedNotesByFolderId(folderId)
                .filterNotNull(),
            labelRepository.getLabelsByFolderId(folderId)
                .filterNotNull(),
            noteLabelRepository.getNoteLabels()
                .filterNotNull(),
            searchTerm.map { it.trim() },
        ) { notes, archivedNotes, labels, noteLabels, searchTerm ->
            mutableNotes.value = notes.mapWithLabels(labels, noteLabels).filterContent(searchTerm).let { UiState.Success(it) }
            mutableArchivedNotes.value = archivedNotes.mapWithLabels(labels, noteLabels).let { UiState.Success(it) }
        }.launchIn(viewModelScope)

        labelRepository.getLabelsByFolderId(folderId)
            .filterNotNull()
            .map {
                it.sortedBy { it.position }.map { label ->
                    val value = labels.value
                        .toList()
                        .find { pair -> pair.first.id == label.id }
                        ?.second ?: false
                    label to value
                }.toMap()
            }
            .onEach { mutableLabels.value = it }
            .launchIn(viewModelScope)
    }

    fun createOrUpdateFolder(
        title: String,
        layout: Layout,
        notePreviewSize: Int,
        newNoteCursorPosition: NewNoteCursorPosition,
        isShowNoteCreationDate: Boolean,
    ) = viewModelScope.launch {
        val color = notoColors.value.first { it.second }.first

        val folder = folder.value.copy(
            title = title.trim(),
            color = color,
            layout = layout,
            notePreviewSize = notePreviewSize,
            isShowNoteCreationDate = isShowNoteCreationDate,
            newNoteCursorPosition = newNoteCursorPosition,
        )

        if (folderId == 0L)
            folderRepository.createFolder(folder)
        else
            folderRepository.updateFolder(folder)
    }

    fun deleteFolder() = viewModelScope.launch {
        folderRepository.deleteFolder(folder.value)
        folder.value.folders.forEach { childFolder ->
            folderRepository.updateFolder(childFolder.first.copy(parentId = folder.value.parentId))
        }
    }

    fun toggleFolderIsArchived() = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(isArchived = !folder.value.isArchived, isVaulted = false, parentId = null))
        folder.value.folders.forEachRecursively { entry, _ ->
            launch {
                folderRepository.updateFolder(entry.first.copy(isArchived = !entry.first.isArchived, isVaulted = false))
            }
        }
    }

    fun toggleFolderIsVaulted() = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(isVaulted = !folder.value.isVaulted, isArchived = false, parentId = null))
        folder.value.folders.forEachRecursively { entry, _ ->
            launch {
                folderRepository.updateFolder(entry.first.copy(isVaulted = !entry.first.isVaulted, isArchived = false))
            }
        }
    }

    fun toggleFolderIsPinned() = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(isPinned = !folder.value.isPinned))
    }

    fun updateNotePosition(note: Note, position: Int) = viewModelScope.launch {
        noteRepository.updateNote(note.copy(position = position))
    }

    fun updateSortingType(value: NoteListSortingType) = viewModelScope.launch {
        if (value == NoteListSortingType.Manual)
            folderRepository.updateFolder(folder.value.copy(sortingType = value, sortingOrder = SortingOrder.Ascending))
        else
            folderRepository.updateFolder(folder.value.copy(sortingType = value))
    }

    fun updateSortingOrder(value: SortingOrder) = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(sortingOrder = value))
    }

    fun updateGrouping(value: Grouping) = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(grouping = value))
    }

    fun selectNotoColor(notoColor: NotoColor) {
        mutableNotoColors.value = mutableNotoColors.value
            .mapTrueIfSameColor(notoColor)
    }

    fun selectLabel(id: Long) {
        mutableLabels.value = labels.value
            .map { it.key to if (it.value) true else (it.key.id == id) }
            .toMap()
    }

    fun deselectLabel(id: Long) {
        mutableLabels.value = labels.value
            .map { it.key to if (it.key.id == id) false else it.value }
            .toMap()
    }

    fun clearLabelSelection() {
        mutableLabels.value = labels.value
            .map { it.key to false }
            .toMap()
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

    fun updateFolderParentId(folderId: Long) = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(parentId = folderId.takeUnless { it == 0L }))
    }

    private fun List<Pair<NotoColor, Boolean>>.mapTrueIfSameColor(notoColor: NotoColor) = map { it.first to (it.first == notoColor) }

    private fun Folder.mapRecursively(allFolders: List<Folder>): Folder {
        val childFolders = allFolders
            .filter { it.parentId == id }
            .map { it.mapRecursively(allFolders) to 0 }
        return copy(folders = childFolders)
    }
}