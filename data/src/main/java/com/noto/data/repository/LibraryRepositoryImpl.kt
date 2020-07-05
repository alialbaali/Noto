package com.noto.data.repository

import com.noto.data.source.local.LibraryLocalDataSource
import com.noto.domain.model.Library
import com.noto.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withContext

class LibraryRepositoryImpl(private val localSource: LibraryLocalDataSource) : LibraryRepository {

    override suspend fun createLibrary(library: Library) = withContext(Dispatchers.IO) {

        localSource.createLibrary(library)

    }

    override suspend fun deleteLibrary(library: Library) = withContext(Dispatchers.IO) {

        localSource.deleteLibrary(library)

    }

    override suspend fun updateLibrary(library: Library) = withContext(Dispatchers.IO) {

        localSource.updateLibrary(library)

    }

    override suspend fun getLibraries(): Result<Flow<List<Library>>> = Result.success(localSource.getLibraries())


    override suspend fun getLibraryById(libraryId: Long): Result<Flow<Library>> = withContext(Dispatchers.IO) {
        Result.success(localSource.getLibraryById(libraryId))
    }

    override suspend fun countNotos(libraryId: Long): Int = withContext(Dispatchers.IO) {
        localSource.countNotos(libraryId)
    }
}