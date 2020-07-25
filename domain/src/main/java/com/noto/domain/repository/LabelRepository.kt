package com.noto.domain.repository

import com.noto.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelRepository {

    fun getLabels(): Flow<List<Label>>

    fun getLabel(labelId: Long): Flow<Label>

    suspend fun createLabel(label: Label)

    suspend fun updateLabel(label: Label)

    suspend fun deleteLabel(label: Label)

}