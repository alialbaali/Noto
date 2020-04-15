package com.noto.note.viewModel

import androidx.lifecycle.*
import com.noto.database.SortMethod
import com.noto.database.SortType
import com.noto.note.model.Notebook
import com.noto.note.repository.NotebookRepository
import com.noto.util.sortAsc
import com.noto.util.sortDesc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotebookListViewModel(private val notebookRepository: NotebookRepository) : ViewModel() {

    private val _notebooks = MediatorLiveData<List<Notebook>>()
    val notebooks: LiveData<List<Notebook>> = _notebooks

    private val _sortType = MutableLiveData<SortType>()
    val sortType :LiveData<SortType> = _sortType

    private val _sortMethod = MutableLiveData<SortMethod>()
    val sortMethod :LiveData<SortMethod> = _sortMethod

    init {
        getNotebooks()
        getSortType()
        getSortMethod()
    }

    internal fun saveNotebook(notebook: Notebook) {
        viewModelScope.launch {

            if (_notebooks.value?.any { it.notebookId == notebook.notebookId }!!) {

                notebookRepository.updateNotebook(notebook)
            } else {

                notebookRepository.insertNotebook(notebook)

                _notebooks.postValue(notebookRepository.getNotebooks())

            }
        }
    }

    private fun getNotebooks() {
        viewModelScope.launch(Dispatchers.Default) {
            _notebooks.postValue(sort(notebookRepository.getNotebooks()))
        }
    }

    private fun sort(list: List<Notebook>): List<Notebook> {
        return if (_sortType.value == SortType.ASC) {
            list.sortAsc(_sortMethod.value ?: SortMethod.Custom)
        } else {
            list.sortDesc(_sortMethod.value ?: SortMethod.Custom)
        }
    }

    internal fun deleteNotebook(notebookId: Long) {
        viewModelScope.launch {
            notebookRepository.deleteNotebook(notebookId)
        }
    }

    internal fun swapNotebooks(from: Notebook, to: Notebook) {
        viewModelScope.launch {
            notebookRepository.swapNotebooks(from, to)
        }
    }

    fun updateNotebooks(notebooks: List<Notebook>) {
        viewModelScope.launch {
            notebookRepository.updateNotebooks(notebooks)
        }
    }

    fun updateSortType() {
        if (_sortType.value == SortType.ASC) {
            notebookRepository.updateSortType(SortType.DESC)
        } else {
            notebookRepository.updateSortType(SortType.ASC)
        }
        getSortType()
        getNotebooks()
    }

    fun updateSortMethod(sortMethod: SortMethod) {
        notebookRepository.updateSortMethod(sortMethod)
        getSortMethod()

        if (_sortMethod.value != SortMethod.Custom) {
            getNotebooks()
        }
    }

    private fun getSortType() {
        _sortType.value = notebookRepository.getSortType()
    }

    private fun getSortMethod() {
        _sortMethod.value = notebookRepository.getSortMethod()
    }
}