package com.noto.app.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.UiState
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val storage: LocalStorage,
    private val libraryId: Long,
) : ViewModel() {

    val library = combine(
        libraryRepository.getLibraryById(libraryId)
            .filterNotNull()
            .onStart { emit(Library(libraryId, position = 0)) },
        libraryRepository.getAllLibraries(),
    ) { library, libraries ->
        mutableNotoColors.value = notoColors.value.mapTrueIfSameColor(library.color)
        library.mapRecursively(libraries)
    }.stateIn(viewModelScope, SharingStarted.Lazily, Library(libraryId, position = 0))

    private val mutableNotes = MutableStateFlow<UiState<List<NoteWithLabels>>>(UiState.Loading)
    val notes get() = mutableNotes.asStateFlow()

    private val mutableArchivedNotes = MutableStateFlow<UiState<List<NoteWithLabels>>>(UiState.Loading)
    val archivedNotes get() = mutableArchivedNotes.asStateFlow()

    private val mutableLabels = MutableStateFlow(emptyMap<Label, Boolean>())
    val labels get() = mutableLabels.asStateFlow()

    val font = storage.get(Constants.FontKey)
        .filterNotNull()
        .map { Font.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    private val mutableNotoColors = MutableStateFlow(NotoColor.values().associateWith { false }.toList())
    val notoColors get() = mutableNotoColors.asStateFlow()

    private val mutableIsSearchEnabled = MutableStateFlow(false)
    val isSearchEnabled get() = mutableIsSearchEnabled.asStateFlow()

    private val mutableSearchTerm = MutableStateFlow("")
    val searchTerm get() = mutableSearchTerm.asStateFlow()

    val isCollapseToolbar = storage.getOrNull(Constants.CollapseToolbar)
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    init {
        combine(
            noteRepository.getNotesByLibraryId(libraryId)
                .filterNotNull(),
            noteRepository.getArchivedNotesByLibraryId(libraryId)
                .filterNotNull(),
            labelRepository.getLabelsByLibraryId(libraryId)
                .filterNotNull(),
            noteLabelRepository.getNoteLabels()
                .filterNotNull(),
            searchTerm.map { it.trim() },
        ) { notes, archivedNotes, labels, noteLabels, searchTerm ->
            mutableNotes.value = notes.mapWithLabels(labels, noteLabels).filterContent(searchTerm).let { UiState.Success(it) }
            mutableArchivedNotes.value = archivedNotes.mapWithLabels(labels, noteLabels).let { UiState.Success(it) }
        }.launchIn(viewModelScope)

        labelRepository.getLabelsByLibraryId(libraryId)
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

    fun createOrUpdateLibrary(
        title: String,
        layout: Layout,
        notePreviewSize: Int,
        newNoteCursorPosition: NewNoteCursorPosition,
        isShowNoteCreationDate: Boolean,
    ) = viewModelScope.launch {
        val color = notoColors.value.first { it.second }.first

        val library = library.value.copy(
            title = title.trim(),
            color = color,
            layout = layout,
            notePreviewSize = notePreviewSize,
            isShowNoteCreationDate = isShowNoteCreationDate,
            newNoteCursorPosition = newNoteCursorPosition,
        )

        if (libraryId == 0L)
            libraryRepository.createLibrary(library)
        else
            libraryRepository.updateLibrary(library)
    }

    fun deleteLibrary() = viewModelScope.launch {
        libraryRepository.deleteLibrary(library.value)
    }

    fun toggleLibraryIsArchived() = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(isArchived = !library.value.isArchived, isVaulted = false, parentId = null))
        library.value.libraries.forEachRecursively { entry, _ ->
            launch {
                libraryRepository.updateLibrary(entry.first.copy(isArchived = !entry.first.isArchived, isVaulted = false))
            }
        }
    }

    fun toggleLibraryIsVaulted() = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(isVaulted = !library.value.isVaulted, isArchived = false, parentId = null))
        library.value.libraries.forEachRecursively { entry, _ ->
            launch {
                libraryRepository.updateLibrary(entry.first.copy(isVaulted = !entry.first.isVaulted, isArchived = false))
            }
        }
    }

    fun toggleLibraryIsPinned() = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(isPinned = !library.value.isPinned))
    }

    fun updateNotePosition(note: Note, position: Int) = viewModelScope.launch {
        noteRepository.updateNote(note.copy(position = position))
    }

    fun updateSortingType(value: NoteListSortingType) = viewModelScope.launch {
        if (value == NoteListSortingType.Manual)
            libraryRepository.updateLibrary(library.value.copy(sortingType = value, sortingOrder = SortingOrder.Ascending))
        else
            libraryRepository.updateLibrary(library.value.copy(sortingType = value))
    }

    fun updateSortingOrder(value: SortingOrder) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(sortingOrder = value))
    }

    fun updateGrouping(value: Grouping) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(grouping = value))
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

    fun updateLibraryParentId(libraryId: Long) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(parentId = libraryId))
    }

    private fun List<Pair<NotoColor, Boolean>>.mapTrueIfSameColor(notoColor: NotoColor) = map { it.first to (it.first == notoColor) }

    private fun Library.mapRecursively(allLibraries: List<Library>): Library {
        val childLibraries = allLibraries
            .filter { it.parentId == id }
            .map { it.mapRecursively(allLibraries) to 0 }
        return copy(libraries = childLibraries)
    }
}