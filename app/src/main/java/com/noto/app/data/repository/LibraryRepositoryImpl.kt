package com.noto.app.data.repository

import com.noto.app.domain.model.Library
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.source.LocalLibraryDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LibraryRepositoryImpl(
    private val dataSource: LocalLibraryDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LibraryRepository {

    override fun getAllLibraries(): Flow<List<Library>> = dataSource.getAllLibraries()

    override fun getLibraries(): Flow<List<Library>> = dataSource.getLibraries()

    override fun getArchivedLibraries(): Flow<List<Library>> = dataSource.getArchivedLibraries()

    override fun getVaultedLibraries(): Flow<List<Library>> = dataSource.getVaultedLibraries()

    override fun getLibraryById(libraryId: Long): Flow<Library> = dataSource.getLibraryById(libraryId)

    override suspend fun createLibrary(library: Library) = withContext(dispatcher) {
        val position = getLibraryPosition()
        dataSource.createLibrary(library.copy(position = position))
    }

    override suspend fun updateLibrary(library: Library) = withContext(dispatcher) {
        dataSource.updateLibrary(library)
    }

    override suspend fun deleteLibrary(library: Library) = withContext(dispatcher) {
        dataSource.deleteLibrary(library)
    }

    override suspend fun clearLibraries() = dataSource.clearLibraries()

    private suspend fun getLibraryPosition() = dataSource.getLibraries()
        .filterNotNull()
        .first()
        .count()
}