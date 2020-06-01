package com.alialbaali.noto.data.repository

import com.alialbaali.noto.data.source.local.NotoLocalDataSource
import com.alialbaali.noto.data.source.remote.NotoRemoteDataSource
import com.noto.domain.model.Label
import com.noto.domain.model.Noto
import com.noto.domain.model.NotoLabel
import com.noto.domain.repository.NotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NotoRepositoryImpl(private val localSource: NotoLocalDataSource, private val remoteSource: NotoRemoteDataSource) : NotoRepository {

    override suspend fun createNoto(noto: Noto, labels: List<Label>) {
        withContext(Dispatchers.IO) {
            val notoLabels = mutableListOf<NotoLabel>()

            labels.forEach { label ->
                notoLabels.add(NotoLabel(noto.notoId, label.labelId))
            }

            localSource.createNoto(noto, notoLabels)
        }
    }

    override suspend fun deleteNoto(noto: Noto, labels: List<Label>) {
        withContext(Dispatchers.IO) {
            localSource.deleteNoto(noto)
        }
    }

    override suspend fun updateNoto(noto: Noto, labels: List<Label>) {
        withContext(Dispatchers.IO) {
            val notoLabels = mutableListOf<NotoLabel>()

            labels.forEach { label ->
                notoLabels.add(NotoLabel(noto.notoId, label.labelId))
            }

            localSource.updateNoto(noto, notoLabels)
        }
    }

    override suspend fun getNotos(libraryId: Long): Result<Flow<List<Noto>>> {
        return withContext(Dispatchers.IO) {
            Result.success(localSource.getNotos(libraryId))
        }
    }

    override suspend fun getNoto(notoId: Long): Result<Pair<Flow<Noto>, MutableList<Label>>> {
        return withContext(Dispatchers.IO) {
            val notoLabels = localSource.getNotoLabels(notoId)
            val labels = localSource.getLabels()
            val filteredLabels = mutableListOf<Label>()

            labels.forEach { label ->
                if (notoLabels.any { notoLabel -> notoLabel.labelId == label.labelId }) {
                    filteredLabels.add(label)
                }
            }

            val noto = localSource.getNotoById(notoId)
            Result.success(Pair(noto, filteredLabels))
        }
    }
}