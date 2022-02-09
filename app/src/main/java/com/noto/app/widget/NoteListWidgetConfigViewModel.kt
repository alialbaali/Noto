package com.noto.app.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Label
import com.noto.app.domain.repository.*
import com.noto.app.util.NoteWithLabels
import com.noto.app.util.mapWithLabels
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteListWidgetConfigViewModel(
    private val appWidgetId: Int,
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    private val mutableFolder = MutableStateFlow(Folder(position = 0))
    val folder get() = mutableFolder.asStateFlow()

    private val mutableNotes = MutableStateFlow(emptyList<NoteWithLabels>())
    val notes get() = mutableNotes.asStateFlow()

    private val mutableLabels = MutableStateFlow(emptyMap<Label, Boolean>())
    val labels get() = mutableLabels.asStateFlow()

    val isWidgetCreated = settingsRepository.getIsWidgetCreated(appWidgetId)
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val mutableIsWidgetHeaderEnabled = MutableStateFlow(true)
    val isWidgetHeaderEnabled get() = mutableIsWidgetHeaderEnabled.asStateFlow()

    private val mutableIsEditWidgetButtonEnabled = MutableStateFlow(true)
    val isEditWidgetButtonEnabled get() = mutableIsEditWidgetButtonEnabled.asStateFlow()

    private val mutableIsAppIconEnabled = MutableStateFlow(true)
    val isAppIconEnabled get() = mutableIsAppIconEnabled.asStateFlow()

    private val mutableIsNewFolderButtonEnabled = MutableStateFlow(true)
    val isNewFolderButtonEnabled get() = mutableIsNewFolderButtonEnabled.asStateFlow()

    private val mutableWidgetRadius = MutableStateFlow(16)
    val widgetRadius get() = mutableWidgetRadius.asStateFlow()

    init {
        settingsRepository.getIsWidgetHeaderEnabled(appWidgetId)
            .onEach { mutableIsWidgetHeaderEnabled.value = it }
            .launchIn(viewModelScope)

        settingsRepository.getIsWidgetEditButtonEnabled(appWidgetId)
            .onEach { mutableIsEditWidgetButtonEnabled.value = it }
            .launchIn(viewModelScope)

        settingsRepository.getIsWidgetAppIconEnabled(appWidgetId)
            .onEach { mutableIsAppIconEnabled.value = it }
            .launchIn(viewModelScope)

        settingsRepository.getIsWidgetNewItemButtonEnabled(appWidgetId)
            .onEach { mutableIsNewFolderButtonEnabled.value = it }
            .launchIn(viewModelScope)

        settingsRepository.getWidgetRadius(appWidgetId)
            .onEach { mutableWidgetRadius.value = it }
            .launchIn(viewModelScope)
    }

    fun getWidgetData(folderId: Long) {
        folderRepository.getFolderById(folderId)
            .filterNotNull()
            .onEach { mutableFolder.value = it }
            .launchIn(viewModelScope)

        combine(
            noteRepository.getNotesByFolderId(folderId)
                .filterNotNull(),
            labelRepository.getLabelsByFolderId(folderId)
                .filterNotNull(),
            noteLabelRepository.getNoteLabels()
                .filterNotNull()
        ) { notes, labels, noteLabels ->
            mutableNotes.value = notes.mapWithLabels(labels, noteLabels)
        }.launchIn(viewModelScope)

        combine(
            labelRepository.getLabelsByFolderId(folderId)
                .filterNotNull(),
            settingsRepository.getWidgetSelectedLabelIds(appWidgetId, folderId)
                .onStart { emit(emptyList()) },
        ) { labels, labelIds ->
            mutableLabels.value = labels.sortedBy { it.position }.map { label ->
                val value = labelIds.any { it == label.id }
                label to value
            }.toMap()
        }.launchIn(viewModelScope)
    }

    fun selectLabel(id: Long) {
        mutableLabels.value = labels.value
            .map { it.key to if (it.value) true else (it.key.id == id) }
            .toMap()
    }

    fun deselectLabel(id: Long) {
        mutableLabels.value = labels.value
            .map { it.key to if (it.key.id == id) false else it.value }
            .toMap()
    }

    fun setIsWidgetHeaderEnabled(value: Boolean) {
        mutableIsWidgetHeaderEnabled.value = value
    }

    fun setIsEditWidgetButtonEnabled(value: Boolean) {
        mutableIsEditWidgetButtonEnabled.value = value
    }

    fun setIsAppIconEnabled(value: Boolean) {
        mutableIsAppIconEnabled.value = value
    }

    fun setIsNewFolderButtonEnabled(value: Boolean) {
        mutableIsNewFolderButtonEnabled.value = value
    }

    fun setWidgetRadius(value: Int) {
        mutableWidgetRadius.value = value
    }

    fun createOrUpdateWidget() = viewModelScope.launch {
        val labelIds = labels.value.filterValues { it }.map { it.key.id }
        launch { settingsRepository.updateWidgetFolderId(appWidgetId, folder.value.id) }
        launch { settingsRepository.updateIsWidgetCreated(appWidgetId, true) }
        launch { settingsRepository.updateIsWidgetHeaderEnabled(appWidgetId, isWidgetHeaderEnabled.value) }
        launch { settingsRepository.updateIsWidgetEditButtonEnabled(appWidgetId, isEditWidgetButtonEnabled.value) }
        launch { settingsRepository.updateIsWidgetAppIconEnabled(appWidgetId, isAppIconEnabled.value) }
        launch { settingsRepository.updateIsWidgetNewItemButtonEnabled(appWidgetId, isNewFolderButtonEnabled.value) }
        launch { settingsRepository.updateWidgetRadius(appWidgetId, widgetRadius.value) }
        launch { settingsRepository.updateWidgetSelectedLabelIds(appWidgetId, folder.value.id, labelIds) }
    }
}
