package com.noto.note.model

import com.noto.R

data class Notebook(
    val id: Long = 0L,
    val title: String,
    val color: NotebookColor = NotebookColor.PINK
)

enum class NotebookColor {
    GRAY,
    BLUE,
    PINK,
    CYAN
}