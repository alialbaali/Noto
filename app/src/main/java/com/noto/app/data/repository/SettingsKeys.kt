package com.noto.app.data.repository

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.noto.app.data.repository.SettingsKeys.Keys.Widget.AppIcon
import com.noto.app.data.repository.SettingsKeys.Keys.Widget.EditButton
import com.noto.app.data.repository.SettingsKeys.Keys.Widget.FolderId
import com.noto.app.data.repository.SettingsKeys.Keys.Widget.Header
import com.noto.app.data.repository.SettingsKeys.Keys.Widget.Id
import com.noto.app.data.repository.SettingsKeys.Keys.Widget.NewItemButton
import com.noto.app.data.repository.SettingsKeys.Keys.Widget.NotesCount
import com.noto.app.data.repository.SettingsKeys.Keys.Widget.Radius
import com.noto.app.data.repository.SettingsKeys.Keys.Widget.SelectedLabelIds
import com.noto.app.util.Constants

object SettingsKeys {
    val Theme = stringPreferencesKey(Keys.Theme)
    val Font = stringPreferencesKey(Keys.Font)
    val Language = stringPreferencesKey(Keys.Language)
    val FolderListSortingType = stringPreferencesKey(Keys.FolderListSortingType)
    val FolderListSortingOrder = stringPreferencesKey(Keys.FolderListSortingOrder)
    val ShowNotesCount = stringPreferencesKey(Keys.ShowNotesCount)
    val IsVaultOpen = stringPreferencesKey(Keys.IsVaultOpen)
    val VaultPasscode = stringPreferencesKey(Keys.VaultPasscode)
    val VaultTimeout = stringPreferencesKey(Keys.VaultTimeout)
    val ScheduledVaultTimeout = stringPreferencesKey(Keys.ScheduledVaultTimeout)
    val LastVersion = stringPreferencesKey(Keys.LastVersion)
    val IsDoNotDisturb = booleanPreferencesKey(Keys.IsDoNotDisturb)
    val IsScreenOn = booleanPreferencesKey(Keys.IsScreenOn)
    val IsFullScreen = booleanPreferencesKey(Keys.IsFullScreen)
    val IsBioAuthEnabled = stringPreferencesKey(Keys.IsBioAuthEnabled)
    val MainFolderId = longPreferencesKey(Keys.MainFolderId)

    @Suppress("FunctionName")
    object Widget {
        fun Id(widgetId: Int) = stringPreferencesKey(widgetId.Id)
        fun FolderId(widgetId: Int) = longPreferencesKey(widgetId.FolderId)
        fun Header(widgetId: Int) = stringPreferencesKey(widgetId.Header)
        fun EditButton(widgetId: Int) = stringPreferencesKey(widgetId.EditButton)
        fun AppIcon(widgetId: Int) = stringPreferencesKey(widgetId.AppIcon)
        fun NewItemButton(widgetId: Int) = stringPreferencesKey(widgetId.NewItemButton)
        fun NotesCount(widgetId: Int) = stringPreferencesKey(widgetId.NotesCount)
        fun Radius(widgetId: Int) = stringPreferencesKey(widgetId.Radius)
        fun SelectedLabelIds(widgetId: Int, folderId: Long) = stringPreferencesKey(widgetId.SelectedLabelIds(folderId))
    }

    private object Keys {
        const val Theme = "Theme"
        const val Font = "Font"
        const val Language = "Language"
        const val FolderListSortingType = "Library_List_Sorting_Type"
        const val FolderListSortingOrder = "Library_List_Sorting_Order"
        const val ShowNotesCount = "Show_Notes_Count"
        const val IsVaultOpen = "IsVaultOpen"
        const val VaultPasscode = "VaultPasscode"
        const val VaultTimeout = "VaultTimeout"
        const val ScheduledVaultTimeout = "ScheduledVaultTimeout"
        const val LastVersion = "LastVersion"
        const val IsBioAuthEnabled = "IsBioAuthEnabled"
        const val MainFolderId = "MainFolderId"
        const val IsDoNotDisturb = "IsDoNotDisturb"
        const val IsScreenOn = "IsScreenOn"
        const val IsFullScreen = "IsFullScreen"

        object Widget {
            val Int.Id get() = "Widget_Id_$this"
            val Int.FolderId get() = "Widget_Id_Folder_Id_$this"
            val Int.Header get() = "Widget_Header_$this"
            val Int.EditButton get() = "Widget_Edit_Button$this"
            val Int.AppIcon get() = "Widget_App_Icon_$this"
            val Int.NewItemButton get() = "Widget_New_Item_Button_$this"
            val Int.NotesCount get() = "Widget_Notes_Count_$this"
            val Int.Radius get() = "Widget_Radius_$this"

            @Suppress("FunctionName")
            fun Int.SelectedLabelIds(folderId: Long) = Id + "_" + Constants.FolderId + folderId.toString()
        }
    }
}