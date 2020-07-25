package com.noto.data.source.fake

import com.noto.data.source.local.LabelLocalDataSource
import com.noto.domain.model.Label
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLabelDao : LabelLocalDataSource {

    private val labels = mutableListOf<Label>()

    override suspend fun createLabel(label: Label) {
        labels.add(label.copy(labelId = labels.size.toLong()))
    }

    override suspend fun updateLabel(label: Label) = labels.replaceWith(label) {
        it.labelId == label.labelId
    }

    override suspend fun deleteLabel(labelId: Long) {
        labels.removeIf { it.labelId == labelId }
    }

    override fun getLabels(): Flow<List<Label>> = flowOf(labels)

    override fun getLabelById(labelId: Long): Flow<Label> = flowOf(labels.first { it.labelId == labelId })

}

fun <T> MutableList<T>.replaceWith(value: T, predicate: (T) -> Boolean) {
    val result = first(predicate)
    this[indexOf(result)] = value
}