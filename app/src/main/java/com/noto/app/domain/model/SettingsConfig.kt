package com.noto.app.domain.model

import com.noto.app.util.AllFoldersId
import kotlinx.serialization.Serializable

@Serializable
data class SettingsConfig(
    val theme: Theme = Theme.System,
    val font: Font = Font.Nunito,
    val language: Language = Language.System,
    val vaultPasscode: String? = null,
    val vaultTimeout: VaultTimeout = VaultTimeout.Immediately,
    val scheduledVaultTimeout: VaultTimeout? = null,
    val isVaultOpen: Boolean = false,
    val isBioAuthEnabled: Boolean = false,
    val lastVersion: String = Release.Version.Last,
    val sortingType: FolderListSortingType = FolderListSortingType.CreationDate,
    val sortingOrder: SortingOrder = SortingOrder.Descending,
    val isShowNotesCount: Boolean = false,
    val isDoNotDisturb: Boolean = false,
    val isScreenOn: Boolean = true,
    val mainInterfaceId: Long = AllFoldersId,
)