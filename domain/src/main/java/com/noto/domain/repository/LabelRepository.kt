package com.noto.domain.repository

import com.noto.domain.model.Label
import kotlinx.coroutines.flow.Flow

interface LabelRepository {

    suspend fun createLabel(label: Label)

    suspend fun updateLabel(label: Label)

    suspend fun deleteLabel(labelId: Long)

    fun getLabels(): Result<Flow<List<Label>>>

    fun getLabelById(labelId: Long): Result<Flow<Label>>

}