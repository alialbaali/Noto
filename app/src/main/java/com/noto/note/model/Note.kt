package com.noto.note.model

data class Note(
    val id: Long,
    val notebookId: Long,
    val title: String = "",
    val body: String = ""
)