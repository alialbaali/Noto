package com.noto.app.data.repository

import com.noto.app.domain.model.Label
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.source.LocalLabelDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LabelRepositoryImpl(
    private val dataSource: LocalLabelDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LabelRepository {

    override fun getAllLabels(): Flow<List<Label>> = dataSource.getAllLabels()

    override fun getLabelsByFolderId(folderId: Long): Flow<List<Label>> = dataSource.getLabelsByFolderId(folderId)

    override fun getLabelById(id: Long): Flow<Label> = dataSource.getLabelById(id)

    override suspend fun createLabel(label: Label) = withContext(dispatcher) {
        val position = getLabelPosition(label.folderId)
        dataSource.createLabel(label.copy(position = position))
    }

    override suspend fun updateLabel(label: Label) = withContext(dispatcher) {
        dataSource.updateLabel(label.copy(title = label.title.trim()))
    }

    override suspend fun deleteLabel(label: Label) = withContext(dispatcher) {
        dataSource.deleteLabel(label)
    }

    override suspend fun clearLabels() = withContext(dispatcher) {
        dataSource.clearLabels()
    }

    private suspend fun getLabelPosition(folderId: Long) = dataSource.getLabelsByFolderId(folderId)
        .filterNotNull()
        .first()
        .count()
}