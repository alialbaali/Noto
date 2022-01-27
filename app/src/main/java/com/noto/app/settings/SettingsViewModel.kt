package com.noto.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.*
import com.noto.app.util.Constants
import com.noto.app.util.hash
import com.noto.app.util.isGeneral
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val theme = settingsRepository.theme
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    val font = settingsRepository.font
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    val language = settingsRepository.language
        .stateIn(viewModelScope, SharingStarted.Lazily, Language.System)

    val isShowNotesCount = settingsRepository.isShowNotesCount
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val vaultPasscode = settingsRepository.vaultPasscode
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val vaultTimeout = settingsRepository.vaultTimeout
        .stateIn(viewModelScope, SharingStarted.Lazily, VaultTimeout.Immediately)

    val isBioAuthEnabled = settingsRepository.isBioAuthEnabled
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val mainFolderId = settingsRepository.mainFolderId
        .stateIn(viewModelScope, SharingStarted.Eagerly, Folder.GeneralFolderId)

    val isCollapseToolbar = settingsRepository.isCollapseToolbar
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val mutableWhatsNewTab = MutableStateFlow(WhatsNewTab.Default)
    val whatsNewTab get() = mutableWhatsNewTab.asStateFlow()

    fun toggleShowNotesCount() = viewModelScope.launch {
        settingsRepository.updateIsShowNotesCount(!isShowNotesCount.value)
    }

    fun toggleCollapseToolbar() = viewModelScope.launch {
        settingsRepository.updateIsCollapseToolbar(!isCollapseToolbar.value)
    }

    suspend fun exportData(): Map<String, String> = withContext(Dispatchers.IO) {
        val folders = folderRepository.getAllFolders().first()
        val notes = noteRepository.getAllNotes().first()
        val labels = labelRepository.getAllLabels().first()
        val noteLabels = noteLabelRepository.getNoteLabels().first()
        val settings = settingsRepository.config.first()
        mapOf(
            Constants.Folders to DefaultJson.encodeToString(folders),
            Constants.Notes to DefaultJson.encodeToString(notes),
            Constants.Labels to DefaultJson.encodeToString(labels),
            Constants.NoteLabels to DefaultJson.encodeToString(noteLabels),
            Constants.Settings to DefaultJson.encodeToString(settings)
        )
    }

    suspend fun importData(data: Map<String, String>) = withContext(Dispatchers.IO) {
        val folderIds = mutableMapOf<Long, Long>()
        val noteIds = mutableMapOf<Long, Long>()
        val labelIds = mutableMapOf<Long, Long>()
        DefaultJson.decodeFromString<List<Folder>>(data.getValue(Constants.Folders))
            .forEach { folder ->
                if (folder.isGeneral) {
                    folderIds[folder.id] = Folder.GeneralFolderId
                    folderRepository.updateFolder(folder)
                } else {
                    val newFolderId = folderRepository.createFolder(folder.copy(id = 0))
                    folderIds[folder.id] = newFolderId
                }
            }
        DefaultJson.decodeFromString<List<Note>>(data.getValue(Constants.Notes))
            .forEach { note ->
                val folderId = folderIds.getValue(note.folderId)
                val newNoteId = noteRepository.createNote(note.copy(id = 0, folderId = folderId))
                noteIds[note.id] = newNoteId
            }
        DefaultJson.decodeFromString<List<Label>>(data.getValue(Constants.Labels))
            .forEach { label ->
                val folderId = folderIds.getValue(label.folderId)
                val newLabelId = labelRepository.createLabel(label.copy(id = 0, folderId = folderId))
                labelIds[label.id] = newLabelId
            }
        DefaultJson.decodeFromString<List<NoteLabel>>(data.getValue(Constants.NoteLabels))
            .forEach { noteLabel ->
                val noteId = noteIds.getValue(noteLabel.noteId)
                val labelId = labelIds.getValue(noteLabel.labelId)
                noteLabelRepository.createNoteLabel(noteLabel.copy(id = 0, noteId = noteId, labelId = labelId))
            }
        DefaultJson.decodeFromString<SettingsConfig>(data.getValue(Constants.Settings))
            .also { settingsRepository.updateConfig(it) }
    }

    fun setVaultPasscode(passcode: String) = viewModelScope.launch {
        settingsRepository.updateVaultPasscode(passcode.hash())
    }

    fun setVaultTimeout(timeout: VaultTimeout) = viewModelScope.launch {
        settingsRepository.updateVaultTimeout(timeout)
    }

    fun toggleIsBioAuthEnabled() = viewModelScope.launch {
        settingsRepository.updateIsBioAuthEnabled(!isBioAuthEnabled.value)
    }

    fun setWhatsNewTab(tab: WhatsNewTab) {
        mutableWhatsNewTab.value = tab
    }

    fun updateLastVersion() = viewModelScope.launch {
        settingsRepository.updateLastVersion(Release.CurrentVersion)
    }

    fun setMainFolderId(folderId: Long) = viewModelScope.launch {
        settingsRepository.updateMainFolderId(folderId)
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

}

@OptIn(ExperimentalSerializationApi::class)
private val DefaultJson = Json {
    isLenient = true
    allowStructuredMapKeys = true
    coerceInputValues = true
    encodeDefaults = true
    ignoreUnknownKeys = true
    explicitNulls = false
    prettyPrint = true
}