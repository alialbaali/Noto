package com.noto.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.domain.Library
import com.noto.domain.Noto
import com.noto.repository.LibraryRepository
import com.noto.repository.NotoRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LibraryViewModel(private val libraryRepository: LibraryRepository, private val notoRepository: NotoRepository) : ViewModel() {

    private val _library = MutableLiveData<Library>()
    val library: LiveData<Library> = _library

    private val _notos = MutableLiveData<List<Noto>>()
    val notos: LiveData<List<Noto>> = _notos

    fun getNotos(libraryId: Long) {
        viewModelScope.launch {
            _notos.postValue(notoRepository.getNotos(libraryId))
        }
    }

    fun getLibrary(libraryId: Long) {
        viewModelScope.launch {
            _library.postValue(libraryRepository.getLibraryById(libraryId))
        }
    }

    fun countNotos(libraryId: Long): Int {
        return runBlocking {
            notoRepository.countLibraryNotos(libraryId)
        }
    }
}