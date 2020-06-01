package com.alialbaali.noto.data.repository

import com.alialbaali.noto.data.source.local.LabelLocalDataSource
import com.alialbaali.noto.data.source.remote.LabelRemoteDataSource
import com.noto.domain.model.Label
import com.noto.domain.repository.LabelRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LabelRepositoryImpl(private val local: LabelLocalDataSource, private val remote: LabelRemoteDataSource) :
    LabelRepository {

    override suspend fun createLabel(label: Label) {
         withContext(Dispatchers.IO) {
            Result.success(local.createLabel(label))
        }
    }

    override suspend fun updateLabel(label: Label) {
        withContext(Dispatchers.IO) {
            Result.success(local.updateLabel(label))
        }
    }

    override suspend fun getLabels(): Result<List<Label>> {
        return withContext(Dispatchers.IO) {
            Result.success(local.getLabels())
        }
    }

    override suspend fun getLabel(labelId: Long): Result<Label> {
        return withContext(Dispatchers.IO) {
            Result.success(local.getLabel())
        }
    }

    override suspend fun deleteLabel(label: Label) {
        withContext(Dispatchers.IO) {
            Result.success(local.deleteLabel(label))
        }
    }
}