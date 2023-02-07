package com.noto.app.filtered

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.domain.repository.*
import com.noto.app.folder.NoteItemModel
import com.noto.app.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

typealias NotesGroupedByFolder = Map<Folder, List<NoteItemModel>>
typealias NotesGroupedByDate = Map<LocalDate, List<Pair<Folder, NoteItemModel>>>

class FilteredViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
    private val filteredItemModel: FilteredItemModel,
) : ViewModel() {

    private val mutableNotesGroupedByFolder = MutableStateFlow<UiState<NotesGroupedByFolder>>(UiState.Loading)
    val notesGroupedByFolder get() = mutableNotesGroupedByFolder.asStateFlow()

    private val mutableNotesGroupedByDate = MutableStateFlow<UiState<NotesGroupedByDate>>(UiState.Loading)
    val notesGroupedByDate get() = mutableNotesGroupedByDate.asStateFlow()

    private val mutableNotesGroupedByFolderVisibility = MutableStateFlow(emptyMap<Folder, Boolean>())
    val notesGroupedByFolderVisibility get() = mutableNotesGroupedByFolderVisibility.asStateFlow()

    private val mutableNotesGroupedByDateVisibility = MutableStateFlow(emptyMap<LocalDate, Boolean>())
    val notesGroupedByDateVisibility get() = mutableNotesGroupedByDateVisibility.asStateFlow()

    val font = settingsRepository.font
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    private val mutableIsSearchEnabled = MutableStateFlow(false)
    val isSearchEnabled get() = mutableIsSearchEnabled.asStateFlow()

    private val mutableSearchTerm = MutableStateFlow("")
    val searchTerm get() = mutableSearchTerm.asStateFlow()

    val isRememberScrollingPosition = settingsRepository.isRememberScrollingPosition
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val scrollingPosition = settingsRepository.allNotesScrollingPosition
        .stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    val quickExit = settingsRepository.quickExit
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        combine(
            folderRepository.getAllUnvaultedFolders(),
            noteRepository.getAllNotes(),
            labelRepository.getAllLabels(),
            noteLabelRepository.getNoteLabels(),
            searchTerm,
        ) { folders, notes, labels, noteLabels, searchTerm ->
            when (filteredItemModel) {
                FilteredItemModel.All -> {
                    mutableNotesGroupedByFolderVisibility.value = folders.associateWith { notesGroupedByFolderVisibility.value[it] ?: true }
                    mutableNotesGroupedByFolder.value = notes
                        .filter { note -> folders.any { folder -> folder.id == note.folderId } && !note.isArchived }
                        .mapToNoteItemModel(labels, noteLabels)
                        .filterContent(searchTerm)
                        .groupBy { model ->
                            folders.firstOrNull { folder ->
                                folder.id == model.note.folderId
                            }
                        }
                        .filterNotNullKeys()
                        .filterValues { it.isNotEmpty() }
                        .mapValues { it.value.sorted(it.key.sortingType, it.key.sortingOrder) }
                        .toList()
                        .sortedBy { it.first.position }
                        .sortedByDescending { it.first.isGeneral }
                        .toMap()
                        .let { UiState.Success(it) }
                }
                FilteredItemModel.Recent -> {
                    mutableNotesGroupedByDateVisibility.value = notes
                        .map { it.accessDate.toLocalDate() }
                        .associateWith { notesGroupedByDateVisibility.value[it] ?: true }
                    mutableNotesGroupedByDate.value = notes
                        .filter { note -> folders.any { folder -> folder.id == note.folderId } && note.isRecent }
                        .mapToNoteItemModel(labels, noteLabels)
                        .filterContent(searchTerm)
                        .map { model -> folders.first { it.id == model.note.folderId } to model }
                        .groupBy { pair -> pair.second.note.accessDate.toLocalDate() }
                        .filterValues { it.isNotEmpty() }
                        .mapValues { it.value.sortedByDescending { it.second.note.accessDate } }
                        .toSortedMap(compareByDescending { it })
                        .let { UiState.Success(it) }
                }
                FilteredItemModel.Scheduled -> {
                    mutableNotesGroupedByDateVisibility.value = notes
                        .mapNotNull { it.reminderDate?.toLocalDate() }
                        .associateWith { notesGroupedByDateVisibility.value[it] ?: true }
                    mutableNotesGroupedByDate.value = notes
                        .filter { note -> folders.any { folder -> folder.id == note.folderId } && note.reminderDate != null }
                        .mapToNoteItemModel(labels, noteLabels)
                        .filterContent(searchTerm)
                        .map { model -> folders.first { it.id == model.note.folderId } to model }
                        .groupBy { pair -> pair.second.note.accessDate.toLocalDate() }
                        .filterValues { it.isNotEmpty() }
                        .mapValues { it.value.sortedByDescending { it.second.note.accessDate } }
                        .toSortedMap(compareByDescending { it })
                        .let { UiState.Success(it) }
                }
                FilteredItemModel.Archived -> {
                    mutableNotesGroupedByFolderVisibility.value = folders.associateWith { notesGroupedByFolderVisibility.value[it] ?: true }
                    mutableNotesGroupedByFolder.value = notes
                        .filter { note -> folders.any { folder -> folder.id == note.folderId } && note.isArchived }
                        .mapToNoteItemModel(labels, noteLabels)
                        .filterContent(searchTerm)
                        .groupBy { model ->
                            folders.firstOrNull { folder ->
                                folder.id == model.note.folderId
                            }
                        }
                        .filterNotNullKeys()
                        .filterValues { it.isNotEmpty() }
                        .mapValues { it.value.sorted(it.key.sortingType, it.key.sortingOrder) }
                        .toList()
                        .sortedBy { it.first.position }
                        .sortedByDescending { it.first.isGeneral }
                        .toMap()
                        .let { UiState.Success(it) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun toggleVisibilityForFolder(folderId: Long) {
        mutableNotesGroupedByFolderVisibility.value = notesGroupedByFolderVisibility.value.mapValues {
            if (it.key.id == folderId)
                !it.value
            else
                it.value
        }
    }

    fun toggleVisibilityForDate(date: LocalDate) {
        mutableNotesGroupedByDateVisibility.value = notesGroupedByDateVisibility.value.mapValues {
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
        when (filteredItemModel) {
            FilteredItemModel.All, FilteredItemModel.Archived -> {
                mutableNotesGroupedByFolderVisibility.value = notesGroupedByFolderVisibility.value.mapValues { true }
            }
            FilteredItemModel.Recent, FilteredItemModel.Scheduled -> {
                mutableNotesGroupedByDateVisibility.value = notesGroupedByDateVisibility.value.mapValues { true }
            }
        }
    }

    fun collapseAll() {
        when (filteredItemModel) {
            FilteredItemModel.All, FilteredItemModel.Archived -> {
                mutableNotesGroupedByFolderVisibility.value = notesGroupedByFolderVisibility.value.mapValues { false }
            }
            FilteredItemModel.Recent, FilteredItemModel.Scheduled -> {
                mutableNotesGroupedByDateVisibility.value = notesGroupedByDateVisibility.value.mapValues { false }
            }
        }
    }

    fun updateScrollingPosition(scrollingPosition: Int) = viewModelScope.launch {
        settingsRepository.updateAllNotesScrollingPosition(scrollingPosition)
    }
}
