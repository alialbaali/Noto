package com.noto.app.folder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.FilteringType
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Grouping
import com.noto.app.domain.model.GroupingOrder
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.NewNoteCursorPosition
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NoteLabel
import com.noto.app.domain.model.NoteListSortingType
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.OpenNotesIn
import com.noto.app.domain.model.SortingOrder
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.getOrDefault
import com.noto.app.label.LabelItemModel
import com.noto.app.map
import com.noto.app.util.LineSeparator
import com.noto.app.util.SelectedLabelsComparator
import com.noto.app.util.filterByLabels
import com.noto.app.util.filterBySearchTerm
import com.noto.app.util.filterSelected
import com.noto.app.util.forEachRecursively
import com.noto.app.util.getOrCreateLabel
import com.noto.app.util.mapToNoteItemModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

private const val AutoScrollDuration = 2000L

class FolderViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
    private val folderId: Long,
    private val selectedNoteIds: LongArray = longArrayOf(),
) : ViewModel() {

    private val mutableFolder = MutableStateFlow(Folder(position = 0))
    val folder get() = mutableFolder.asStateFlow()

    private val mutableParentFolder = MutableStateFlow<Folder?>(null)
    val parentFolder get() = mutableParentFolder.asStateFlow()

    private val mutableNotes = MutableStateFlow<UiState<List<NoteItemModel>>>(UiState.Loading)
    val notes get() = mutableNotes.asStateFlow()

    private val mutableArchivedNotes =
        MutableStateFlow<UiState<List<NoteItemModel>>>(UiState.Loading)
    val archivedNotes get() = mutableArchivedNotes.asStateFlow()

    private val mutableLabels = MutableStateFlow(emptyList<LabelItemModel>())
    val labels get() = mutableLabels.asStateFlow()

    val font = settingsRepository.font
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    private val mutableNotoColors = MutableStateFlow(NotoColor.entries.associateWith { false }.toList())
    val notoColors get() = mutableNotoColors.asStateFlow()

    private val mutableIsSearchEnabled = MutableStateFlow(false)
    val isSearchEnabled get() = mutableIsSearchEnabled.asStateFlow()

    private val mutableSearchTerm = MutableStateFlow("")
    val searchTerm get() = mutableSearchTerm.asStateFlow()

    val isRememberScrollingPosition = settingsRepository.isRememberScrollingPosition
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val mutableIsSelection = MutableStateFlow(false)
    val isSelection get() = mutableIsSelection.asStateFlow()

    val quickExit = settingsRepository.quickExit
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private var sortSelectedLabels = true

    val selectionLabels = combine(notes, labels) { notes, labels ->
        val selectedLabels =
            notes.getOrDefault(emptyList()).filter { it.isSelected }.map { it.labels }.flatten()
        labels.map { model -> LabelItemModel(model.label, selectedLabels.contains(model.label)) }
            .let {
                if (it.isNotEmpty() && sortSelectedLabels) {
                    sortSelectedLabels = false
                    it.sortedWith(SelectedLabelsComparator)
                } else {
                    it
                }
            }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val mutablePreviewNotePosition = MutableStateFlow(0)
    val previewNotePosition get() = mutablePreviewNotePosition.asStateFlow()

    private var currentPosition = mutablePreviewNotePosition.value

    private var isUserScrolling = false

    val selectedNotes
        get() = notes.value
            .getOrDefault(emptyList())
            .filter { it.isSelected }
            .sortedBy { it.selectionOrder }

    val selectedArchivedNotes
        get() = archivedNotes.value
            .getOrDefault(emptyList())
            .filter { it.isSelected }
            .sortedBy { it.selectionOrder }

    private val previewAutoScroll = settingsRepository.previewAutoScroll
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    init {
        combine(
            folderRepository.getFolderById(folderId)
                .filterNotNull()
                .onStart { emit(Folder(position = 0)) },
            folderRepository.getAllFolders(),
        ) { folder, folders ->
            mutableNotoColors.value = notoColors.value.mapTrueIfSameColor(folder.color)
            mutableFolder.value = folder.mapRecursively(folders)
            if (folder.parentId != null)
                mutableParentFolder.value =
                    folderRepository.getFolderById(folder.parentId).firstOrNull()
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
            val selectedNoteIds = selectedNoteIds.toList()
                .ifEmpty { selectedNotes.map { it.note.id } }
                .toLongArray()
            val draggedNoteIds = mutableNotes.value
                .getOrDefault(emptyList())
                .filter { it.isDragged }
                .map { it.note.id }
                .toLongArray()
            mutableNotes.value =
                notes.mapToNoteItemModel(labels, noteLabels, selectedNoteIds, draggedNoteIds)
                    .sortedBy { selectedNoteIds.indexOf(it.note.id) }
                    .let { UiState.Success(it) }
            mutableArchivedNotes.value =
                archivedNotes.mapToNoteItemModel(labels, noteLabels).let { UiState.Success(it) }
        }.launchIn(viewModelScope)

        labelRepository.getLabelsByFolderId(folderId)
            .filterNotNull()
            .map {
                it.sortedBy { it.position }.map { label ->
                    val isSelected =
                        labels.value.find { model -> model.label.id == label.id }?.isSelected
                            ?: false
                    LabelItemModel(label, isSelected)
                }
            }
            .onEach { mutableLabels.value = it }
            .launchIn(viewModelScope)

        notes
            .onEach { notesState ->
                val isNoneSelected = notesState.getOrDefault(emptyList()).none { it.isSelected }
                if (isNoneSelected) {
                    disableSelection()
                    deselectAllNotes()
                }
            }
            .launchIn(viewModelScope)

        archivedNotes
            .onEach { notesState ->
                val isNoneSelected = notesState.getOrDefault(emptyList()).none { it.isSelected }
                if (isNoneSelected) {
                    disableSelection()
                    deselectAllArchivedNotes()
                }
            }
            .launchIn(viewModelScope)

        notes.combine(previewAutoScroll) { state, isEnabled ->
            if (isEnabled) {
                val models = state.getOrDefault(emptyList()).filter { it.isSelected }
                if (models.isNotEmpty()) {
                    while (true) {
                        if (isUserScrolling) {
                            delay(1000L)
                            isUserScrolling = false
                        }
                        delay(AutoScrollDuration)
                        val nextPosition = if (currentPosition == models.lastIndex) {
                            0
                        } else {
                            currentPosition + 1
                        }
                        mutablePreviewNotePosition.value = nextPosition
                    }
                }
            }
        }.launchIn(viewModelScope)
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
        openNotesIn: OpenNotesIn,
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
            openNotesIn = openNotesIn,
        )

        if (folderId == 0L)
            folderRepository.createFolder(folder).also(onCreateFolder)
        else
            folderRepository.updateFolder(folder)
    }

    fun updateFolderScrollingPosition(scrollingPosition: Int) = viewModelScope.launch {
        if (folder.value.id != 0L) {
            folderRepository.updateFolder(folder.value.copy(scrollingPosition = scrollingPosition))
        }
    }

    fun deleteFolder() = viewModelScope.launch {
        folderRepository.deleteFolder(folder.value)
        folder.value.folders.forEach { childFolder ->
            folderRepository.updateFolder(childFolder.first.copy(parentId = folder.value.parentId))
        }
    }

    fun toggleFolderIsArchived() = viewModelScope.launch {
        folderRepository.updateFolder(
            folder.value.copy(
                isArchived = !folder.value.isArchived,
                isVaulted = false,
                parentId = null
            )
        )
        folder.value.folders.forEachRecursively { entry, _ ->
            launch {
                folderRepository.updateFolder(
                    entry.first.copy(
                        isArchived = !entry.first.isArchived,
                        isVaulted = false
                    )
                )
            }
        }
    }

    fun toggleFolderIsVaulted() = viewModelScope.launch {
        folderRepository.updateFolder(
            folder.value.copy(
                isVaulted = !folder.value.isVaulted,
                isArchived = false,
                parentId = null
            )
        )
        folder.value.folders.forEachRecursively { entry, _ ->
            launch {
                folderRepository.updateFolder(
                    entry.first.copy(
                        isVaulted = !entry.first.isVaulted,
                        isArchived = false
                    )
                )
            }
        }
    }

    fun toggleFolderIsPinned() = viewModelScope.launch {
        folderRepository.updateFolder(folder.value.copy(isPinned = !folder.value.isPinned))
    }

    fun updateNotePosition(note: Note, position: Int) = viewModelScope.launch {
        noteRepository.updateNote(note.copy(position = position))
    }

    fun selectNotoColor(notoColor: NotoColor) {
        mutableNotoColors.value = mutableNotoColors.value
            .mapTrueIfSameColor(notoColor)
    }

    fun selectLabel(id: Long) {
        mutableLabels.value = labels.value
            .map { model -> if (model.label.id == id) model.copy(isSelected = true) else model }
    }

    fun deselectLabel(id: Long) {
        mutableLabels.value = labels.value
            .map { model -> if (model.label.id == id) model.copy(isSelected = false) else model }
    }

    fun clearLabelSelection() {
        mutableLabels.value = labels.value
            .map { model -> model.copy(isSelected = false) }
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

    fun enableSelection() {
        mutableIsSelection.value = true
    }

    fun disableSelection() {
        mutableIsSelection.value = false
    }

    fun enableDragging(id: Long) {
        mutableNotes.value = notes.value.map {
            it.map { model ->
                model.copy(isSelected = false, selectionOrder = -1, isDragged = model.note.id == id)
            }
        }
    }

    fun disableDragging() {
        mutableNotes.value = notes.value.map {
            it.map { model ->
                model.copy(isDragged = false)
            }
        }
    }

    fun selectNote(id: Long) {
        mutableNotes.value = notes.value.map {
            val selectionOrder = it.maxOf { it.selectionOrder }.plus(1)
            it.map { model ->
                if (model.note.id == id)
                    model.copy(isSelected = true, selectionOrder = selectionOrder)
                else
                    model
            }
        }
    }

    fun deselectNote(id: Long) {
        mutableNotes.value = notes.value.map {
            it.map { model ->
                if (model.note.id == id)
                    model.copy(isSelected = false, selectionOrder = -1)
                else
                    model
            }
        }
    }

    fun selectAllNotes() {
        var selectionOrder = -1
        val filteredNotes = notes.value.getOrDefault(emptyList())
            .filterByLabels(labels.value.filterSelected(), folder.value.filteringType)
            .filterBySearchTerm(searchTerm.value)

        mutableNotes.value = notes.value.map {
            it.map { model ->
                model.copy(
                    isSelected = if (model.isSelected) true else filteredNotes.contains(model),
                    selectionOrder = selectionOrder++,
                )
            }
        }
    }

    fun deselectAllNotes() {
        mutableNotes.value = notes.value.map {
            it.map { model ->
                model.copy(isSelected = false, selectionOrder = -1)
            }
        }
    }

    fun mergeSelectedNotes() = viewModelScope.launch {
        val title = selectedNotes.joinToString(LineSeparator) { it.note.title }.trim()
        val body = selectedNotes.joinToString(LineSeparator) { it.note.body }.trim()
        val isPinned = selectedNotes.any { it.note.isPinned }
        val labels = selectedNotes.map { it.labels }.flatten()
        val note =
            Note(folderId = folderId, title = title, body = body, isPinned = isPinned, position = 0)
        val noteId = noteRepository.createNote(note)
        labels.forEach { label ->
            launch {
                val noteLabel = NoteLabel(noteId = noteId, labelId = label.id)
                noteLabelRepository.createNoteLabel(noteLabel)
            }
        }
    }

    fun pinSelectedNotes() = viewModelScope.launch {
        selectedNotes.forEach { model ->
            launch {
                noteRepository.updateNote(model.note.copy(isPinned = true))
            }
        }
    }

    fun unpinSelectedNotes() = viewModelScope.launch {
        selectedNotes.forEach { model ->
            launch {
                noteRepository.updateNote(model.note.copy(isPinned = false))
            }
        }
    }

    fun archiveSelectedNotes() = viewModelScope.launch {
        selectedNotes.forEach { model ->
            launch {
                noteRepository.updateNote(model.note.copy(isArchived = true, reminderDate = null))
            }
        }
    }

    fun duplicateSelectedNotes() = viewModelScope.launch {
        selectedNotes.forEach { model ->
            launch {
                val noteId = noteRepository.createNote(
                    model.note.copy(
                        id = 0,
                        reminderDate = null,
                        creationDate = Clock.System.now()
                    )
                )
                model.labels.forEach { label ->
                    launch {
                        noteLabelRepository.createNoteLabel(
                            NoteLabel(
                                noteId = noteId,
                                labelId = label.id
                            )
                        )
                    }
                }
            }
        }
    }

    fun moveSelectedNotes(folderId: Long) = viewModelScope.launch {
        selectedNotes.forEach { model ->
            launch {
                noteRepository.updateNote(model.note.copy(folderId = folderId))
                model.labels.forEach { label ->
                    launch {
                        val labelId = labelRepository.getOrCreateLabel(folderId, label)
                        noteLabelRepository.createNoteLabel(
                            NoteLabel(
                                labelId = labelId,
                                noteId = model.note.id
                            )
                        )
                        noteLabelRepository.deleteNoteLabel(model.note.id, label.id)
                    }
                }
            }
        }
    }

    fun copySelectedNotes(folderId: Long) = viewModelScope.launch {
        selectedNotes.forEach { model ->
            val noteId = noteRepository.createNote(
                model.note.copy(
                    id = 0,
                    folderId = folderId,
                    creationDate = Clock.System.now()
                )
            )
            model.labels.forEach { label ->
                launch {
                    val labelId = labelRepository.getOrCreateLabel(folderId, label)
                    noteLabelRepository.createNoteLabel(
                        NoteLabel(
                            labelId = labelId,
                            noteId = noteId
                        )
                    )
                }
            }
        }
    }

    fun deleteSelectedNotes() = viewModelScope.launch {
        selectedNotes.forEach { model ->
            launch {
                noteRepository.deleteNote(model.note)
            }
        }
    }

    fun setCurrentNotePosition(position: Int) {
        currentPosition = position
    }

    fun setIsUserScrolling(isScrolling: Boolean) {
        isUserScrolling = isScrolling
    }

    fun selectLabelForSelectedNotes(id: Long) = viewModelScope.launch {
        selectedNotes.forEach { model ->
            val noteLabel = NoteLabel(noteId = model.note.id, labelId = id)
            launch { noteLabelRepository.createNoteLabel(noteLabel) }
        }
    }

    fun deselectLabelForSelectedNotes(id: Long) = viewModelScope.launch {
        selectedNotes.forEach { model ->
            launch { noteLabelRepository.deleteNoteLabel(model.note.id, labelId = id) }
        }
    }

    fun selectArchivedNote(id: Long) {
        mutableArchivedNotes.value = archivedNotes.value.map {
            val selectionOrder = it.maxOf { it.selectionOrder }.plus(1)
            it.map { model ->
                if (model.note.id == id)
                    model.copy(isSelected = true, selectionOrder = selectionOrder)
                else
                    model
            }
        }
    }

    fun deselectArchivedNote(id: Long) {
        mutableArchivedNotes.value = archivedNotes.value.map {
            it.map { model ->
                if (model.note.id == id)
                    model.copy(isSelected = false, selectionOrder = -1)
                else
                    model
            }
        }
    }

    fun deselectAllArchivedNotes() {
        mutableArchivedNotes.value = archivedNotes.value.map {
            it.map { model ->
                model.copy(isSelected = false, selectionOrder = -1)
            }
        }
    }

    fun unarchiveSelectedArchivedNotes() = viewModelScope.launch {
        selectedArchivedNotes.forEach { model ->
            launch {
                noteRepository.updateNote(model.note.copy(isArchived = false))
            }
        }
    }

    fun deleteSelectedArchivedNotes() = viewModelScope.launch {
        selectedArchivedNotes.forEach { model ->
            launch {
                noteRepository.deleteNote(model.note)
            }
        }
    }

    fun updateFolderNotesView(
        filteringType: FilteringType,
        sortingType: NoteListSortingType,
        sortingOrder: SortingOrder,
        groupingType: Grouping,
        groupingOrder: GroupingOrder,
    ) = viewModelScope.launch {
        folderRepository.updateFolder(
            folder.value.copy(
                filteringType = filteringType,
                sortingType = sortingType,
                sortingOrder = if (sortingType == NoteListSortingType.Manual) SortingOrder.Ascending else sortingOrder,
                grouping = groupingType,
                groupingOrder = groupingOrder,
            )
        )
    }

    private fun List<Pair<NotoColor, Boolean>>.mapTrueIfSameColor(notoColor: NotoColor) =
        map { it.first to (it.first == notoColor) }

    private fun Folder.mapRecursively(allFolders: List<Folder>): Folder {
        val childFolders = allFolders
            .filter { it.parentId == id }
            .map { it.mapRecursively(allFolders) to 0 }
        return copy(folders = childFolders)
    }
}