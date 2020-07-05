package com.noto.data.repository

import com.noto.data.source.local.NotoLocalDataSource
import com.noto.domain.model.Noto
import com.noto.domain.repository.NotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class NotoRepositoryImpl(private val localSource: NotoLocalDataSource) : NotoRepository {

    override suspend fun createNoto(noto: Noto) = withContext(Dispatchers.IO) {

        localSource.createNoto(noto.apply {
            notoTitle = notoTitle.trim()
            notoBody = notoBody.trim()
        })

    }

    override suspend fun deleteNoto(noto: Noto) = withContext(Dispatchers.IO) {

        localSource.deleteNoto(noto)

    }

    override suspend fun updateNoto(noto: Noto) = withContext(Dispatchers.IO) {

        localSource.updateNoto(noto.apply {
            notoTitle = notoTitle.trim()
            notoBody = notoBody.trim()
        })

    }

    override suspend fun getNotos(libraryId: Long): Result<Flow<List<Noto>>> = Result.success(localSource.getNotos(libraryId))

    override suspend fun getArchivedNotos(): Result<Flow<List<Noto>>> = Result.success(localSource.getArchivedNotos())

    override suspend fun getNoto(notoId: Long): Result<Flow<Noto>> = Result.success(localSource.getNotoById(notoId))

    override suspend fun countLibraryNotos(libraryId: Long): Int = withContext(Dispatchers.IO) {
        localSource.countLibraryNotos(libraryId)
    }

    override suspend fun getAllNotos(): Result<Flow<List<Noto>>> = Result.success(localSource.getAllNotos())

}
