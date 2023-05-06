package com.noto.app.folder

import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Note

data class NoteItemModel(
    val note: Note,
    val labels: List<Label>,
    val isSelected: Boolean,
    val selectionOrder: Int = -1,
    val isDragged: Boolean,
) {
    companion object
}