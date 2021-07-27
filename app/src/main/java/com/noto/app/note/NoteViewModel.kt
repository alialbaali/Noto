package com.noto.app.note

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.util.asLiveData
import com.noto.app.util.isValid
import com.noto.app.util.notifyObserver
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

class NoteViewModel(private val libraryRepository: LibraryRepository, private val noteRepository: NoteRepository) : ViewModel() {

    private val _note = MutableLiveData<Note>()
    val note = _note.asLiveData().distinctUntilChanged()

    private val _library = MutableLiveData<Library>()
    val library = _library.asLiveData()

    private val _labels = MutableLiveData<MutableSet<Label>>()
    val labels = _labels.asLiveData()

    fun getNoteById(notoId: Long) = viewModelScope.launch {

        noteRepository.getNoteById(notoId).collect { value ->
            _note.postValue(value)
        }

//        notoUseCases.getNotoWithLabels(notoId).onSuccess { flow ->
//            flow.collect { notoWithLabels ->
//                _noto.postValue(notoWithLabels.note)
//                _labels.postValue(notoWithLabels.labels.toMutableSet())
//            }
//        }

    }

    fun getLibraryById(libraryId: Long) = viewModelScope.launch {
        libraryRepository.getLibraryById(libraryId).collect { value ->
            _library.postValue(value)
        }
    }

    fun postNote(libraryId: Long) = viewModelScope.launch {

        noteRepository.getNotesByLibraryId(libraryId).collect { value ->
            _note.postValue(Note(libraryId = libraryId, position = value.count()))
        }

//        _labels.postValue(mutableSetOf())

    }

    fun setNotoReminder(zonedDateTime: ZonedDateTime?) {
        _note.value = _note.value?.copy(reminderDate = zonedDateTime)
    }

    fun createNote() = viewModelScope.launch {
        if (note.value?.isValid() == true) noteRepository.createNote(note.value!!)
    }

    fun updateNote() = viewModelScope.launch {
        if (note.value?.isValid() == true) noteRepository.updateNote(note.value!!) else deleteNoto()
    }

    fun setNotoArchived(value: Boolean) {
        _note.value = _note.value?.copy(isArchived = value)
    }

    fun deleteNoto() = viewModelScope.launch { noteRepository.deleteNote(note.value!!) }

    fun createNotoWithLabels() = viewModelScope.launch {
        if (note.value?.isValid() == true) noteRepository.createNoteWithLabels(note.value!!, labels.value!!)
    }

    fun notifyLabelsObserver() = _labels.notifyObserver()

    fun setNoteTitle(title: String) {
        _note.value = _note.value?.copy(title = title)
    }

    fun setNoteBody(body: String) {
        _note.value = _note.value?.copy(body = body)
    }

    fun toggleNotoStar() {
        _note.value?.let { _note.value = it.copy(isStarred = !it.isStarred) }
    }

}