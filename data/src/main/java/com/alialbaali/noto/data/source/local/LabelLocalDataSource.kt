package com.alialbaali.noto.data.source.local

import com.noto.domain.model.Label

interface LabelLocalDataSource {

    suspend fun createLabel(label: Label)

    suspend fun getLabels(): List<Label>

    suspend fun getLabel(): Label

    suspend fun updateLabel(label: Label)

    suspend fun deleteLabel(label: Label)
}