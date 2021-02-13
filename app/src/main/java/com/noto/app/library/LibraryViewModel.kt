package com.noto.app.library

import androidx.lifecycle.*
import com.noto.app.util.LayoutManager
import com.noto.app.util.sortByMethod
import com.noto.domain.local.LocalStorage
import com.noto.domain.model.*
import com.noto.domain.repository.LibraryRepository
import com.noto.domain.repository.NoteRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

private const val LAYOUT_MANAGER_KEY = "Library_Layout_Manager"

class LibraryViewModel(private val libraryRepository: LibraryRepository, private val noteRepository: NoteRepository, private val storage: LocalStorage) : ViewModel() {

    private val _library = MutableLiveData<Library>()
    val library: LiveData<Library> = _library.distinctUntilChanged()

    private var _notos = MutableLiveData<List<Note>>()
    val notos: LiveData<List<Note>> = _notos

    val searchTerm = MutableLiveData("")

    val layoutManager = liveData {
        storage.get(LAYOUT_MANAGER_KEY)
            .mapCatching { flow -> flow.map { LayoutManager.valueOf(it) } }
            .getOrDefault(flowOf(LayoutManager.Linear))
            .onStart { emit(LayoutManager.Linear) }
            .asLiveData()
            .let { emitSource(it) }
    }

    fun setLayoutManager(value: LayoutManager) = viewModelScope.launch {
        storage.put(LAYOUT_MANAGER_KEY, value.toString())
    }

    fun setLibraryTitle(title: String) {
        _library.value = _library.value?.copy(title = title)
    }

    fun getNotes(libraryId: Long) = viewModelScope.launch {
        noteRepository.getNotesByLibraryId(libraryId).collect { value ->
            _notos.postValue(value)
        }
    }

    fun getArchivedNotes(libraryId: Long) = viewModelScope.launch {
        noteRepository.getArchivedNotesByLibraryId(libraryId).collect { value ->
            _notos.postValue(value)
        }
    }

    fun getLibrary(libraryId: Long) = viewModelScope.launch {
        libraryRepository.getLibraryById(libraryId).collect { value ->
            _library.postValue(value)
        }
    }

    fun deleteLibrary() = viewModelScope.launch {
        libraryRepository.deleteLibrary(library.value!!)
    }

    fun createLibrary() = viewModelScope.launch {
        libraryRepository.createLibrary(library.value!!)
    }

    fun updateLibrary() = viewModelScope.launch {
        libraryRepository.updateLibrary(library.value!!)
    }

    fun setNotoColor(notoColor: NotoColor) {
        _library.value = _library.value?.copy(color = notoColor)
    }

    fun setNotoIcon(notoIcon: NotoIcon) {
        _library.value = _library.value?.copy(icon = notoIcon)
    }

    fun setSortingMethod(sortingMethod: SortingMethod) {
        _library.value = _library.value?.copy(sortingMethod = sortingMethod)
        println(sortingMethod)
        sortNotes()
    }

    fun setSortingType(sortingType: SortingType) {
        _library.value = _library.value?.copy(sortingType = sortingType)
        println(sortingType)
        sortNotes()
    }

    fun postLibrary() = viewModelScope.launch {
        libraryRepository.getLibraries().collect { value ->
            _library.postValue(Library(position = value.count()))
        }
    }

    fun toggleNoteStar(note: Note) = viewModelScope.launch {
        noteRepository.updateNote(note.copy(isStarred = !note.isStarred))
    }

    private fun sortNotes() {

        val sortingType = when (library.value?.sortingType) {
            SortingType.Alphabetically -> Note::title
            SortingType.CreationDate -> Note::creationDate
            else -> Note::creationDate
        }

        _notos.value = _notos.value
            ?.sortByMethod(library.value?.sortingMethod ?: SortingMethod.Desc, sortingType)
            ?.toList()

    }

}