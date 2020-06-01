package com.noto.noto

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.domain.interactor.label.LabelUseCases
import com.noto.domain.interactor.library.LibraryUseCases
import com.noto.domain.interactor.noto.NotoUseCases
import com.noto.domain.model.Label
import com.noto.domain.model.Library
import com.noto.domain.model.Noto
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch

class NotoViewModel(private val libraryUseCases: LibraryUseCases, private val notoUseCases: NotoUseCases, private val labelUseCases: LabelUseCases) : ViewModel() {

    private val _noto = MutableLiveData<Noto>()
    val noto: LiveData<Noto> = _noto

    val _notoLabels = MutableLiveData<MutableList<Label>>()
    val notoLabels: LiveData<MutableList<Label>> = _notoLabels

    private val _library = MutableLiveData<Library>()
    val library: LiveData<Library> = _library

    private val _labels = MutableLiveData<List<Label>>()
    val labels: LiveData<List<Label>> = _labels

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _navigate = MutableLiveData<Boolean>()
    val navigate: LiveData<Boolean> = _navigate

    fun getNoto(libraryId: Long, notoId: Long) {

        viewModelScope.launch {

            if (notoId == 0L) {
                // Supply the noto position
                _noto.postValue(Noto(libraryId = libraryId, notoPosition = 0))

                _notoLabels.postValue(mutableListOf())

            } else {

                notoUseCases.getNoto(notoId).onSuccess {
                    _noto.postValue(it.first.first())
                    _notoLabels.postValue(it.second)
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
                    notoUseCases.createNoto(noto.value!!, notoLabels.value!!)
                } else {
                    notoUseCases.updateNoto(noto.value!!, notoLabels.value!!)
                }
            }
            navigate(true)
        }
    }

    fun navigate(value: Boolean) {
        _navigate.value = value
    }

    fun getLabels() {
        viewModelScope.launch {
            _labels.postValue(labelUseCases.getLabels().getOrThrow())
        }
    }

    fun deleteNoto() {
        viewModelScope.launch {
            notoUseCases.deleteNoto(noto.value!!, notoLabels.value!!)
        }
    }
}