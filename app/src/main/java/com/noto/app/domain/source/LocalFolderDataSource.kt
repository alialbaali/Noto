package com.noto.app.domain.source

import com.noto.app.domain.model.Folder
import kotlinx.coroutines.flow.Flow

interface LocalFolderDataSource {

    fun getAllFolders(): Flow<List<Folder>>

    fun getAllUnvaultedFolders(): Flow<List<Folder>>

    fun getFolders(): Flow<List<Folder>>

    fun getArchivedFolders(): Flow<List<Folder>>

    fun getVaultedFolders(): Flow<List<Folder>>

    fun getFolderById(folderId: Long): Flow<Folder>

    suspend fun createFolder(folder: Folder): Long

    suspend fun updateFolder(folder: Folder)

    suspend fun deleteFolder(folder: Folder)

    suspend fun clearFolders()
}