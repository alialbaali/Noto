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
    const val SelectedLibraryItemClickListener = "select_library_item_click_listener"
    const val Libraries = "Libraries"
    const val Notes = "Notes"
    const val Labels = "Labels"
    const val NoteLabels = "NoteLabels"
    const val Settings = "Settings"

    object Intent {
        const val ActionCreateLibrary = "com.noto.intent.action.CREATE_LIBRARY"
        const val ActionCreateNote = "com.noto.intent.action.CREATE_NOTE"
        const val ActionOpenLibrary = "com.noto.intent.action.OPEN_LIBRARY"
        const val ActionOpenNote = "com.noto.intent.action.OPEN_NOTE"
    }

    object Widget {
        val Int.WidgetId get() = "Widget_Id_$this"
        val Int.WidgetHeader get() = "Widget_Header_$this"
        val Int.EditWidgetButton get() = "Edit_Widget_Button_$this"
        val Int.AppIcon get() = "App_Icon_$this"
        val Int.NewLibraryButton get() = "New_Library_Button_$this"
        val Int.NotesCount get() = "Notes_Count_$this"
        val Int.WidgetRadius get() = "Widget_Radius_$this"
        val Int.WidgetLayout get() = "Widget_Layout_$this"
    }
}