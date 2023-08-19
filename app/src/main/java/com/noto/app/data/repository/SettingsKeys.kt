package com.noto.app.data.repository

import androidx.datastore.preferences.core.*
import com.noto.app.filtered.FilteredItemModel
import com.noto.app.util.Constants

object SettingsKeys {
    val Theme = stringPreferencesKey("Theme")
    val Font = stringPreferencesKey("Font")
    val Language = stringPreferencesKey("Language")
    val Icon = stringPreferencesKey("Icon")
    val FolderListSortingType = stringPreferencesKey("Library_List_Sorting_Type")
    val FolderListSortingOrder = stringPreferencesKey("Library_List_Sorting_Order")
    val ShowNotesCount = stringPreferencesKey("Show_Notes_Count")
    val IsVaultOpen = stringPreferencesKey("IsVaultOpen")
    val VaultPasscode = stringPreferencesKey("VaultPasscode")
    val VaultTimeout = stringPreferencesKey("VaultTimeout")
    val ScheduledVaultTimeout = stringPreferencesKey("ScheduledVaultTimeout")
    val LastVersion = stringPreferencesKey("LastVersion")
    val IsDoNotDisturb = booleanPreferencesKey("IsDoNotDisturb")
    val IsScreenOn = booleanPreferencesKey("IsScreenOn")
    val IsFullScreen = booleanPreferencesKey("IsFullScreen")
    val IsBioAuthEnabled = stringPreferencesKey("IsBioAuthEnabled")
    val MainInterfaceId = longPreferencesKey("MainFolderId")
    val IsRememberScrollingPosition = booleanPreferencesKey("IsRememberScrollingPosition")
    val QuickNoteFolderId = longPreferencesKey("QuickNoteFolderId")
    val ScreenBrightnessLevel = floatPreferencesKey("ScreenBrightnessLevel")
    val QuickExit = booleanPreferencesKey("QuickExit")
    val ContinuousSearch = booleanPreferencesKey("ContinuousSearch")
    val PreviewAutoScroll = booleanPreferencesKey("PreviewAutoScroll")
    fun FilteredItemModel(model: FilteredItemModel) = intPreferencesKey("Filtered_Item_Model_${model.id}")

    @Suppress("FunctionName")
    object Widget {
        fun Id(widgetId: Int) = stringPreferencesKey("Widget_Id_$widgetId")
        fun FolderId(widgetId: Int) = longPreferencesKey("Widget_Id_Folder_Id_$widgetId")
        fun Header(widgetId: Int) = stringPreferencesKey("Widget_Header_$widgetId")
        fun EditButton(widgetId: Int) = stringPreferencesKey("Widget_Edit_Button$widgetId")
        fun AppIcon(widgetId: Int) = stringPreferencesKey("Widget_App_Icon_$widgetId")
        fun NewItemButton(widgetId: Int) = stringPreferencesKey("Widget_New_Item_Button_$widgetId")
        fun NotesCount(widgetId: Int) = stringPreferencesKey("Widget_Notes_Count_$widgetId")
        fun Radius(widgetId: Int) = stringPreferencesKey("Widget_Radius_$widgetId")
        fun FilteringType(widgetId: Int) = stringPreferencesKey("Widget_Filtering_Type_$widgetId")
        fun SelectedLabelIds(widgetId: Int, folderId: Long) =
            stringPreferencesKey("Widget_Id_$widgetId" + "_" + Constants.FolderId + folderId.toString())
    }
}