package com.noto.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.domain.Label
import com.noto.repository.LabelRepository
import kotlinx.coroutines.launch

class LabelListViewModel(private val repository: LabelRepository) : ViewModel() {

    private val _labels = MutableLiveData<List<Label>>()
    val labels: LiveData<List<Label>> = _labels

    init {
        getLabels()
    }

    private fun getLabels() {
        viewModelScope.launch {
            _labels.postValue(repository.getLabels())
        }
    }

//    private fun getLabelById(labelId: Label) {
//        viewModelScope.launch {
//
//        }
//    }

    fun saveLabel(label: Label) {
        viewModelScope.launch {
            if (label.labelId == 0L) {
                repository.insertLabel(label)
            } else {
                repository.updateLabel(label)
            }
            _labels.postValue(repository.getLabels())
        }
    }

    fun deleteLabel(label: Label) {
        viewModelScope.launch {
            repository.deleteLabel(label)
            _labels.postValue(repository.getLabels())
        }
    }
}