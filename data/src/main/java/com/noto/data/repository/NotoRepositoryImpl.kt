package com.noto.data.repository

import com.noto.data.source.local.EntityStatusDataSource
import com.noto.data.source.local.NotoLocalDataSource
import com.noto.data.source.local.UserLocalDataSource
import com.noto.data.source.remote.NotoRemoteDataSource
import com.noto.domain.model.EntityStatus
import com.noto.domain.model.Noto
import com.noto.domain.model.Status
import com.noto.domain.model.Type
import com.noto.domain.repository.AUTH_SCHEME
import com.noto.domain.repository.NotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class NotoRepositoryImpl(
    private val userLocalSource: UserLocalDataSource,
    private val entityStatusDataSource: EntityStatusDataSource,
    private val localSource: NotoLocalDataSource,
    private val remoteSource: NotoRemoteDataSource
) : NotoRepository {

    override val userToken: String
        get() = AUTH_SCHEME.plus(userLocalSource.getUserToken())

    override suspend fun createNoto(noto: Noto) = withContext(Dispatchers.IO) {

        localSource.createNoto(noto)

        localSource.getNotos(noto.libraryId).collect { notos ->
            notos.lastOrNull()?.let {
                entityStatusDataSource.createEntityStatus(EntityStatus(libraryId = noto.libraryId, notoId = it.notoId, type = Type.NOTO, status = Status.CREATED))
            }

        }

    }

    override suspend fun deleteNoto(noto: Noto) = withContext(Dispatchers.IO) {

        localSource.deleteNoto(noto)

        entityStatusDataSource.createEntityStatus(EntityStatus(libraryId = noto.libraryId, notoId = noto.notoId, type = Type.NOTO, status = Status.DELETED))

    }

    override suspend fun updateNoto(noto: Noto) = withContext(Dispatchers.IO) {

        localSource.updateNoto(noto)

        entityStatusDataSource.createEntityStatus(EntityStatus(libraryId = noto.libraryId, notoId = noto.notoId, type = Type.NOTO, status = Status.UPDATED))

    }

    override suspend fun getNotos(libraryId: Long): Result<Flow<List<Noto>>> = Result.success(localSource.getNotos(libraryId))


    override suspend fun getNoto(notoId: Long): Result<Flow<Noto>> = Result.success(localSource.getNotoById(notoId))


    override suspend fun countLibraryNotos(libraryId: Long): Int = withContext(Dispatchers.IO) {
        localSource.countLibraryNotos(libraryId)
    }

}
