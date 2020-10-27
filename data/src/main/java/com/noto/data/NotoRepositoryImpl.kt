package com.noto.data

import com.noto.domain.local.NotoLocalDataSource
import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoWithLabels
import com.noto.domain.model.toNotoLabel
import com.noto.domain.repository.NotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class NotoRepositoryImpl(private val dataSource: NotoLocalDataSource) : NotoRepository {

    override fun getNotosByLibraryId(libraryId: Long): Flow<List<Noto>> = dataSource.getNotosByLibraryId(libraryId)

    override fun getArchivedNotosByLibraryId(libraryId: Long): Flow<List<Noto>> = dataSource.getArchivedNotosByLibraryId(libraryId)

    override fun getNotoById(notoId: Long): Flow<Noto> = dataSource.getNotoById(notoId)

    override suspend fun createNoto(noto: Noto) = withContext(Dispatchers.IO) {
        dataSource.createNoto(noto.copy(notoTitle = noto.notoTitle.trim(), notoBody = noto.notoBody.trim()))
    }

    override suspend fun updateNoto(noto: Noto) = withContext(Dispatchers.IO) {
        dataSource.updateNoto(noto.copy(notoTitle = noto.notoTitle.trim(), notoBody = noto.notoBody.trim()))
    }

    override suspend fun deleteNoto(noto: Noto) = withContext(Dispatchers.IO) {
        dataSource.deleteNoto(noto)
    }

    override suspend fun getNotoWithLabels(notoId: Long): Flow<Result<NotoWithLabels>> = dataSource.getNotoWithLabels(notoId).map { Result.success(it) }

    override suspend fun createNotoWithLabels(noto: Noto, labels: Set<Label>) = withContext(Dispatchers.IO) {
        val notoLabels = labels.map { it.toNotoLabel(noto.notoId) }.toSet()
        dataSource.createNotoWithLabels(noto, notoLabels)
    }

    override suspend fun updateNotoWithLabels(noto: Noto, labels: Set<Label>) = withContext(Dispatchers.IO) {
        val notoLabels = labels.map { it.toNotoLabel(noto.notoId) }.toSet()
        dataSource.updateNotoWithLabels(noto, notoLabels)
    }

    override suspend fun deleteNotoWithLabels(notoId: Long) = withContext(Dispatchers.IO) {
        dataSource.deleteNotoWithLabels(notoId)
    }

}
