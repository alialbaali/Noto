package com.noto.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.domain.interactor.library.LibraryUseCases
import com.noto.domain.interactor.noto.NotoUseCases
import com.noto.domain.model.Library
import com.noto.domain.model.Noto
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LibraryViewModel(private val libraryUseCases: LibraryUseCases, private val notoUseCases: NotoUseCases) : ViewModel() {

    private val _library = MutableLiveData<Library>()
    val library: LiveData<Library> = _library

    private var _notos = MutableLiveData<List<Noto>>()
    val notos: LiveData<List<Noto>> = _notos

    fun getNotos(libraryId: Long) {
        viewModelScope.launch {
            notoUseCases.getNotos(libraryId).onSuccess {
                it.collect { list ->
                    _notos.postValue(list)
                }
            }
//            _notos.postValue(sort(notoRepository.getNotos(libraryId)))
        }
    }

    fun getLibraryById(libraryId: Long) {
        viewModelScope.launch {
            libraryUseCases.getLibraryById(libraryId).onSuccess {
                it.collect { library ->
                    _library.postValue(library)
                }
            }
//            _library.postValue(libraryRepository.getLibraryById(libraryId))
        }
    }

    fun countNotos(libraryId: Long): Int {
        return runBlocking {
            libraryUseCases.countNotos(libraryId)
        }
    }

    fun deleteLibrary() {
        viewModelScope.launch {
            libraryUseCases.deleteLibrary(library.value!!)
        }
    }
//
//    private fun sort(notos: List<Noto>): List<Noto> {
//        return if (library.value?.sortType == SortType.ASC) {
//            notos.sortAscNoto(library.value?.sortMethod ?: SortMethod.CreationDate)
//        } else {
//            notos.sortDescNoto(library.value?.sortMethod ?: SortMethod.CreationDate)
//        }
//    }

//    fun updateSortType() {
//        if (library.value!!.sortType == SortType.ASC) {
//            library.value!!.sortType = SortType.DESC
//        } else {
//            library.value!!.sortType = SortType.ASC
//        }
//        viewModelScope.launch {
//            libraryRepository.updateLibrary(library.value!!)
////            _library.postValue(libraryRepository.getLibraryById(library.value!!.libraryId))
//        }
//    }

//    fun updateSortMethod(sortMethod: SortMethod) {
//        library.value!!.sortMethod = sortMethod
//
//        if (library.value!!.sortMethod != SortMethod.Custom) {
//            viewModelScope.launch {
//                libraryRepository.updateLibrary(library.value!!)
////                _library.postValue(libraryRepository.getLibraryById(library.value!!.libraryId))
//            }
//        }
//    }
}