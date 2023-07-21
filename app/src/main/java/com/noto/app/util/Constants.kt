package com.noto.app.util

object Constants {
    const val Theme = "Theme"
    const val FolderId = "folder_id"
    const val FolderTitle = "folder_title"
    const val FilteredFolderIds = "filtered_folder_ids"
    const val SelectedFolderId = "selected_folder_id"
    const val SelectedNoteIds = "selected_note_ids"
    const val NoteId = "note_id"
    const val Body = "body"
    const val IsDismissible = "is_dismissible"
    const val ClickListener = "click_listener"
    const val VaultTimeout = "VaultTimeout"
    const val IsNoneEnabled = "is_none_enabled"
    const val IsPasscodeValid = "IsPasscodeValid"
    const val WidgetRadius = "WidgetRadius"
    const val NoteTitle = "NoteTitle"
    const val NoteBody = "NoteBody"
    const val ScrollPosition = "ScrollPosition"
    const val IsTitleVisible = "IsTitleVisible"
    const val IsBodyVisible = "IsBodyVisible"
    const val Title = "title"
    const val Model = "model"
    const val EmailType = "mailto:"
    const val DisableSelection = "DisableSelection"
    const val FilteringType = "FilteringType"
    const val SortingType = "SortingType"
    const val SortingOrder = "SortingOrder"
    const val GroupingType = "GroupingType"
    const val GroupingOrder = "GroupingOrder"

    object Intent {
        const val ActionCreateFolder = "com.noto.intent.action.CREATE_FOLDER"
        const val ActionCreateNote = "com.noto.intent.action.CREATE_NOTE"
        const val ActionQuickNote = "com.noto.intent.action.QUICK_NOTE"
        const val ActionOpenFolder = "com.noto.intent.action.OPEN_FOLDER"
        const val ActionOpenNote = "com.noto.intent.action.OPEN_NOTE"
        const val ActionSettings = "com.noto.intent.action.SETTINGS"
    }

    object Noto {
        const val Email = "noto@alialbaali.com"
        const val DeveloperUrl = "https://www.alialbaali.com"
        const val LicenseUrl = "https://www.apache.org/licenses/LICENSE-2.0"
        const val PlayStoreUrl = "https://play.google.com/store/apps/details?id=com.noto"
        const val GithubUrl = "https://github.com/alialbaali/Noto"
        const val TelegramUrl = "https://t.me/notoapp"
        const val PrivacyPolicyUrl = "https://github.com/alialbaali/Noto/blob/master/PrivacyPolicy.md"
        const val GithubIssueUrl = "https://github.com/alialbaali/Noto/issues/new"
        const val ReportIssueEmailSubject = "Issue Regarding Noto"
        const val GitHubReleasesUrl = "https://github.com/alialbaali/Noto/releases"
        fun GitHubReleaseUrl(version: String) = "https://github.com/alialbaali/Noto/releases/tag/v$version"

        fun ReportIssueEmailBody(androidVersion: String, sdkVersion: String, appVersion: String) = """
            Hi there,
            
            I'm having an issue with [ISSUE].
            
            Android version: $androidVersion
            SDK version: $sdkVersion
            App version: $appVersion
            
            Regards,
        """.trimIndent()

        const val TranslationEmailSubject = "Translate Noto"
        val TranslationEmailBody = """
            Hi there,
            
            I would like to translate Noto to [LANGUAGE].
            
            I want to be credited as (optional):
            Name: [NAME]
            Link (optional): [LINK]
            
            Regards,
        """.trimIndent()
    }
}