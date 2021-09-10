package com.noto.app.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.sortByOrder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
    private val libraryId: Long,
) : ViewModel() {

    private val mutableState = MutableStateFlow(State(Library(libraryId, position = 0)))
    val state get() = mutableState.asStateFlow()

    private val mutableNotoColors = MutableStateFlow(NotoColor.values().associateWith { it == state.value.library.color }.toList())
    val notoColors get() = mutableNotoColors.asStateFlow()

    init {
        combine(
            libraryRepository.getLibraryById(libraryId)
                .filterNotNull(),
            noteRepository.getNotesByLibraryId(libraryId)
                .filterNotNull(),
            noteRepository.getArchivedNotesByLibraryId(libraryId)
                .filterNotNull(),
            storage.get(Constants.FontKey)
                .filterNotNull()
                .map { Font.valueOf(it) },
        ) { library, notes, archivedNotes, font ->
            State(
                library,
                notes.sorted(library.sorting, library.sortingOrder),
                archivedNotes.sorted(library.sorting, library.sortingOrder),
                font
            )
        }.onEach {
            mutableState.value = it
            mutableNotoColors.value = mutableNotoColors.value.mapTrueIfSameColor(it.library.color)
        }.launchIn(viewModelScope)
    }

    fun createOrUpdateLibrary(title: String, notePreviewSize: Int, isShowNoteCreationDate: Boolean, isSetNewNoteCursorOnTitle: Boolean) =
        viewModelScope.launch {
            val color = notoColors.value.first { it.second }.first

            val library = state.value.library.copy(
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
        libraryRepository.deleteLibrary(state.value.library)
    }

    fun toggleLibraryIsArchived() = viewModelScope.launch {
        libraryRepository.updateLibrary(state.value.library.copy(isArchived = !state.value.library.isArchived))
    }

    fun toggleLibraryIsPinned() = viewModelScope.launch {
        libraryRepository.updateLibrary(state.value.library.copy(isPinned = !state.value.library.isPinned))
    }

    fun updateLayoutManager(value: LayoutManager) = viewModelScope.launch {
        libraryRepository.updateLibrary(state.value.library.copy(layoutManager = value))
    }

    fun updateNotePosition(note: Note, position: Int) = viewModelScope.launch {
        noteRepository.updateNote(note.copy(position = position))
    }

    fun updateSorting(value: NoteListSorting) = viewModelScope.launch {
        libraryRepository.updateLibrary(state.value.library.copy(sorting = value))
    }

    fun updateSortingOrder(value: SortingOrder) = viewModelScope.launch {
        libraryRepository.updateLibrary(state.value.library.copy(sortingOrder = value))
    }

    fun searchNotes(term: String) = viewModelScope.launch {
        val currentNotes = noteRepository.getNotesByLibraryId(libraryId)
            .first()

        mutableState.value = state.value.copy(
            notes = if (term.isBlank())
                currentNotes
            else
                currentNotes
                    .filter { it.title.contains(term, ignoreCase = true) || it.body.contains(term, ignoreCase = true) }
        )
    }

    fun selectNotoColor(notoColor: NotoColor) {
        mutableNotoColors.value = mutableNotoColors.value
            .mapTrueIfSameColor(notoColor)
    }

    private fun List<Pair<NotoColor, Boolean>>.mapTrueIfSameColor(notoColor: NotoColor) = map { it.first to (it.first == notoColor) }

    private fun List<Note>.sorted(sorting: NoteListSorting, sortingOrder: SortingOrder) = sortByOrder(sortingOrder) { note ->
        when (sorting) {
            NoteListSorting.Manual -> note.position
            NoteListSorting.CreationDate -> note.creationDate
            NoteListSorting.Alphabetical -> note.title.ifBlank { note.body }
        }
    }

    data class State(
        val library: Library,
        val notes: List<Note> = emptyList(),
        val archivedNotes: List<Note> = emptyList(),
        val font: Font = Font.Nunito,
    )
}