package com.noto.app.data.repository

import com.noto.app.domain.model.Folder
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

    override fun getAllLibraries(): Flow<List<Folder>> = dataSource.getAllLibraries()

    override fun getLibraries(): Flow<List<Folder>> = dataSource.getLibraries()

    override fun getArchivedLibraries(): Flow<List<Folder>> = dataSource.getArchivedLibraries()

    override fun getVaultedLibraries(): Flow<List<Folder>> = dataSource.getVaultedLibraries()

    override fun getLibraryById(libraryId: Long): Flow<Folder> = dataSource.getLibraryById(libraryId)

    override suspend fun createLibrary(folder: Folder) = withContext(dispatcher) {
        val position = getLibraryPosition()
        dataSource.createLibrary(folder.copy(position = position))
    }

    override suspend fun updateLibrary(folder: Folder) = withContext(dispatcher) {
        dataSource.updateLibrary(folder)
    }

    override suspend fun deleteLibrary(folder: Folder) = withContext(dispatcher) {
        dataSource.deleteLibrary(folder)
    }

    override suspend fun clearLibraries() = dataSource.clearLibraries()

    private suspend fun getLibraryPosition() = dataSource.getLibraries()
        .filterNotNull()
        .first()
        .count()
}