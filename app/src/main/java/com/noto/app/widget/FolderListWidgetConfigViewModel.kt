package com.noto.app.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Icon
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FolderListWidgetConfigViewModel(
    private val appWidgetId: Int,
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val folders = folderRepository.getFolders()
        .combine(noteRepository.getFolderNotesCount()) { folders, foldersNotesCount ->
            folders.map { folder ->
                val notesCount = foldersNotesCount.firstOrNull { it.folderId == folder.id }?.notesCount ?: 0
                folder to notesCount
            }
        }
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

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

    private val mutableIsNotesCountEnabled = MutableStateFlow(true)
    val isNotesCountEnabled get() = mutableIsNotesCountEnabled.asStateFlow()

    private val mutableWidgetRadius = MutableStateFlow(16)
    val widgetRadius get() = mutableWidgetRadius.asStateFlow()

    val icon = settingsRepository.icon
        .stateIn(viewModelScope, SharingStarted.Eagerly, Icon.Futuristic)

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

        settingsRepository.getWidgetNotesCount(appWidgetId)
            .onEach { mutableIsNotesCountEnabled.value = it }
            .launchIn(viewModelScope)

        settingsRepository.getWidgetRadius(appWidgetId)
            .onEach { mutableWidgetRadius.value = it }
            .launchIn(viewModelScope)
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

    fun setIsNotesCountEnabled(value: Boolean) {
        mutableIsNotesCountEnabled.value = value
    }

    fun createOrUpdateWidget() = viewModelScope.launch {
        launch { settingsRepository.updateIsWidgetCreated(appWidgetId, true) }
        launch { settingsRepository.updateIsWidgetHeaderEnabled(appWidgetId, isWidgetHeaderEnabled.value) }
        launch { settingsRepository.updateIsWidgetEditButtonEnabled(appWidgetId, isEditWidgetButtonEnabled.value) }
        launch { settingsRepository.updateIsWidgetAppIconEnabled(appWidgetId, isAppIconEnabled.value) }
        launch { settingsRepository.updateIsWidgetNewItemButtonEnabled(appWidgetId, isNewFolderButtonEnabled.value) }
        launch { settingsRepository.updateWidgetNotesCount(appWidgetId, isNotesCountEnabled.value) }
        launch { settingsRepository.updateWidgetRadius(appWidgetId, widgetRadius.value) }
    }
}