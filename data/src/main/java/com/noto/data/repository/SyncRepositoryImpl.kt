package com.noto.data.repository

import com.noto.data.repository.util.tryCatching
import com.noto.data.source.local.EntityStatusDataSource
import com.noto.data.source.local.LibraryLocalDataSource
import com.noto.data.source.local.NotoLocalDataSource
import com.noto.data.source.local.UserLocalDataSource
import com.noto.data.source.remote.LibraryRemoteDataSource
import com.noto.data.source.remote.NotoRemoteDataSource
import com.noto.domain.model.EntityStatus
import com.noto.domain.model.Status
import com.noto.domain.model.Type
import com.noto.domain.repository.AUTH_SCHEME
import com.noto.domain.repository.SyncRepository
import kotlinx.coroutines.flow.single

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

    override suspend fun fetchData() {
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

    private lateinit var entitiesStatus: List<EntityStatus>

    override suspend fun syncData() {

        entityStatusDataSource.getEntitiesStatus().let { list ->
            entitiesStatus = list
        }

        entitiesStatus.forEach { entityStatus ->

            when (entityStatus.type) {

                Type.LIBRARY -> {

                    val library = libraryLocalDataSource.getLibraryById(entityStatus.id).single()

                    when (entityStatus.status) {

                        Status.CREATED ->

                            libraryRemoteDataSource.createLibrary(userToken, library).run {
                                if (this.success) entityStatusDataSource.deleteEntityStatus(entityStatus)
                            }

                        Status.UPDATED ->

                            libraryRemoteDataSource.updateLibrary(userToken, library).run {
                                if (this.success) entityStatusDataSource.deleteEntityStatus(entityStatus)
                            }

                        Status.DELETED ->

                            libraryRemoteDataSource.deleteLibrary(userToken, library.libraryId).run {
                                if (this.success) entityStatusDataSource.deleteEntityStatus(entityStatus)
                            }

                    }
                }

                Type.NOTO -> {

                    val noto = notoLocalDataSource.getNotoById(entityStatus.id).single()

                    when (entityStatus.status) {

                        Status.CREATED ->

                            notoRemoteDataSource.createNoto(userToken, noto).run {
                                if (this.success) entityStatusDataSource.deleteEntityStatus(entityStatus)
                            }

                        Status.UPDATED ->

                            notoRemoteDataSource.updateNoto(userToken, noto).run {
                                if (this.success) entityStatusDataSource.deleteEntityStatus(entityStatus)
                            }

                        Status.DELETED ->

                            notoRemoteDataSource.deleteNoto(userToken, noto.libraryId, noto.notoId).run {
                                if (this.success) entityStatusDataSource.deleteEntityStatus(entityStatus)
                            }

                    }

                }
            }
        }
    }


}