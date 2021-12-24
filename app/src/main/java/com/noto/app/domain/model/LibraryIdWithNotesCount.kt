package com.noto.app.domain.model

import androidx.room.ColumnInfo

data class LibraryIdWithNotesCount(
    @ColumnInfo(name = "library_id")
    val libraryId: Long,
    val notesCount: Int,
)