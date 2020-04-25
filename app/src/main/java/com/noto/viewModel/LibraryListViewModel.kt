package com.noto.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.domain.Library
import com.noto.repository.LibraryRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

class LibraryListViewModel(private val repository: LibraryRepository) : ViewModel() {

    private val _libraries = MutableLiveData<List<Library>>()
    val libraries: LiveData<List<Library>> = _libraries

    fun getLibraries(){
        viewModelScope.launch {
            _libraries.postValue(repository.getLibraries())
        }
    }

    fun saveLibrary(library: Library) {
        viewModelScope.launch {
            repository.insertLibrary(library)
            _libraries.postValue(repository.getLibraries())
        }
    }

    fun countNotos(libraryId:Long): Int{
        return runBlocking {
            repository.countNotos(libraryId)
        }
    }

//    private val _notos = MediatorLiveData<List<Noto>>()
//    val notos: LiveData<List<Noto>> = _notos
//
//    private val _sortType = MutableLiveData<SortType>()
//    val sortType: LiveData<SortType> = _sortType
//
//    private val _sortMethod = MutableLiveData<SortMethod>()
//    val sortMethod: LiveData<SortMethod> = _sortMethod

//    init {
//        getNotebooks()
//        getSortType()
//        getSortMethod()
//    }

//    fun saveNoto(noto: Noto) {
//        viewModelScope.launch {
//
//            if (_notos.value?.any { it.notoId == noto.notoId }!!) {
//
//                repository.updateNoto(noto)
//            } else {
//
//                repository.insertNoto(noto)
//
//                _notos.postValue(sort(repository.getNotos()))
//
//            }
//        }
//    }
//
//    private fun getNotebooks() {
//        viewModelScope.launch(Dispatchers.Default) {
//            _notos.postValue(sort(repository.getNotos()))
//        }
//    }
//
//    private fun sort(notos: List<Noto>): List<Noto> {
//        return if (_sortType.value == SortType.ASC) {
//            notos.sortAsc(_sortMethod.value ?: SortMethod.Custom)
//        } else {
//            notos.sortDesc(_sortMethod.value ?: SortMethod.Custom)
//        }
//    }
//
//    fun deleteNoto(id: Long) {
//        viewModelScope.launch {
//            repository.deleteNoto(id)
//        }
//    }
//
//    fun updateNotebooks(notos: List<Noto>) {
//        viewModelScope.launch {
//            repository.updateNotos(notos)
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
//        getNotebooks()
//    }
//
//    fun updateSortMethod(sortMethod: SortMethod) {
//        repository.updateSortMethod(sortMethod)
//        getSortMethod()
//
//        if (_sortMethod.value != SortMethod.Custom) {
//            getNotebooks()
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
//
}