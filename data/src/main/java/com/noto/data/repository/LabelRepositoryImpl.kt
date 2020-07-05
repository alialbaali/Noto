package com.noto.data.repository

import com.noto.data.source.local.LabelLocalDataSource
import com.noto.domain.model.Label
import com.noto.domain.repository.LabelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LabelRepositoryImpl(private val dataSource: LabelLocalDataSource) : LabelRepository {

    override suspend fun createLabel(label: Label) = withContext(Dispatchers.IO) {
        dataSource.createLabel(label)
    }

    override suspend fun updateLabel(label: Label) = withContext(Dispatchers.IO) {
        dataSource.updateLabel(label)
    }

    override suspend fun deleteLabel(labelId: Long) = withContext(Dispatchers.IO) {
        dataSource.deleteLabel(labelId)
    }

    override fun getLabels(): Result<Flow<List<Label>>> = Result.success(dataSource.getLabels())

    override fun getLabelById(labelId: Long): Result<Flow<Label>> = Result.success(dataSource.getLabelById(labelId))

}