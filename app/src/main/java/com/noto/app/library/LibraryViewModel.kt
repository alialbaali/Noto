package com.noto.app.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val storage: LocalStorage,
    private val libraryId: Long,
) : ViewModel() {

    val library = libraryRepository.getLibraryById(libraryId)
        .filterNotNull()
        .onEach { library -> mutableNotoColors.value = notoColors.value.mapTrueIfSameColor(library.color) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Library(libraryId, position = 0))

    private val mutableNotes = MutableStateFlow(emptyList<Note>())
    val notes get() = mutableNotes.asStateFlow()

    val archivedNotes = noteRepository.getArchivedNotesByLibraryId(libraryId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val labels = labelRepository.getLabelsByLibraryId(libraryId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val font = storage.get(Constants.FontKey)
        .filterNotNull()
        .map { Font.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    private val mutableNotoColors = MutableStateFlow(NotoColor.values().associateWith { false }.toList())
    val notoColors get() = mutableNotoColors.asStateFlow()

    init {
        noteRepository.getNotesByLibraryId(libraryId)
            .filterNotNull()
            .onEach { mutableNotes.value = it }
            .launchIn(viewModelScope)
    }

    fun createOrUpdateLibrary(title: String, notePreviewSize: Int, isShowNoteCreationDate: Boolean, isSetNewNoteCursorOnTitle: Boolean) =
        viewModelScope.launch {
            val color = notoColors.value.first { it.second }.first

            val library = library.value.copy(
                title = title.trim(),
                color = color,
                notePreviewSize = notePreviewSize,
                isShowNoteCreationDate = isShowNoteCreationDate,
                isSetNewNoteCursorOnTitle = isSetNewNoteCursorOnTitle,
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
        libraryRepository.updateLibrary(library.value.copy(isArchived = !library.value.isArchived))
    }

    fun toggleLibraryIsPinned() = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(isPinned = !library.value.isPinned))
    }

    fun updateLayoutManager(value: LayoutManager) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(layoutManager = value))
    }

    fun updateNotePosition(note: Note, position: Int) = viewModelScope.launch {
        noteRepository.updateNote(note.copy(position = position))
    }

    fun updateSorting(value: NoteListSorting) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(sorting = value))
    }

    fun updateSortingOrder(value: SortingOrder) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value.copy(sortingOrder = value))
    }

    fun searchNotes(term: String) = viewModelScope.launch {
        val currentNotes = noteRepository.getNotesByLibraryId(libraryId)
            .first()

        mutableNotes.value = if (term.isBlank())
            currentNotes
        else
            currentNotes
                .filter { it.title.contains(term, ignoreCase = true) || it.body.contains(term, ignoreCase = true) }
    }

    fun selectNotoColor(notoColor: NotoColor) {
        mutableNotoColors.value = mutableNotoColors.value
            .mapTrueIfSameColor(notoColor)
    }

    private fun List<Pair<NotoColor, Boolean>>.mapTrueIfSameColor(notoColor: NotoColor) = map { it.first to (it.first == notoColor) }
}