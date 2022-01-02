package com.noto.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.*
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppViewModel(private val storage: LocalStorage) : ViewModel() {

    val theme = storage.get(Constants.ThemeKey)
        .filterNotNull()
        .map { Theme.valueOf(it) }
        .shareIn(viewModelScope, SharingStarted.Lazily, replay = 1)

    val font = storage.get(Constants.FontKey)
        .filterNotNull()
        .map { Font.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Font.Nunito)

    val language = storage.get(Constants.LanguageKey)
        .filterNotNull()
        .map { Language.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Language.System)

    val vaultTimeout = storage.get(Constants.VaultTimeout)
        .filterNotNull()
        .map { VaultTimeout.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, VaultTimeout.Immediately)

    val lastVersion = storage.get(Constants.LastVersion)
        .shareIn(viewModelScope, SharingStarted.Eagerly, replay = 1)

    init {
        createDefaultConstants()
        vaultTimeout
            .onEach { timeout -> if (timeout == VaultTimeout.Immediately) closeVault() }
            .launchIn(viewModelScope)
    }

    fun updateTheme(value: Theme) = viewModelScope.launch {
        storage.put(Constants.ThemeKey, value.toString())
    }

    fun updateFont(value: Font) = viewModelScope.launch {
        storage.put(Constants.FontKey, value.toString())
    }

    fun updateLanguage(value: Language) = viewModelScope.launch {
        storage.put(Constants.LanguageKey, value.toString())
    }

    fun closeVault() = viewModelScope.launch {
        storage.put(Constants.IsVaultOpen, false.toString())
    }

    private fun createDefaultConstants() = viewModelScope.launch {
        launch {
            storage.getOrNull(Constants.ThemeKey)
                .firstOrNull()
                .also { if (it == null) storage.put(Constants.ThemeKey, Theme.System.toString()) }
        }

        launch {
            storage.getOrNull(Constants.FontKey)
                .firstOrNull()
                .also { if (it == null) storage.put(Constants.FontKey, Font.Nunito.toString()) }
        }

        launch {
            storage.getOrNull(Constants.LanguageKey)
                .firstOrNull()
                .also { if (it == null) storage.put(Constants.LanguageKey, Language.System.toString()) }
        }

        launch {
            storage.getOrNull(Constants.LibraryListSortingTypeKey)
                .firstOrNull()
                .also { if (it == null) storage.put(Constants.LibraryListSortingTypeKey, LibraryListSortingType.CreationDate.toString()) }
        }

        launch {
            storage.getOrNull(Constants.LibraryListSortingOrderKey)
                .firstOrNull()
                .also { if (it == null) storage.put(Constants.LibraryListSortingOrderKey, SortingOrder.Descending.toString()) }
        }

        launch {
            storage.getOrNull(Constants.LibraryListLayoutKey)
                .firstOrNull()
                .also { if (it == null) storage.put(Constants.LibraryListLayoutKey, Layout.Grid.toString()) }
        }

        launch {
            storage.getOrNull(Constants.ShowNotesCountKey)
                .firstOrNull()
                .also { if (it == null) storage.put(Constants.ShowNotesCountKey, true.toString()) }
        }

        launch {
            storage.getOrNull(Constants.VaultTimeout)
                .firstOrNull()
                .also { if (it == null) storage.put(Constants.VaultTimeout, VaultTimeout.Immediately.toString()) }
        }

        launch {
            storage.getOrNull(Constants.LastVersion)
                .firstOrNull()
                .also { if (it == null) storage.put(Constants.LastVersion, "1.7.2") }
        }
    }
}