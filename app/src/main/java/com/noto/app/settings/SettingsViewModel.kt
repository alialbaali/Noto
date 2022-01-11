package com.noto.app.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.hash
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SettingsViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    val isShowNotesCount = storage.get(Constants.ShowNotesCountKey)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val vaultPasscode = storage.getOrNull(Constants.VaultPasscode)
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val vaultTimeout = storage.get(Constants.VaultTimeout)
        .filterNotNull()
        .map { VaultTimeout.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, VaultTimeout.Immediately)

    val isBioAuthEnabled = storage.getOrNull(Constants.IsBioAuthEnabled)
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    private val mutableWhatsNewTab = MutableStateFlow(WhatsNewTab.Default)
    val whatsNewTab get() = mutableWhatsNewTab.asStateFlow()

    fun toggleShowNotesCount() = viewModelScope.launch {
        storage.put(Constants.ShowNotesCountKey, (!isShowNotesCount.value).toString())
    }

    suspend fun exportData(): Map<String, String> {
        val libraries = libraryRepository.getAllLibraries().first()
        val notes = noteRepository.getAllNotes().first()
        val labels = labelRepository.getAllLabels().first()
        val noteLabels = noteLabelRepository.getNoteLabels().first()
        val settings = storage.getAll().first()
        return mapOf(
            Constants.Libraries to DefaultJson.encodeToString(libraries),
            Constants.Notes to DefaultJson.encodeToString(notes),
            Constants.Labels to DefaultJson.encodeToString(labels),
            Constants.NoteLabels to DefaultJson.encodeToString(noteLabels),
            Constants.Settings to DefaultJson.encodeToString(settings)
        )
    }

    suspend fun importData(data: Map<String, String>) {
        val libraryIds = mutableMapOf<Long, Long>()
        val noteIds = mutableMapOf<Long, Long>()
        val labelIds = mutableMapOf<Long, Long>()
        DefaultJson.decodeFromString<List<Library>>(data.getValue(Constants.Libraries))
            .forEach { library ->
                val newLibraryId = libraryRepository.createLibrary(library.copy(id = 0))
                libraryIds[library.id] = newLibraryId
            }
        DefaultJson.decodeFromString<List<Note>>(data.getValue(Constants.Notes))
            .forEach { note ->
                val libraryId = libraryIds.getValue(note.libraryId)
                val newNoteId = noteRepository.createNote(note.copy(id = 0, libraryId = libraryId))
                noteIds[note.id] = newNoteId
            }
        DefaultJson.decodeFromString<List<Label>>(data.getValue(Constants.Labels))
            .forEach { label ->
                val libraryId = libraryIds.getValue(label.libraryId)
                val newLabelId = labelRepository.createLabel(label.copy(id = 0, libraryId = libraryId))
                labelIds[label.id] = newLabelId
            }
        DefaultJson.decodeFromString<List<NoteLabel>>(data.getValue(Constants.NoteLabels))
            .forEach { noteLabel ->
                val noteId = noteIds.getValue(noteLabel.noteId)
                val labelId = labelIds.getValue(noteLabel.labelId)
                noteLabelRepository.createNoteLabel(noteLabel.copy(id = 0, noteId = noteId, labelId = labelId))
            }
        DefaultJson.decodeFromString<Map<String, String>>(data.getValue(Constants.Settings))
            .forEach { (key, value) -> storage.put(key, value) }
    }

    fun setVaultPasscode(passcode: String) = viewModelScope.launch {
        storage.put(Constants.VaultPasscode, passcode.hash())
    }

    fun setVaultTimeout(timeout: VaultTimeout) = viewModelScope.launch {
        storage.put(Constants.VaultTimeout, timeout.name)
    }

    fun toggleIsBioAuthEnabled() = viewModelScope.launch {
        storage.put(Constants.IsBioAuthEnabled, isBioAuthEnabled.value.not().toString())
    }

    fun setWhatsNewTab(tab: WhatsNewTab) {
        mutableWhatsNewTab.value = tab
    }

    fun updateLastVersion() = viewModelScope.launch {
        storage.put(Constants.LastVersion, Release.CurrentVersion)
    }
}

private val DefaultJson = Json {
    isLenient = true
    allowStructuredMapKeys = true
    coerceInputValues = true
    encodeDefaults = true
    ignoreUnknownKeys = true
    prettyPrint = true
}