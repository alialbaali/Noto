package com.noto.app.notelist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.model.NotoColor
import com.noto.app.domain.model.NotoIcon
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

    fun createOrUpdateLibrary(title: String, notoColor: NotoColor, notoIcon: NotoIcon) = viewModelScope.launch {
        val library = library.value.copy(
            title = title,
            color = notoColor,
            icon = notoIcon,
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

    fun toggleNoteStar(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note.copy(isStarred = !note.isStarred))
    }

}