package com.noto.app.noto

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

class NotoViewModel(private val libraryUseCases: LibraryUseCases, private val notoUseCases: NotoUseCases) : ViewModel() {

    private val _noto = MutableLiveData<Noto>()
    val noto: LiveData<Noto> = _noto

    private val _library = MutableLiveData<Library>()
    val library: LiveData<Library> = _library

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _navigate = MutableLiveData<Boolean>()
    val navigate: LiveData<Boolean> = _navigate

    fun getNoto(libraryId: Long, notoId: Long) {

        viewModelScope.launch {

            if (notoId == 0L) {

                _noto.postValue(Noto(libraryId = libraryId, notoPosition = notoUseCases.countLibraryNotos(libraryId)))

            } else {
                notoUseCases.getNoto(notoId).onSuccess { flow ->
                    flow.collect { value ->
                        _noto.postValue(value)
                    }
                }.onFailure {
                    _error.postValue(it.message)
                }
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
        }
    }

    fun saveNoto(notoId: Long) {
        viewModelScope.launch {
            if (!(noto.value!!.notoTitle.isBlank() && noto.value!!.notoBody.isBlank())) {
                if (notoId == 0L) {
                    notoUseCases.createNoto(noto.value!!)
                } else {
                    notoUseCases.updateNoto(noto.value!!)
                }
            }
            navigate(true)
        }
    }

    fun navigate(value: Boolean) {
        _navigate.value = value
    }

    fun deleteNoto() {
        viewModelScope.launch {
            notoUseCases.deleteNoto(noto.value!!)
        }
    }
}