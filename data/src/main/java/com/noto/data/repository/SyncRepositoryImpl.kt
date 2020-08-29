package com.noto.data.repository

import com.noto.data.repository.util.tryCatching
import com.noto.data.source.local.EntityStatusDataSource
import com.noto.data.source.local.LibraryLocalDataSource
import com.noto.data.source.local.NotoLocalDataSource
import com.noto.data.source.local.UserLocalDataSource
import com.noto.data.source.remote.LibraryRemoteDataSource
import com.noto.data.source.remote.NotoRemoteDataSource
import com.noto.domain.model.Status
import com.noto.domain.model.Type
import com.noto.domain.repository.AUTH_SCHEME
import com.noto.domain.repository.SyncRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncRepositoryImpl(
    private val userLocalDataSource: UserLocalDataSource,
    private val entityStatusDataSource: EntityStatusDataSource,
    private val libraryLocalDataSource: LibraryLocalDataSource,
    private val libraryRemoteDataSource: LibraryRemoteDataSource,
    private val notoLocalDataSource: NotoLocalDataSource,
    private val notoRemoteDataSource: NotoRemoteDataSource
) : SyncRepository {

    override val userToken: String
        get() = AUTH_SCHEME.plus(userLocalDataSource.getUserToken())

    override suspend fun fetchData(): Result<Unit> = withContext(Dispatchers.IO) {
        tryCatching {
            val response = libraryRemoteDataSource.getLibraries(userToken)
            if (response.success) {
                response.data?.forEach { library ->
                    libraryLocalDataSource.createLibrary(library)
                }
            }
        }

        tryCatching {
            val response = notoRemoteDataSource.getNotos(userToken)
            if (response.success) {
                response.data?.forEach { noto ->
                    notoLocalDataSource.createNoto(noto)
                }
            }
        }
    }


    override suspend fun syncData() = withContext(Dispatchers.IO) {

        tryCatching {

            entityStatusDataSource.getEntitiesStatus().forEach { entityStatus ->

                when (entityStatus.type) {


                    Type.LIBRARY -> {

                        val library = libraryLocalDataSource.getLibrary(entityStatus.libraryId)

                        when (entityStatus.status) {

                            Status.CREATED ->

                                libraryRemoteDataSource.createLibrary(userToken, library).run {
                                    if (this.success) entityStatusDataSource.deleteEntityStatusById(entityStatus.entityStatusId)
                                }

                            Status.UPDATED ->

                                libraryRemoteDataSource.updateLibrary(userToken, library).run {
                                    if (this.success) entityStatusDataSource.deleteEntityStatusById(entityStatus.entityStatusId)
                                }

                            Status.DELETED ->

                                libraryRemoteDataSource.deleteLibrary(userToken, entityStatus.libraryId).run {
                                    entityStatusDataSource.deleteEntityStatusById(entityStatus.entityStatusId)
                                }

                        }
                    }

                    Type.NOTO -> {

                        val noto = notoLocalDataSource.getNoto(entityStatus.notoId!!)

                        when (entityStatus.status) {

                            Status.CREATED ->

                                notoRemoteDataSource.createNoto(userToken, noto).run {
                                    if (this.success) entityStatusDataSource.deleteEntityStatusById(entityStatus.entityStatusId)
                                }

                            Status.UPDATED ->

                                notoRemoteDataSource.updateNoto(userToken, noto).run {
                                    if (this.success) entityStatusDataSource.deleteEntityStatusById(entityStatus.entityStatusId)
                                }

                            Status.DELETED ->

                                notoRemoteDataSource.deleteNoto(userToken, entityStatus.libraryId, entityStatus.notoId!!).run {
                                    entityStatusDataSource.deleteEntityStatusById(entityStatus.entityStatusId)
                                }

                        }

                    }
                }
            }
        }
    }
}
