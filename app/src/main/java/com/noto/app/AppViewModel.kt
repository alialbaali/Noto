package com.noto.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Language
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.VaultTimeout
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.isInbox
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(private val libraryRepository: LibraryRepository, private val settingsRepository: SettingsRepository) : ViewModel() {

    val theme = settingsRepository.theme
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    val language = settingsRepository.language
        .map { it.takeIf { it != Language.Tamil } ?: Language.System }
        .stateIn(viewModelScope, SharingStarted.Lazily, Language.System)

    val vaultTimeout = settingsRepository.vaultTimeout
        .stateIn(viewModelScope, SharingStarted.Eagerly, VaultTimeout.Immediately)

    /**
     * If the activity gets destroyed, a new work will be enqueued everytime the app runs. This way, we check if there has been any scheduled work before,
     * so we don't cancel an already existing one unless they don't match with [vaultTimeout] property above.
     * */
    val scheduledVaultTimeout = settingsRepository.scheduledVaultTimeout
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val isVaultOpen = settingsRepository.isVaultOpen
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val lastVersion = settingsRepository.lastVersion
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    val mainLibraryId = settingsRepository.mainLibraryId
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    init {
        createInboxLibrary()
//        vaultTimeout
//            .drop(1)
//            .onEach { timeout -> if (timeout == VaultTimeout.Immediately) closeVault() }
//            .launchIn(viewModelScope)
    }

    fun closeVault() = viewModelScope.launch {
        settingsRepository.updateIsVaultOpen(false)
    }

    fun setScheduledVaultTimeout(vaultTimeout: VaultTimeout?) = viewModelScope.launch {
        settingsRepository.updateScheduledVaultTimeout(vaultTimeout)
    }

    private fun createInboxLibrary() = viewModelScope.launch {
        libraryRepository.getLibraries()
            .firstOrNull()
            ?.also { libraries -> if (libraries.none { it.isInbox }) libraryRepository.createLibrary(Folder.Inbox()) }
    }
}