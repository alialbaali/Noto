package com.noto.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.AllFoldersId
import com.noto.app.util.firstLineOrEmpty
import com.noto.app.util.isGeneral
import com.noto.app.util.takeAfterFirstLineOrEmpty
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Use [Flow.distinctUntilChanged] with [SharedFlow] or [Flow.shareIn]
 * to update and emit only the value you specified. Otherwise, everytime you update
 * a value, it will re-emit every value to every flow that uses [Flow.shareIn].
 * */

class AppViewModel(
    private val folderRepository: FolderRepository,
    private val noteRepository: NoteRepository,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val theme = settingsRepository.theme
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val language = settingsRepository.language
        .map { if (it in Language.Deprecated) Language.System else it }
        .stateIn(viewModelScope, SharingStarted.Eagerly, Language.System)

    val icon = settingsRepository.icon
        .stateIn(viewModelScope, SharingStarted.Eagerly, Icon.Futuristic)

    val vaultTimeout = settingsRepository.vaultTimeout
        .stateIn(viewModelScope, SharingStarted.Eagerly, VaultTimeout.Immediately)

    /**
     * If the activity gets destroyed, a new work will be enqueued everytime the app runs. This way, we check if there has been any scheduled work before,
     * so we don't cancel an already existing one unless they don't match with [vaultTimeout] property above.
     * */
    val scheduledVaultTimeout = settingsRepository.scheduledVaultTimeout
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val isVaultOpen = settingsRepository.isVaultOpen
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val lastVersion = settingsRepository.lastVersion
        .distinctUntilChanged()
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val mainInterfaceId = settingsRepository.mainInterfaceId
        .stateIn(viewModelScope, SharingStarted.Eagerly, AllFoldersId)

    var shouldNavigateToMainFragment = true
        private set

    val currentIcon = viewModelScope.async { settingsRepository.icon.first() }

    var currentTheme: Theme? = null
        private set

    private val mutableIsNotificationPermissionGranted = MutableStateFlow<Boolean?>(null)
    val isNotificationPermissionGranted get() = mutableIsNotificationPermissionGranted.asStateFlow()

    val quickNoteFolderId = settingsRepository.quickNoteFolderId
        .stateIn(viewModelScope, SharingStarted.Eagerly, Folder.GeneralFolderId)

    var isQuickNoteDialogCreated = false
        private set

    init {
        createGeneralFolder()
        vaultTimeout
            .onEach { timeout -> if (timeout == VaultTimeout.Immediately) closeVault() }
            .launchIn(viewModelScope)
    }

    fun closeVault() = viewModelScope.launch {
        settingsRepository.updateIsVaultOpen(false)
    }

    fun setScheduledVaultTimeout(vaultTimeout: VaultTimeout?) = viewModelScope.launch {
        settingsRepository.updateScheduledVaultTimeout(vaultTimeout)
    }

    private fun createGeneralFolder() = viewModelScope.launch {
        folderRepository.getFolders()
            .firstOrNull()
            ?.also { folders -> if (folders.none { it.isGeneral }) folderRepository.createFolder(Folder.GeneralFolder()) }
    }

    fun setShouldNavigateToMainFragment(value: Boolean) {
        shouldNavigateToMainFragment = value
    }

    fun setCurrentTheme(theme: Theme) {
        currentTheme = theme
    }

    fun createQuickNote(content: String, onSuccess: (Folder, Note) -> Unit) = viewModelScope.launch {
        val title = content.firstLineOrEmpty()
        val body = content.takeAfterFirstLineOrEmpty()
        val folderId = settingsRepository.quickNoteFolderId.first()
        val note = Note(folderId = folderId, title = title, body = body, position = 0)
        val noteId = noteRepository.createNote(note)
        val folder = folderRepository.getFolderById(folderId).first()
        onSuccess(folder, note.copy(id = noteId))
    }

    fun setNotificationPermissionResult(isGranted: Boolean?) {
        mutableIsNotificationPermissionGranted.value = isGranted
    }

    fun updateLanguage(value: Language) = viewModelScope.launch {
        settingsRepository.updateLanguage(value)
    }

    fun setIsQuickNoteDialogCreated() {
        isQuickNoteDialogCreated = true
    }
}