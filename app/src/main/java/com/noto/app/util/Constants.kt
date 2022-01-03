package com.noto.app.util

object Constants {
    const val ThemeKey = "Theme"
    const val FontKey = "Font"
    const val LibraryListLayoutKey = "Library_List_Layout"
    const val LibraryListSortingTypeKey = "Library_List_Sorting_Type"
    const val LibraryListSortingOrderKey = "Library_List_Sorting_Order"
    const val ShowNotesCountKey = "Show_Notes_Count"
    const val LanguageKey = "Language"
    const val LibraryId = "library_id"
    const val NoteId = "note_id"
    const val Body = "body"
    const val IsDismissible = "is_dismissible"
    const val ClickListener = "click_listener"
    const val Libraries = "Libraries"
    const val Notes = "Notes"
    const val Labels = "Labels"
    const val NoteLabels = "NoteLabels"
    const val Settings = "Settings"
    const val IsVaultOpen = "IsVaultOpen"
    const val VaultPasscode = "VaultPasscode"
    const val VaultTimeout = "VaultTimeout"
    const val ScheduledVaultTimeout = "ScheduledVaultTimeout"
    const val LastVersion = "LastVersion"

    object Intent {
        const val ActionCreateLibrary = "com.noto.intent.action.CREATE_LIBRARY"
        const val ActionCreateNote = "com.noto.intent.action.CREATE_NOTE"
        const val ActionOpenLibrary = "com.noto.intent.action.OPEN_LIBRARY"
        const val ActionOpenNote = "com.noto.intent.action.OPEN_NOTE"
    }

    object Widget {
        val Int.Id get() = "Widget_Id_$this"
        val Int.Header get() = "Widget_Header_$this"
        val Int.EditButton get() = "Widget_Edit_Button$this"
        val Int.AppIcon get() = "Widget_App_Icon_$this"
        val Int.NewItemButton get() = "Widget_New_Item_Button_$this"
        val Int.NotesCount get() = "Widget_Notes_Count_$this"
        val Int.Radius get() = "Widget_Radius_$this"
        fun Int.LabelIds(libraryId: Long) = Id + "_" + LibraryId + libraryId.toString()
    }
}