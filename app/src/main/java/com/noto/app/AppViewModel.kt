package com.noto.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Icon
import com.noto.app.domain.model.Language
import com.noto.app.domain.model.VaultTimeout
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.repository.SettingsRepository
import com.noto.app.util.AllFoldersId
import com.noto.app.util.isGeneral
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/** Use [Flow.distinctUntilChanged] with [SharedFlow] or [Flow.shareIn]
 * to update and emit only the value you specified. Otherwise, everytime you update
 * a value, it will re-emit every value to every flow that uses [Flow.shareIn].
 * */

class AppViewModel(private val folderRepository: FolderRepository, private val settingsRepository: SettingsRepository) : ViewModel() {

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

    val currentIcon = viewModelScope.async { settingsRepository.icon.first() }

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
}