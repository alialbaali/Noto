package com.noto.app.label

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.FolderRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LabelViewModel(
    private val folderRepository: FolderRepository,
    private val labelRepository: LabelRepository,
    private val folderId: Long,
    private val labelId: Long,
) : ViewModel() {

    val folder = folderRepository.getFolderById(folderId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, Folder(folderId, position = 0))

    val labels = labelRepository.getLabelsByFolderId(folderId)
        .filterNotNull()
        .map { it.sortedBy { it.position } }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val label = labelRepository.getLabelById(labelId)
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, Label(labelId, folderId))

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