package com.noto.data.repository

import com.noto.data.source.local.EntityStatusDataSource
import com.noto.data.source.local.LibraryLocalDataSource
import com.noto.data.source.local.UserLocalDataSource
import com.noto.data.source.remote.LibraryRemoteDataSource
import com.noto.domain.model.EntityStatus
import com.noto.domain.model.Library
import com.noto.domain.model.Status
import com.noto.domain.model.Type
import com.noto.domain.repository.AUTH_SCHEME
import com.noto.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class LibraryRepositoryImpl(
    private val userLocalSource: UserLocalDataSource,
    private val entityStatusDataSource: EntityStatusDataSource,
    private val localSource: LibraryLocalDataSource,
    private val remoteSource: LibraryRemoteDataSource
) : LibraryRepository {

    override val userToken: String
        get() = AUTH_SCHEME.plus(userLocalSource.getUserToken())

    override suspend fun createLibrary(library: Library) = withContext(Dispatchers.IO) {

        localSource.createLibrary(library)

        localSource.getLibraries().collect { libraries ->
            libraries.lastOrNull()?.let {
                entityStatusDataSource.createEntityStatus(EntityStatus(libraryId = it.libraryId, type = Type.LIBRARY, status = Status.CREATED))
            }
        }

    }

    override suspend fun deleteLibrary(library: Library) = withContext(Dispatchers.IO) {

        localSource.deleteLibrary(library)

        entityStatusDataSource.createEntityStatus(EntityStatus(libraryId = library.libraryId, type = Type.LIBRARY, status = Status.DELETED))
    }

    override suspend fun updateLibrary(library: Library) = withContext(Dispatchers.IO) {

        localSource.updateLibrary(library)

        entityStatusDataSource.createEntityStatus(EntityStatus(libraryId = library.libraryId, type = Type.LIBRARY, status = Status.UPDATED))

    }

    override suspend fun getLibraries(): Result<Flow<List<Library>>> = Result.success(localSource.getLibraries())


    override suspend fun getLibraryById(libraryId: Long): Result<Flow<Library>> = withContext(Dispatchers.IO) {
        Result.success(localSource.getLibraryById(libraryId))
    }

    override suspend fun countNotos(libraryId: Long): Int = withContext(Dispatchers.IO) {
        localSource.countNotos(libraryId)
    }
}