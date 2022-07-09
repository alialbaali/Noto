package com.noto.app.domain.repository

import com.noto.app.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelRepository {

    fun getAllLabels(): Flow<List<Label>>

    fun getLabelsByFolderId(folderId: Long): Flow<List<Label>>

    fun getLabelById(id: Long): Flow<Label>

    suspend fun createLabel(label: Label): Long

    suspend fun updateLabel(label: Label)

    suspend fun deleteLabel(label: Label)

    suspend fun clearLabels()
}