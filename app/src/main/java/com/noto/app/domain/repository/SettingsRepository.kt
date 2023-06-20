package com.noto.app.domain.repository

import com.noto.app.domain.model.*
import com.noto.app.filtered.FilteredItemModel
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    val config: Flow<SettingsConfig>

    val theme: Flow<Theme>

    val font: Flow<Font>

    val language: Flow<Language>

    val icon: Flow<Icon>

    val vaultPasscode: Flow<String?>

    val vaultTimeout: Flow<VaultTimeout>

    val scheduledVaultTimeout: Flow<VaultTimeout?>

    val isVaultOpen: Flow<Boolean>

    val isBioAuthEnabled: Flow<Boolean>

    val isDoNotDisturb: Flow<Boolean>

    val isScreenOn: Flow<Boolean>

    val isFullScreen: Flow<Boolean>

    val lastVersion: Flow<String>

    val sortingType: Flow<FolderListSortingType>

    val sortingOrder: Flow<SortingOrder>

    val isShowNotesCount: Flow<Boolean>

    val mainInterfaceId: Flow<Long>

    val isRememberScrollingPosition: Flow<Boolean>

    val quickNoteFolderId: Flow<Long>

    val screenBrightnessLevel: Flow<ScreenBrightnessLevel>

    val quickExit: Flow<Boolean>

    val continuousSearch: Flow<Boolean>

    val previewAutoScroll: Flow<Boolean>

    fun getFilteredNotesScrollingPosition(model: FilteredItemModel): Flow<Int>

    fun getWidgetFolderId(widgetId: Int): Flow<Long>

    fun getIsWidgetCreated(widgetId: Int): Flow<Boolean>

    fun getIsWidgetHeaderEnabled(widgetId: Int): Flow<Boolean>

    fun getIsWidgetEditButtonEnabled(widgetId: Int): Flow<Boolean>

    fun getIsWidgetAppIconEnabled(widgetId: Int): Flow<Boolean>

    fun getIsWidgetNewItemButtonEnabled(widgetId: Int): Flow<Boolean>

    fun getWidgetNotesCount(widgetId: Int): Flow<Boolean>

    fun getWidgetRadius(widgetId: Int): Flow<Int>

    fun getWidgetSelectedLabelIds(widgetId: Int, folderId: Long): Flow<List<Long>>

    fun getWidgetFilteringType(widgetId: Int): Flow<FilteringType>

    suspend fun updateConfig(config: SettingsConfig)

    suspend fun updateTheme(theme: Theme)

    suspend fun updateFont(font: Font)

    suspend fun updateLanguage(language: Language)

    suspend fun updateIcon(icon: Icon)

    suspend fun updateVaultPasscode(passcode: String?)

    suspend fun updateVaultTimeout(timeout: VaultTimeout)

    suspend fun updateScheduledVaultTimeout(timeout: VaultTimeout?)

    suspend fun updateIsVaultOpen(isOpen: Boolean)

    suspend fun updateIsBioAuthEnabled(isEnabled: Boolean)

    suspend fun updateLastVersion(version: String)

    suspend fun updateSortingType(sortingType: FolderListSortingType)

    suspend fun updateSortingOrder(sortingOrder: SortingOrder)

    suspend fun updateIsShowNotesCount(isShow: Boolean)

    suspend fun updateIsDoNotDisturb(isDoNotDisturb: Boolean)

    suspend fun updateIsScreenOn(isScreenOn: Boolean)

    suspend fun updateIsFullScreen(isFullScreen: Boolean)

    suspend fun updateMainInterfaceId(interfaceId: Long)

    suspend fun updateFilteredNotesScrollingPosition(model: FilteredItemModel, scrollingPosition: Int)

    suspend fun updateIsRememberScrollingPosition(isRememberScrollingPosition: Boolean)

    suspend fun updateWidgetFolderId(widgetId: Int, folderId: Long)

    suspend fun updateIsWidgetCreated(widgetId: Int, isCreated: Boolean)

    suspend fun updateIsWidgetHeaderEnabled(widgetId: Int, isEnabled: Boolean)

    suspend fun updateIsWidgetEditButtonEnabled(widgetId: Int, isEnabled: Boolean)

    suspend fun updateIsWidgetAppIconEnabled(widgetId: Int, isEnabled: Boolean)

    suspend fun updateIsWidgetNewItemButtonEnabled(widgetId: Int, isEnabled: Boolean)

    suspend fun updateWidgetNotesCount(widgetId: Int, isEnabled: Boolean)

    suspend fun updateWidgetRadius(widgetId: Int, radius: Int)

    suspend fun updateWidgetSelectedLabelIds(widgetId: Int, folderId: Long, labelIds: List<Long>)

    suspend fun updateWidgetFilteringType(widgetId: Int, filteringType: FilteringType)

    suspend fun updateQuickNoteFolderId(folderId: Long)

    suspend fun updateScreenBrightnessLevel(level: ScreenBrightnessLevel)

    suspend fun updateQuickExit(enabled: Boolean)

    suspend fun updateContinuousSearch(isEnabled: Boolean)

    suspend fun updatePreviewAutoScroll(isEnabled: Boolean)

}