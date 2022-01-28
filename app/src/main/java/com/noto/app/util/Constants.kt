package com.noto.app.util

object Constants {
    const val Theme = "Theme"
    const val FolderId = "folder_id"
    const val FilteredFolderIds = "filtered_folder_ids"
    const val SelectedFolderId = "selected_folder_id"
    const val NoteId = "note_id"
    const val Body = "body"
    const val IsDismissible = "is_dismissible"
    const val ClickListener = "click_listener"
    const val Folders = "Folders"
    const val Notes = "Notes"
    const val Labels = "Labels"
    const val NoteLabels = "NoteLabels"
    const val Settings = "Settings"
    const val VaultTimeout = "VaultTimeout"

    object Intent {
        const val ActionCreateFolder = "com.noto.intent.action.CREATE_FOLDER"
        const val ActionCreateNote = "com.noto.intent.action.CREATE_NOTE"
        const val ActionOpenFolder = "com.noto.intent.action.OPEN_FOLDER"
        const val ActionOpenNote = "com.noto.intent.action.OPEN_NOTE"
        const val ActionSettings = "com.noto.intent.action.SETTINGS"
    }
}