package com.noto.library

import androidx.core.content.edit
import androidx.lifecycle.*
import com.noto.domain.interactor.library.LibraryUseCases
import com.noto.domain.interactor.noto.NotoUseCases
import com.noto.domain.model.Library
import com.noto.domain.model.Noto
import com.tfcporciuncula.flow.FlowSharedPreferences
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val LAYOUT_MANAGER_KEY = "Library_Layout_Manager"

class LibraryViewModel(private val libraryUseCases: LibraryUseCases, private val notoUseCases: NotoUseCases, private val storage: FlowSharedPreferences) : ViewModel() {

    private val _library = MutableLiveData<Library>()
    val library: LiveData<Library> = _library

    private var _notos = MutableLiveData<List<Noto>>()
    val notos: LiveData<List<Noto>> = _notos

    val layoutManager = liveData<Int> {
        val flow = storage.getInt(LAYOUT_MANAGER_KEY, LINEAR_LAYOUT_MANAGER).asFlow()
        emitSource(flow.asLiveData())
    }

    fun getNotos(libraryId: Long) {
        viewModelScope.launch {
            notoUseCases.getNotos(libraryId).onSuccess {
                it.collect { list ->
                    _notos.postValue(list)
                }
            }
        }
    }

    fun getArchivedNotos(libraryId: Long) = viewModelScope.launch {

        notoUseCases.getArchivedNotos().onSuccess { flow ->
            flow.collect {
                if (libraryId == 0L) _notos.postValue(it) else _notos.postValue(it.filter { it.libraryId == libraryId })
            }
        }

    }

    fun getAllNotos() = viewModelScope.launch {
        notoUseCases.getAllNotos().onSuccess { flow ->
            flow.collect {
                _notos.postValue(it)
            }
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


    fun setLayoutManager(value: Int) = storage.sharedPreferences.edit { putInt(LAYOUT_MANAGER_KEY, value) }
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