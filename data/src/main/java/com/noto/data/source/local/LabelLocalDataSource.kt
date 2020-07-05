package com.noto.data.source.local

import com.noto.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelLocalDataSource {

    suspend fun createLabel(label: Label)

    suspend fun updateLabel(label: Label)

    suspend fun deleteLabel(labelId: Long)

    fun getLabels(): Flow<List<Label>>

    fun getLabelById(labelId: Long): Flow<Label>

}