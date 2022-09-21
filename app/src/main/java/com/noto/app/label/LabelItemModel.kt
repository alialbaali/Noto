package com.noto.app.label

import com.noto.app.domain.model.Label

data class LabelItemModel(
    val label: Label,
    val isSelected: Boolean
)