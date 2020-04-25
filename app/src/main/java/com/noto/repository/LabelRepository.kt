package com.noto.repository

import com.noto.database.LabelDao
import com.noto.domain.Label
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class LabelRepository(private val dao: LabelDao) {

    suspend fun insertLabel(label: Label) {
        withContext(Dispatchers.IO) {
            dao.insertLabel(label)
        }
    }

    suspend fun updateLabel(label: Label) {
        withContext(Dispatchers.IO) {
            dao.updateLabel(label)
        }
    }

    suspend fun deleteLabel(label: Label) {
        withContext(Dispatchers.IO) {
            dao.deleteLabel(label)
        }
    }

    suspend fun getLabels(): List<Label> {
        return withContext(Dispatchers.IO) {
            dao.getLabels()
        }
    }
}