package com.noto.app.filtered

import com.noto.app.domain.model.NotoColor

enum class FilteredItemModel(val id: Long, val color: NotoColor) {
    All(-2L, NotoColor.Blue),
    Recent(-3L, NotoColor.Yellow),
    Scheduled(-5L, NotoColor.Red),
    Archived(-6L, NotoColor.Purple);

    companion object {
        val Ids = entries.map(FilteredItemModel::id)
    }
}