package com.noto.app.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SettingsConfig(
    val theme: Theme,
    val font: Font,
    val language: Language,
    val vaultPasscode: String?,
    val vaultTimeout: VaultTimeout,
    val scheduledVaultTimeout: VaultTimeout?,
    val isVaultOpen: Boolean,
    val isBioAuthEnabled: Boolean,
    val lastVersion: String,
    val sortingType: FolderListSortingType,
    val sortingOrder: SortingOrder,
    val isCollapseToolbar: Boolean,
    val isShowNotesCount: Boolean,
    val mainFolderId: Long,
)