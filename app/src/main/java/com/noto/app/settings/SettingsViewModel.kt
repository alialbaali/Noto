package com.noto.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.*
import com.noto.app.util.NotoDefaultJson
import com.noto.app.util.hash
import com.noto.app.util.isGeneral
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString

class SettingsViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val theme = settingsRepository.theme
        .stateIn(viewModelScope, SharingStarted.Lazily, Theme.System)

    val font = settingsRepository.font
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    val icon = settingsRepository.icon
        .stateIn(viewModelScope, SharingStarted.Eagerly, Icon.Futuristic)

    val isShowNotesCount = settingsRepository.isShowNotesCount
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isDoNotDisturb = settingsRepository.isDoNotDisturb
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isScreenOn = settingsRepository.isScreenOn
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isFullScreen = settingsRepository.isFullScreen
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val screenBrightnessLevel = settingsRepository.screenBrightnessLevel
        .stateIn(viewModelScope, SharingStarted.Eagerly, ScreenBrightnessLevel.System)

    val vaultPasscode = settingsRepository.vaultPasscode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val vaultTimeout = settingsRepository.vaultTimeout
        .stateIn(viewModelScope, SharingStarted.Lazily, VaultTimeout.Immediately)

    val isBioAuthEnabled = settingsRepository.isBioAuthEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val mainInterfaceId = settingsRepository.mainInterfaceId
        .stateIn(viewModelScope, SharingStarted.Eagerly, Folder.GeneralFolderId)

    val isRememberScrollingPosition = settingsRepository.isRememberScrollingPosition
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    private val mutableIsImportFinished = MutableSharedFlow<Unit>(replay = Int.MAX_VALUE)
    val isImportFinished get() = mutableIsImportFinished.asSharedFlow()

    val quickNoteFolderId = settingsRepository.quickNoteFolderId
        .stateIn(viewModelScope, SharingStarted.Eagerly, Folder.GeneralFolderId)

    val quickExit = settingsRepository.quickExit
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val continuousSearch = settingsRepository.continuousSearch
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    val previewAutoScroll = settingsRepository.previewAutoScroll
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    fun toggleShowNotesCount() = viewModelScope.launch {
        settingsRepository.updateIsShowNotesCount(!isShowNotesCount.value)
    }

    fun toggleDoNotDisturb() = viewModelScope.launch {
        settingsRepository.updateIsDoNotDisturb(!isDoNotDisturb.value)
    }

    fun toggleScreenOn() = viewModelScope.launch {
        settingsRepository.updateIsScreenOn(!isScreenOn.value)
    }

    fun toggleFullScreen() = viewModelScope.launch {
        settingsRepository.updateIsFullScreen(!isFullScreen.value)
    }

    fun updateScreenBrightnessLevel(level: ScreenBrightnessLevel) = viewModelScope.launch {
        settingsRepository.updateScreenBrightnessLevel(level)
    }

    suspend fun exportJson(): String = withContext(Dispatchers.IO) {
        val folders = folderRepository.getAllFolders().first()
        val notes = noteRepository.getAllNotes().first()
        val labels = labelRepository.getAllLabels().first()
        val noteLabels = noteLabelRepository.getNoteLabels().first()
        val settings = settingsRepository.config.first()
        val data = NotoData(folders, notes, labels, noteLabels, settings)
        NotoDefaultJson.encodeToString(data)
    }

    suspend fun importJson(json: String) = withContext(Dispatchers.IO) {
        val folderIds = mutableMapOf<Long, Long>()
        val noteIds = mutableMapOf<Long, Long>()
        val labelIds = mutableMapOf<Long, Long>()
        val data = NotoDefaultJson.decodeFromString<NotoData>(json)
        data.apply {
            folders.forEach { folder ->
                if (folder.isGeneral) {
                    folderRepository.updateFolder(folder)
                    folderIds[folder.id] = Folder.GeneralFolderId
                } else {
                    val parentFolder = folders.firstOrNull { it.id == folder.parentId }
                    val parentId = folderIds.getOrDefault(parentFolder?.id ?: 0L, 0L).takeUnless { it == 0L }
                    val newFolderId = folderRepository.createFolder(folder.copy(id = 0, parentId = parentId), overridePosition = false)
                    folderIds[folder.id] = newFolderId
                }
            }
            notes.forEach { note ->
                val folderId = folderIds.getValue(note.folderId)
                val newNoteId = noteRepository.createNote(note.copy(id = 0, folderId = folderId), overridePosition = false)
                noteIds[note.id] = newNoteId
            }
            labels.forEach { label ->
                val folderId = folderIds.getValue(label.folderId)
                val newLabelId = labelRepository.createLabel(label.copy(id = 0, folderId = folderId), overridePosition = false)
                labelIds[label.id] = newLabelId
            }
            noteLabels.forEach { noteLabel ->
                val noteId = noteIds.getValue(noteLabel.noteId)
                val labelId = labelIds.getValue(noteLabel.labelId)
                noteLabelRepository.createNoteLabel(noteLabel.copy(id = 0, noteId = noteId, labelId = labelId))
            }
            settingsRepository.updateConfig(settings)
        }
    }

    fun setVaultPasscode(passcode: String) = viewModelScope.launch {
        settingsRepository.updateVaultPasscode(passcode.hash())
    }

    fun updateVaultTimeout(timeout: VaultTimeout) = viewModelScope.launch {
        settingsRepository.updateVaultTimeout(timeout)
    }

    fun toggleIsBioAuthEnabled() = viewModelScope.launch {
        settingsRepository.updateIsBioAuthEnabled(!isBioAuthEnabled.value)
    }

    fun updateLastVersion() = viewModelScope.launch {
        settingsRepository.updateLastVersion(Release.Version.Current.format())
    }

    fun setMainInterfaceId(folderId: Long) = viewModelScope.launch {
        settingsRepository.updateMainInterfaceId(folderId)
    }

    fun updateTheme(value: Theme) = viewModelScope.launch {
        settingsRepository.updateTheme(value)
    }

    fun updateFont(value: Font) = viewModelScope.launch {
        settingsRepository.updateFont(value)
    }

    fun updateLanguage(value: Language) = viewModelScope.launch {
        settingsRepository.updateLanguage(value)
    }

    fun updateIcon(value: Icon) = viewModelScope.launch {
        settingsRepository.updateIcon(value)
    }

    fun toggleRememberScrollingPosition() = viewModelScope.launch {
        settingsRepository.updateIsRememberScrollingPosition(!isRememberScrollingPosition.value)
    }

    fun toggleQuickExit() = viewModelScope.launch {
        settingsRepository.updateQuickExit(!quickExit.value)
    }

    fun toggleContinuousSearch() = viewModelScope.launch {
        settingsRepository.updateContinuousSearch(!continuousSearch.value)
    }

    fun togglePreviewAutoScroll() = viewModelScope.launch {
        settingsRepository.updatePreviewAutoScroll(!previewAutoScroll.value)
    }

    fun emitIsImportFinished() = viewModelScope.launch {
        mutableIsImportFinished.emit(Unit)
    }

    fun getFolderById(folderId: Long) = folderRepository.getFolderById(folderId)

    fun setQuickNoteFolderId(folderId: Long) = viewModelScope.launch {
        settingsRepository.updateQuickNoteFolderId(folderId)
    }

    fun disableVault() = viewModelScope.launch {
        folderRepository.getAllFolders().first()
            .map { it.copy(isVaulted = false) }
            .forEach { folderRepository.updateFolder(it) }
        settingsRepository.updateVaultPasscode(passcode = null)
        settingsRepository.updateVaultTimeout(timeout = VaultTimeout.Immediately)
        settingsRepository.updateScheduledVaultTimeout(timeout = null)
        settingsRepository.updateIsBioAuthEnabled(isEnabled = false)
        settingsRepository.updateIsVaultOpen(isOpen = false)
    }

}