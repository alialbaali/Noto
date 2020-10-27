package com.noto.app.noto

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.noto.app.util.asLiveData
import com.noto.app.util.isValid
import com.noto.app.util.notifyObserver
import com.noto.domain.model.Label
import com.noto.domain.model.Library
import com.noto.domain.model.Noto
import com.noto.domain.repository.LibraryRepository
import com.noto.domain.repository.NotoRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class NotoViewModel(private val libraryRepository: LibraryRepository, private val notoRepository: NotoRepository) : ViewModel() {

    private val _noto = MutableLiveData<Noto>()
    val noto = _noto.asLiveData().distinctUntilChanged()

    private val _library = MutableLiveData<Library>()
    val library = _library.asLiveData()

    private val _labels = MutableLiveData<MutableSet<Label>>()
    val labels = _labels.asLiveData()

    fun getNotoById(notoId: Long) = viewModelScope.launch {

        notoRepository.getNotoById(notoId).collect { value ->
            _noto.postValue(value)
        }

//        notoUseCases.getNotoWithLabels(notoId).onSuccess { flow ->
//            flow.collect { notoWithLabels ->
//                _noto.postValue(notoWithLabels.noto)
//                _labels.postValue(notoWithLabels.labels.toMutableSet())
//            }
//        }

    }

    fun getLibraryById(libraryId: Long) = viewModelScope.launch {
        libraryRepository.getLibraryById(libraryId).collect { value ->
            _library.postValue(value)
        }
    }

    fun postNoto(libraryId: Long) = viewModelScope.launch {

        notoRepository.getNotosByLibraryId(libraryId).collect { value ->
            _noto.postValue(Noto(libraryId = libraryId, notoPosition = value.count()))
        }

//        _labels.postValue(mutableSetOf())

    }

    fun setNotoReminder(zonedDateTime: ZonedDateTime?) {
        _noto.value?.notoReminder = zonedDateTime
        _noto.value = _noto.value
    }

    fun createNoto() = viewModelScope.launch {
        if (noto.value?.isValid() == true) notoRepository.createNoto(noto.value!!)
    }

    fun updateNoto() = viewModelScope.launch {
        if (noto.value?.isValid() == true) notoRepository.updateNoto(noto.value!!) else deleteNoto()
    }

    fun setArchived(value: Boolean) {
        noto.value?.notoIsArchived = value
    }

    fun deleteNoto() = viewModelScope.launch { notoRepository.deleteNoto(noto.value!!) }

    fun createNotoWithLabels() = viewModelScope.launch {
        if (noto.value?.isValid() == true) notoRepository.createNotoWithLabels(noto.value!!, labels.value!!)
    }

    fun notifyLabelsObserver() = _labels.notifyObserver()

    fun setNotoTitle(title: String) {
        _noto.value = _noto.value?.copy(notoTitle = title)
    }

    fun setNotoBody(body: String) {
        _noto.value = _noto.value?.copy(notoBody = body)
    }

}