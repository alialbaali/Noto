package com.noto.label

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.domain.interactor.label.LabelUseCases
import com.noto.domain.model.Label
import com.noto.domain.repository.LabelRepository
import kotlinx.coroutines.launch

class LabelListViewModel(private val labelUseCases: LabelUseCases) : ViewModel() {

    private val _labels = MutableLiveData<List<Label>>()
    val labels: LiveData<List<Label>> = _labels

    init {
        getLabels()
    }

    private fun getLabels() {
        viewModelScope.launch {
            _labels.postValue(labelUseCases.getLabels().getOrThrow())
        }
    }

    fun saveLabel(label: Label) {
        viewModelScope.launch {
            if (label.labelId == 0L) {
                labelUseCases.createLabel(label)
            } else {
                labelUseCases.updateLabel(label)
            }
            _labels.postValue(labelUseCases.getLabels().getOrThrow())
        }
    }

    fun deleteLabel(label: Label) {
        viewModelScope.launch {
            labelUseCases.deleteLabel(label)
            _labels.postValue(labelUseCases.getLabels().getOrThrow())
        }
    }
    fun updateLabels(){
        viewModelScope.launch {
        _labels.postValue(labelUseCases.getLabels().getOrThrow())
        }
    }
}