package com.noto.app.data.repository

import com.noto.app.domain.model.Folder
import com.noto.app.domain.repository.FolderRepository
import com.noto.app.domain.source.LocalFolderDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FolderRepositoryImpl(
    private val dataSource: LocalFolderDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : FolderRepository {

    override fun getAllFolders(): Flow<List<Folder>> = dataSource.getAllFolders().flowOn(dispatcher)

    override fun getFolders(): Flow<List<Folder>> = dataSource.getFolders().flowOn(dispatcher)

    override fun getArchivedFolders(): Flow<List<Folder>> = dataSource.getArchivedFolders().flowOn(dispatcher)

    override fun getVaultedFolders(): Flow<List<Folder>> = dataSource.getVaultedFolders().flowOn(dispatcher)

    override fun getFolderById(folderId: Long): Flow<Folder> = dataSource.getFolderById(folderId).flowOn(dispatcher)

    override suspend fun createFolder(folder: Folder, overridePosition: Boolean) = withContext(dispatcher) {
        val position = if (overridePosition) getFolderPosition() else folder.position
        dataSource.createFolder(folder.copy(position = position))
    }

    override suspend fun updateFolder(folder: Folder) = withContext(dispatcher) {
        dataSource.updateFolder(folder)
    }

    override suspend fun deleteFolder(folder: Folder) = withContext(dispatcher) {
        dataSource.deleteFolder(folder)
    }

    override suspend fun clearFolders() = withContext(dispatcher) {
        dataSource.clearFolders()
    }

    private suspend fun getFolderPosition() = withContext(dispatcher) {
        dataSource.getFolders()
            .filterNotNull()
            .first()
            .count()
    }
}