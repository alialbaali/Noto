package com.noto.app.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.LayoutManager
import com.noto.app.util.sortByMethod
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

private const val LAYOUT_MANAGER_KEY = "Library_Layout_Manager"

class LibraryViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
    private val libraryId: Long,
) : ViewModel() {

    private val mutableLibrary = MutableStateFlow(Library(position = 0))
    val library get() = mutableLibrary.asStateFlow()

    private val mutableNotes = MutableStateFlow<List<Note>>(emptyList())
    val notes get() = mutableNotes.asStateFlow()

    private val mutableArchivedNotes = MutableStateFlow<List<Note>>(emptyList())
    val archivedNotes get() = mutableArchivedNotes.asStateFlow()

    private val mutableLayoutManager = MutableStateFlow(LayoutManager.Linear)
    val layoutManager get() = mutableLayoutManager.asStateFlow()

    init {
        if (libraryId != 0L)
            libraryRepository.getLibraryById(libraryId)
                .onEach { mutableLibrary.value = it }
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

    fun setLayoutManager(value: LayoutManager) = viewModelScope.launch {
        storage.put(LAYOUT_MANAGER_KEY, value.toString())
    }

    fun setLibraryTitle(title: String) {
        mutableLibrary.value = mutableLibrary.value.copy(title = title)
    }

    fun deleteLibrary() = viewModelScope.launch {
        libraryRepository.deleteLibrary(library.value)
    }

    fun createLibrary() = viewModelScope.launch {
        libraryRepository.createLibrary(library.value)
    }

    fun updateLibrary() = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value)
    }

    fun setNotoColor(notoColor: NotoColor) {
        viewModelScope.launch {
            libraryRepository.updateLibrary(library.value.copy(color = notoColor))
        }
    }

    fun setNotoIcon(notoIcon: NotoIcon) {
        viewModelScope.launch {
            libraryRepository.updateLibrary(library.value.copy(icon = notoIcon))
        }
    }

    fun setSortingMethod(sortingMethod: SortingMethod) {
        mutableLibrary.value = mutableLibrary.value.copy(sortingMethod = sortingMethod)
        sortNotes()
    }

    fun setSortingType(sortingType: SortingType) {
        mutableLibrary.value = mutableLibrary.value.copy(sortingType = sortingType)
        sortNotes()
    }

    fun postLibrary() = viewModelScope.launch {
        libraryRepository.getLibraries().collect { value ->
            mutableLibrary.value = Library(position = value.count())
        }
    }

    fun toggleNoteStar(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note.copy(isStarred = !note.isStarred))
    }

    private fun sortNotes() {

        val sortingType = when (library.value?.sortingType) {
            SortingType.Alphabetically -> Note::title
            SortingType.CreationDate -> Note::creationDate
        }

        mutableNotes.value = mutableNotes.value
            .sortByMethod(library.value.sortingMethod, sortingType)
            .toList()
    }

}