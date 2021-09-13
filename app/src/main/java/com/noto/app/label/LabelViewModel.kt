package com.noto.app.label

import androidx.lifecycle.*
import com.noto.app.domain.model.Label
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.util.asLiveData
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LabelViewModel(private val labelRepository: LabelRepository) : ViewModel() {

    val labels = liveData<List<Label>> {
        val source = labelRepository.getLabels().asLiveData()
        emitSource(source)
    }

    private val _label = MutableLiveData<Label>()
    val label = _label.asLiveData()

    fun getLabelById(labelId: Long) = viewModelScope.launch {

        labelRepository.getLabel(labelId).collect { value ->
            _label.postValue(value)
        }

    }

    fun saveLabel(label: Label) = viewModelScope.launch {

        if (label.id == 0L) labelRepository.createLabel(label) else labelRepository.updateLabel(label)

    }

    fun deleteLabel(label: Label) = viewModelScope.launch { labelRepository.deleteLabel(label) }

}