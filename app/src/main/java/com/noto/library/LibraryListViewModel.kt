package com.noto.library

import androidx.lifecycle.*
import com.noto.domain.interactor.library.LibraryUseCases
import com.noto.domain.model.Library
import com.noto.domain.model.SortMethod
import com.noto.domain.model.SortType
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LibraryListViewModel(private val libraryUseCases: LibraryUseCases) : ViewModel() {

    private val _libraries = liveData<List<Library>> {
        libraryUseCases.getLibraries().fold({ emitSource(it.asLiveData()) }, { _error.postValue(it.message) })
    }
    val libraries: LiveData<List<Library>> = _libraries

    private val _sortType = MutableLiveData<SortType>()
    val sortType: LiveData<SortType> = _sortType

    private val _sortMethod = MutableLiveData<SortMethod>()
    val sortMethod: LiveData<SortMethod> = _sortMethod

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun saveLibrary(library: Library) {
        viewModelScope.launch {
            libraryUseCases.createLibrary(library)
        }
    }

    fun countNotos(libraryId: Long): Int {
        return runBlocking {
            libraryUseCases.countNotos(libraryId)
        }
    }


    init {
//        getSortType()
//        getSortMethod()
    }


//    private fun sort(libraries: List<Library>): List<Library> {
//        return if (_sortType.value == SortType.ASC) {
//            libraries.sortAsc(_sortMethod.value ?: SortMethod.Custom)
//        } else {
//            libraries.sortDesc(_sortMethod.value ?: SortMethod.Custom)
//        }
//    }

//
//    fun updateNotebooks(libraries: List<Library>) {
//        viewModelScope.launch {
//            repository.updateLibraries(libraries)
//        }
//    }
//
//    fun updateSortType() {
//        if (_sortType.value == SortType.ASC) {
//            repository.updateSortType(SortType.DESC)
//        } else {
//            repository.updateSortType(SortType.ASC)
//        }
//        getSortType()
//        getLibraries()
//    }
//
//    fun updateSortMethod(sortMethod: SortMethod) {
//        repository.updateSortMethod(sortMethod)
//        getSortMethod()
//
//        if (_sortMethod.value != SortMethod.Custom) {
//            getLibraries()
//        }
//    }
//
//    private fun getSortType() {
//        _sortType.value = repository.getSortType()
//    }
//
//    private fun getSortMethod() {
//        _sortMethod.value = repository.getSortMethod()
//    }

}