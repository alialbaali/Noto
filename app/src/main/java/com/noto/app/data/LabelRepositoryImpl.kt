package com.noto.app.data

import com.noto.app.domain.model.Label
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.source.LabelLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LabelRepositoryImpl(private val dataSource: LabelLocalDataSource) : LabelRepository {

    override fun getLabels(): Flow<List<Label>> = dataSource.getLabels()

    override fun getLabel(labelId: Long): Flow<Label> = dataSource.getLabel(labelId)

    override suspend fun createLabel(label: Label) = withContext(Dispatchers.IO) {
        dataSource.createLabel(label.copy(labelTitle = label.labelTitle.trim()))
    }

    override suspend fun updateLabel(label: Label) = withContext(Dispatchers.IO) {
        dataSource.updateLabel(label.copy(labelTitle = label.labelTitle.trim()))
    }

    override suspend fun deleteLabel(label: Label) = withContext(Dispatchers.IO) {
        dataSource.deleteLabel(label)
    }

}