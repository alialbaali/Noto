package com.noto.app.library

import androidx.lifecycle.*
import com.noto.app.util.LayoutManager
import com.noto.domain.local.LocalStorage
import com.noto.domain.model.Library
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import com.noto.domain.repository.LibraryRepository
import com.noto.domain.repository.NotoRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

private const val LAYOUT_MANAGER_KEY = "Library_Layout_Manager"

class LibraryViewModel(private val libraryRepository: LibraryRepository, private val notoRepository: NotoRepository, private val storage: LocalStorage) : ViewModel() {

    private val _library = MutableLiveData<Library>()
    val library: LiveData<Library> = _library.distinctUntilChanged()

    private var _notos = MutableLiveData<List<Noto>>()
    val notos: LiveData<List<Noto>> = _notos

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
        _library.value = _library.value?.copy(libraryTitle = title)
    }

    fun getNotos(libraryId: Long) = viewModelScope.launch {
        notoRepository.getNotosByLibraryId(libraryId).collect { value ->
            _notos.postValue(value)
        }
    }

    fun getArchivedNotos(libraryId: Long) = viewModelScope.launch {
        notoRepository.getArchivedNotosByLibraryId(libraryId).collect { value ->
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
        _library.value = _library.value?.copy(notoColor = notoColor)
    }

    fun setNotoIcon(notoIcon: NotoIcon) {
        _library.value = _library.value?.copy(notoIcon = notoIcon)
    }

    fun postLibrary() = viewModelScope.launch {
        libraryRepository.getLibraries().collect { value ->
            _library.postValue(Library(libraryPosition = value.count()))
        }
    }

    fun toggleNotoStar(noto: Noto) = viewModelScope.launch {
        notoRepository.updateNoto(noto.copy(notoIsStarred = !noto.notoIsStarred))
    }

}