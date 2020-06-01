package com.alialbaali.noto.data.repository

import com.alialbaali.noto.data.source.local.LibraryLocalDataSource
import com.alialbaali.noto.data.source.remote.LibraryRemoteDataSource
import com.noto.domain.model.Library
import com.noto.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LibraryRepositoryImpl(private val local: LibraryLocalDataSource, private val remote: LibraryRemoteDataSource) : LibraryRepository {

    override suspend fun createLibrary(library: Library) {
        withContext(Dispatchers.IO) {
            local.createLibrary(library)
        }
    }

    override suspend fun deleteLibrary(library: Library) {
        withContext(Dispatchers.IO) {
            local.deleteLibrary(library)
        }
    }

    override suspend fun updateLibrary(library: Library) {
        withContext(Dispatchers.IO) {
            local.updateLibrary(library)
        }
    }

    override suspend fun getLibraries(): Result<Flow<List<Library>>> {
        return Result.success(local.getLibraries())
    }

    override suspend fun getLibraryById(libraryId: Long): Result<Flow<Library>> {
        return withContext(Dispatchers.IO) {
            Result.success(local.getLibraryById(libraryId))
        }
    }

    override suspend fun countNotos(libraryId: Long): Int {
        return withContext(Dispatchers.IO) {
            local.countNotos(libraryId)
        }
    }

}