package com.noto.app.data.repository

import com.noto.app.domain.model.Library
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.source.LocalLibraryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LibraryRepositoryImpl(
    private val dataSource: LocalLibraryDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LibraryRepository {

    override fun getLibraries(): Flow<List<Library>> = dataSource.getLibraries()

    override fun getLibraryById(libraryId: Long): Flow<Library> = dataSource.getLibraryById(libraryId)

    override suspend fun createLibrary(library: Library) = withContext(dispatcher) {
        dataSource.createLibrary(library)
    }

    override suspend fun updateLibrary(library: Library) = withContext(dispatcher) {
        dataSource.updateLibrary(library)
    }

    override suspend fun deleteLibrary(library: Library) = withContext(dispatcher) {
        dataSource.deleteLibrary(library)
    }

    override suspend fun clearLibraries() = dataSource.clearLibraries()
}