package com.noto.app.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.*
import com.noto.app.getOrDefault
import com.noto.app.map
import com.noto.app.util.forEachRecursively
import com.noto.app.util.getOrCreateLabel
import com.noto.app.util.mapToNoteItemModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FolderViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
    private val folderId: Long,
    private var selectedNoteIds: LongArray = longArrayOf(),
) : ViewModel() {

    private val mutableFolder = MutableStateFlow(Folder(folderId, position = 0))
    val folder get() = mutableFolder.asStateFlow()

    private val mutableParentFolder = MutableStateFlow<Folder?>(null)
    val parentFolder get() = mutableParentFolder.asStateFlow()

    private val mutableNotes = MutableStateFlow<UiState<List<NoteItemModel>>>(UiState.Loading)
    val notes get() = mutableNotes.asStateFlow()

    private val mutableArchivedNotes = MutableStateFlow<UiState<List<NoteItemModel>>>(UiState.Loading)
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

    val isRememberScrollingPosition = settingsRepository.isRememberScrollingPosition
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val mutableIsSelection = MutableStateFlow(false)
    val isSelection get() = mutableIsSelection.asStateFlow()

    init {
        combine(
            folderRepository.getFolderById(folderId)
                .filterNotNull()
                .onStart { emit(Folder(folderId, position = 0)) },
            folderRepository.getAllFolders(),
        ) { folder, folders ->
            mutableNotoColors.value = notoColors.value.mapTrueIfSameColor(folder.color)
            mutableFolder.value = folder.mapRecursively(folders)
            if (folder.parentId != null)
                mutableParentFolder.value = folderRepository.getFolderById(folder.parentId).firstOrNull()
        }.launchIn(viewModelScope)

        combine(
            noteRepository.getNotesByFolderId(folderId)
                .filterNotNull(),
            noteRepository.getArchivedNotesByFolderId(folderId)
                .filterNotNull(),
            labelRepository.getLabelsByFolderId(folderId)
                .filterNotNull(),
            noteLabelRepository.getNoteLabels()
                .filterNotNull(),
        ) { notes, archivedNotes, labels, noteLabels ->
            mutableNotes.value = notes.mapToNoteItemModel(labels, noteLabels, selectedNoteIds).let { UiState.Success(it) }
            mutableArchivedNotes.value = archivedNotes.mapToNoteItemModel(labels, noteLabels).let { UiState.Success(it) }
            selectedNoteIds = longArrayOf()
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

        notes
            .onEach { notesState ->
                val isNoneSelected = notesState.getOrDefault(emptyList()).none { it.isSelected }
                if (isNoneSelected) disableSelection()
            }
            .launchIn(viewModelScope)
    }

    suspend fun getFolderById(id: Long) = folderRepository.getFolderById(id).firstOrNull()

    fun setParentFolder(parentId: Long?) = viewModelScope.launch {
        mutableParentFolder.value = if (parentId != null && parentId != 0L)
            folderRepository.getFolderById(parentId).firstOrNull()
        else
            null
    }

    fun createOrUpdateFolder(
        title: String,
        layout: Layout,
        notePreviewSize: Int,
        newNoteCursorPosition: NewNoteCursorPosition,
        isShowNoteCreationDate: Boolean,
        onCreateFolder: (Long) -> Unit,
    ) = viewModelScope.launch {
        val color = notoColors.value.first { it.second }.first

        val folder = folder.value.copy(
            title = title.trim(),
            parentId = parentFolder.value?.id,
            color = color,
            layout = layout,
            notePreviewSize = notePreviewSize,
            isShowNoteCreationDate = isShowNoteCreationDate,
            newNoteCursorPosition = newNoteCursorPosition,
        )

        if (folderId == 0L)
            folderRepository.createFolder(folder).also(onCreateFolder)
        else
            folderRepository.updateFolder(folder)
    }

    fun updateFolderScrollingPosition(scrollingPosition: Int) = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(scrollingPosition = scrollingPosition))
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

    fun updateGroupingOrder(value: GroupingOrder) = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(groupingOrder = value))
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

    fun updateFolderFilteringType(filteringType: FilteringType) = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(filteringType = filteringType))
    }

    fun enableSelection() {
        mutableIsSelection.value = true
    }

    fun disableSelection() {
        mutableIsSelection.value = false
        mutableNotes.value = notes.value.map {
            it.map { model ->
                model.copy(isSelected = false)
            }
        }
    }

    fun selectNote(id: Long) {
        mutableNotes.value = notes.value.map {
            it.map { model ->
                if (model.note.id == id)
                    model.copy(isSelected = true)
                else
                    model
            }
        }
    }

    fun deselectNote(id: Long) {
        mutableNotes.value = notes.value.map {
            it.map { model ->
                if (model.note.id == id)
                    model.copy(isSelected = false)
                else
                    model
            }
        }
    }

    fun pinSelectedNotes() = viewModelScope.launch {
        notes.value.getOrDefault(emptyList())
            .filter { model -> model.isSelected }
            .forEach { model ->
                launch {
                    noteRepository.updateNote(model.note.copy(isPinned = true))
                }
            }
    }

    fun unpinSelectedNotes() = viewModelScope.launch {
        notes.value.getOrDefault(emptyList())
            .filter { model -> model.isSelected }
            .forEach { model ->
                launch {
                    noteRepository.updateNote(model.note.copy(isPinned = false))
                }
            }
    }

    fun archiveSelectedNotes() = viewModelScope.launch {
        notes.value.getOrDefault(emptyList())
            .filter { model -> model.isSelected }
            .forEach { model ->
                launch {
                    noteRepository.updateNote(model.note.copy(isArchived = true))
                }
            }
    }

    fun duplicateSelectedNotes() = viewModelScope.launch {
        notes.value.getOrDefault(emptyList())
            .filter { model -> model.isSelected }
            .forEach { model ->
                launch {
                    val noteId = noteRepository.createNote(model.note.copy(id = 0, reminderDate = null))
                    model.labels.forEach { label ->
                        launch {
                            noteLabelRepository.createNoteLabel(NoteLabel(noteId = noteId, labelId = label.id))
                        }
                    }
                }
            }
    }

    fun moveSelectedNotes(folderId: Long) = viewModelScope.launch {
        notes.value.getOrDefault(emptyList())
            .filter { model -> model.isSelected }
            .forEach { model ->
                launch {
                    noteRepository.updateNote(model.note.copy(folderId = folderId))
                    model.labels.forEach { label ->
                        launch {
                            val labelId = labelRepository.getOrCreateLabel(folderId, label)
                            noteLabelRepository.createNoteLabel(NoteLabel(labelId = labelId, noteId = model.note.id))
                            noteLabelRepository.deleteNoteLabel(model.note.id, label.id)
                        }
                    }
                }
            }
    }

    fun copySelectedNotes(folderId: Long) = viewModelScope.launch {
        notes.value.getOrDefault(emptyList())
            .filter { model -> model.isSelected }
            .forEach { model ->
                val noteId = noteRepository.createNote(model.note.copy(id = 0, folderId = folderId))
                model.labels.forEach { label ->
                    launch {
                        val labelId = labelRepository.getOrCreateLabel(folderId, label)
                        noteLabelRepository.createNoteLabel(NoteLabel(labelId = labelId, noteId = noteId))
                    }
                }
            }
    }

    fun deleteSelectedNotes() = viewModelScope.launch {
        notes.value.getOrDefault(emptyList())
            .filter { model -> model.isSelected }
            .forEach { model ->
                launch {
                    noteRepository.deleteNote(model.note)
                }
            }
    }

    private fun List<Pair<NotoColor, Boolean>>.mapTrueIfSameColor(notoColor: NotoColor) = map { it.first to (it.first == notoColor) }

    private fun Folder.mapRecursively(allFolders: List<Folder>): Folder {
        val childFolders = allFolders
            .filter { it.parentId == id }
            .map { it.mapRecursively(allFolders) to 0 }
        return copy(folders = childFolders)
    }
}