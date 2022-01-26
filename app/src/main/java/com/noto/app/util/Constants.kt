package com.noto.app.util

object Constants {
    const val Theme = "Theme"
    const val LibraryId = "library_id"
    const val FilteredLibraryIds = "filtered_library_ids"
    const val SelectedLibraryId = "selected_library_id"
    const val NoteId = "note_id"
    const val Body = "body"
    const val IsDismissible = "is_dismissible"
    const val ClickListener = "click_listener"
    const val Libraries = "Libraries"
    const val Notes = "Notes"
    const val Labels = "Labels"
    const val NoteLabels = "NoteLabels"
    const val Settings = "Settings"
    const val VaultTimeout = "VaultTimeout"

    object Intent {
        const val ActionCreateLibrary = "com.noto.intent.action.CREATE_LIBRARY"
        const val ActionCreateNote = "com.noto.intent.action.CREATE_NOTE"
        const val ActionOpenLibrary = "com.noto.intent.action.OPEN_LIBRARY"
        const val ActionOpenNote = "com.noto.intent.action.OPEN_NOTE"
    }
}