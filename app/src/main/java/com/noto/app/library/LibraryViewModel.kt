package com.noto.app.library

import androidx.core.content.edit
import androidx.lifecycle.*
import com.noto.domain.model.Library
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoColor
import com.noto.domain.model.NotoIcon
import com.noto.domain.repository.LibraryRepository
import com.noto.domain.repository.NotoRepository
import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

private const val LAYOUT_MANAGER_KEY = "Library_Layout_Manager"

class LibraryViewModel(private val libraryRepository: LibraryRepository, private val notoRepository: NotoRepository, private val storage: FlowSharedPreferences) : ViewModel() {

    private val _library = MutableLiveData<Library>()
    val library: LiveData<Library> = _library

    private var _notos = MutableLiveData<List<Noto>>()
    val notos: LiveData<List<Noto>> = _notos

    val layoutManager = liveData<Int> {
        val flow = storage.getInt(LAYOUT_MANAGER_KEY, LINEAR_LAYOUT_MANAGER).asFlow()
        emitSource(flow.asLiveData())
    }

    fun getNotos(libraryId: Long) = viewModelScope.launch {
        notoRepository.getNotos().collect { value ->
            _notos.postValue(value.filter { noto -> noto.libraryId == libraryId && !noto.notoIsArchived })
        }
    }

    fun getArchivedNotos(libraryId: Long) = viewModelScope.launch {
        notoRepository.getNotos().collect { value ->
            _notos.postValue(value.filter { noto -> noto.libraryId == libraryId && noto.notoIsArchived })
        }
    }

    fun getAllNotos() = viewModelScope.launch {
        notoRepository.getNotos().collect { value ->
            _notos.postValue(value.filter { noto -> !noto.notoIsArchived })
        }
    }

    fun getLibrary(libraryId: Long) = viewModelScope.launch {
        libraryRepository.getLibrary(libraryId).collect { value ->
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

    @ExperimentalCoroutinesApi
    fun setLayoutManager(value: Int) = storage.sharedPreferences.edit { putInt(LAYOUT_MANAGER_KEY, value) }

    fun setNotoColor(notoColor: NotoColor) {
        _library.value?.notoColor = notoColor
    }

    fun setNotoIcon(notoIcon: NotoIcon) {
        _library.value?.notoIcon = notoIcon
    }

    fun postLibrary() = viewModelScope.launch {
        libraryRepository.getLibraries().collect { value ->
            _library.postValue(Library(libraryPosition = value.count()))
        }
    }

}