package com.noto.app.domain.source

import com.noto.app.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelLocalDataSource {

    fun getLabels(): Flow<List<Label>>

    fun getLabel(labelId: Long): Flow<Label>

    suspend fun createLabel(label: Label)

    suspend fun updateLabel(label: Label)

    suspend fun deleteLabel(label: Label)

}