package com.noto.app.notelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.LayoutManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val LAYOUT_MANAGER_KEY = "Library_Layout_Manager"

class NoteListViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
    private val libraryId: Long,
) : ViewModel() {

    private val mutableLibrary = MutableStateFlow(Library(libraryId, position = 0))
    val library get() = mutableLibrary.asStateFlow()

    private val mutableNotes = MutableStateFlow<List<Note>>(emptyList())
    val notes get() = mutableNotes.asStateFlow()

    private val mutableArchivedNotes = MutableStateFlow<List<Note>>(emptyList())
    val archivedNotes get() = mutableArchivedNotes.asStateFlow()

    private val mutableLayoutManager = MutableStateFlow(LayoutManager.Linear)
    val layoutManager get() = mutableLayoutManager.asStateFlow()

    private val mutableNotoColors = MutableStateFlow(NotoColor.values().associateWith { it == library.value.color }.toList())
    val notoColors get() = mutableNotoColors.asStateFlow()

    init {
        if (libraryId != 0L)
            libraryRepository.getLibraryById(libraryId)
                .onEach {
                    mutableLibrary.value = it
                    mutableNotoColors.value = mutableNotoColors.value
                        .mapTrueIfSameColor(it.color)
                }
                .launchIn(viewModelScope)

        noteRepository.getNotesByLibraryId(libraryId)
            .onEach { mutableNotes.value = it }
            .launchIn(viewModelScope)

        noteRepository.getArchivedNotesByLibraryId(libraryId)
            .onEach { mutableArchivedNotes.value = it }
            .launchIn(viewModelScope)

        storage.get(LAYOUT_MANAGER_KEY)
            .map { LayoutManager.valueOf(it) }
            .onEach { mutableLayoutManager.value = it }
            .launchIn(viewModelScope)
    }

    fun createOrUpdateLibrary(title: String) = viewModelScope.launch {
        val color = notoColors.value.first { it.second }.first

        val library = library.value.copy(
            title = title,
            color = color,
        )

        if (libraryId == 0L)
            libraryRepository.createLibrary(library)
        else
            libraryRepository.updateLibrary(library)
    }

    fun deleteLibrary() = viewModelScope.launch {
        libraryRepository.deleteLibrary(library.value)
    }

    fun updateLayoutManager(value: LayoutManager) = viewModelScope.launch {
        storage.put(LAYOUT_MANAGER_KEY, value.toString())
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

    fun updateSortingType(sortingType: SortingType) {
        viewModelScope.launch {
            libraryRepository.updateLibrary(library.value.copy(sortingType = sortingType))
        }
    }

    fun updateSortingMethod(sortingMethod: SortingMethod) {
        viewModelScope.launch {
            libraryRepository.updateLibrary(library.value.copy(sortingMethod = sortingMethod))
        }
    }

    private fun List<Pair<NotoColor, Boolean>>.mapTrueIfSameColor(notoColor: NotoColor) = map { it.first to (it.first == notoColor) }
}