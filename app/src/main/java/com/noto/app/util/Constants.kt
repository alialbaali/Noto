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
    const val VaultTimeout = "VaultTimeout"
    const val IsNotParentEnabled = "is_no_parent_enabled"
    const val MainInterfaceId = ""
    const val IsPasscodeValid = "IsPasscodeValid"
    const val ApiKey = "apikey"
    const val Id = "id"
    const val Name = "name"
    const val Email = "email"
    const val Password = "password"
    const val NameMaxLength = 32
    const val GrantType = "grant_type"
    const val RefreshToken = "refresh_token"
    val EmailRegex = Regex(".+@.+\\..+")
    val PasswordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#^])[A-Za-z\\d@$!%*?&#^]{8,}$")

    object Intent {
        const val ActionCreateFolder = "com.noto.intent.action.CREATE_FOLDER"
        const val ActionCreateNote = "com.noto.intent.action.CREATE_NOTE"
        const val ActionOpenFolder = "com.noto.intent.action.OPEN_FOLDER"
        const val ActionOpenNote = "com.noto.intent.action.OPEN_NOTE"
        const val ActionSettings = "com.noto.intent.action.SETTINGS"
    }
}