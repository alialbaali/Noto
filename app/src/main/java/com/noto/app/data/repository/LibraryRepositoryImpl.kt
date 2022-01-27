package com.noto.app.data.repository

import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.source.LocalFolderDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LibraryRepositoryImpl(
    private val dataSource: LocalFolderDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : LibraryRepository {

    override fun getAllLibraries(): Flow<List<Folder>> = dataSource.getAllFolders()

    override fun getLibraries(): Flow<List<Folder>> = dataSource.getFolders()

    override fun getArchivedLibraries(): Flow<List<Folder>> = dataSource.getArchivedFolders()

    override fun getVaultedLibraries(): Flow<List<Folder>> = dataSource.getVaultedFolders()

    override fun getLibraryById(libraryId: Long): Flow<Folder> = dataSource.getFolderById(libraryId)

    override suspend fun createLibrary(folder: Folder) = withContext(dispatcher) {
        val position = getLibraryPosition()
        dataSource.createFolder(folder.copy(position = position))
    }

    override suspend fun updateLibrary(folder: Folder) = withContext(dispatcher) {
        dataSource.updateFolder(folder)
    }

    override suspend fun deleteLibrary(folder: Folder) = withContext(dispatcher) {
        dataSource.deleteFolder(folder)
    }

    override suspend fun clearLibraries() = dataSource.clearFolders()

    private suspend fun getLibraryPosition() = dataSource.getFolders()
        .filterNotNull()
        .first()
        .count()
}