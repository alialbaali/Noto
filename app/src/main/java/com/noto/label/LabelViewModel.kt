package com.noto.label

import androidx.lifecycle.*
import com.noto.domain.interactor.label.LabelUseCases
import com.noto.domain.model.Label
import com.noto.util.asLiveData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class SharedLabelViewModel(private val labelUseCases: LabelUseCases) : ViewModel() {

    val labels = liveData<List<Label>> {

        labelUseCases.getLabels().onSuccess { flow ->
            emitSource(flow.asLiveData())
        }

    }

    private val _label = MutableLiveData<Label>()
    val label = _label.asLiveData()

    fun getLabelById(labelId: Long) = viewModelScope.launch {

        labelUseCases.getLabelById(labelId).onSuccess { flow ->
            flow.collect {
                _label.postValue(it)
            }
        }

    }

    fun saveLabel(label: Label) = viewModelScope.launch {

        if (label.labelId == 0L) labelUseCases.createLabel(label) else labelUseCases.updateLabel(label)

    }

    fun deleteLabel(labelId: Long) = viewModelScope.launch { labelUseCases.deleteLabel(labelId) }

}