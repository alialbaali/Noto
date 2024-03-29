package com.noto.app.domain.source

import com.noto.app.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LocalLabelDataSource {

    fun getAllLabels(): Flow<List<Label>>

    fun getLabelsByFolderId(folderId: Long): Flow<List<Label>>

    fun getLabelById(id: Long): Flow<Label>

    suspend fun createLabel(label: Label) : Long

    suspend fun updateLabel(label: Label)

    suspend fun deleteLabel(label: Label)
}