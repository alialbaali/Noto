package com.noto.app.domain.model

import androidx.room.ColumnInfo

data class FolderIdWithNotesCount(
    @ColumnInfo(name = "folder_id")
    val folderId: Long,
    val notesCount: Int,
)