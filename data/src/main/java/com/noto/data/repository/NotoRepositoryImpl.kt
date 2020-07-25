package com.noto.data.repository

import com.noto.data.source.local.NotoLocalDataSource
import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoWithLabels
import com.noto.domain.model.toNotoLabel
import com.noto.domain.repository.NotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NotoRepositoryImpl(private val notoLocalDataSource: NotoLocalDataSource) : NotoRepository {

    override fun getNotos(): Flow<List<Noto>> = notoLocalDataSource.getNotos()

    override fun getNoto(notoId: Long): Flow<Noto> = notoLocalDataSource.getNoto(notoId)

    override suspend fun createNoto(noto: Noto) = withContext(Dispatchers.IO) {
        notoLocalDataSource.createNoto(noto.copy(notoTitle = noto.notoTitle.trim(), notoBody = noto.notoBody.trim()))
    }

    override suspend fun updateNoto(noto: Noto) = withContext(Dispatchers.IO) {
        notoLocalDataSource.updateNoto(noto.copy(notoTitle = noto.notoTitle.trim(), notoBody = noto.notoBody.trim()))
    }

    override suspend fun deleteNoto(noto: Noto) = withContext(Dispatchers.IO) {
        notoLocalDataSource.deleteNoto(noto)
    }

    override suspend fun getNotoWithLabels(notoId: Long): Flow<Result<NotoWithLabels>> = notoLocalDataSource.getNotoWithLabels(notoId).map { Result.success(it) }

    override suspend fun createNotoWithLabels(noto: Noto, labels: Set<Label>) = withContext(Dispatchers.IO) {
        val notoLabels = labels.map { it.toNotoLabel(noto.notoId) }.toSet()
        notoLocalDataSource.createNotoWithLabels(noto, notoLabels)
    }

    override suspend fun updateNotoWithLabels(noto: Noto, labels: Set<Label>) = withContext(Dispatchers.IO) {
        val notoLabels = labels.map { it.toNotoLabel(noto.notoId) }.toSet()
        notoLocalDataSource.updateNotoWithLabels(noto, notoLabels)
    }

    override suspend fun deleteNotoWithLabels(notoId: Long) = withContext(Dispatchers.IO) {
        notoLocalDataSource.deleteNotoWithLabels(notoId)
    }

}
