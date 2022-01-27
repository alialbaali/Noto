package com.noto.app.label

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LabelViewModel(
    private val libraryRepository: LibraryRepository,
    private val labelRepository: LabelRepository,
    private val libraryId: Long,
    private val labelId: Long,
) : ViewModel() {

    val library = libraryRepository.getLibraryById(libraryId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, Folder(libraryId, position = 0))

    val labels = labelRepository.getLabelsByLibraryId(libraryId)
        .filterNotNull()
        .map { it.sortedBy { it.position } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val label = labelRepository.getLabelById(labelId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, Label(labelId, libraryId))

    fun createOrUpdateLabel(title: String) = viewModelScope.launch {
        val label = label.value.copy(title = title.trim())
        if (labelId == 0L)
            labelRepository.createLabel(label)
        else
            labelRepository.updateLabel(label)
    }

    fun updateLabelPosition(label: Label, position: Int) = viewModelScope.launch {
        labelRepository.updateLabel(label.copy(position = position))
    }

    fun deleteLabel() = viewModelScope.launch {
        labelRepository.deleteLabel(label.value)
    }
}