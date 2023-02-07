package com.noto.app.domain.repository

import com.noto.app.domain.model.Folder
import kotlinx.coroutines.flow.Flow

interface FolderRepository {

    fun getAllFolders(): Flow<List<Folder>>

    fun getAllUnvaultedFolders(): Flow<List<Folder>>

    fun getFolders(): Flow<List<Folder>>

    fun getArchivedFolders(): Flow<List<Folder>>

    fun getVaultedFolders(): Flow<List<Folder>>

    fun getFolderById(folderId: Long): Flow<Folder>

    suspend fun createFolder(folder: Folder, overridePosition: Boolean = true): Long

    suspend fun updateFolder(folder: Folder)

    suspend fun deleteFolder(folder: Folder)

    suspend fun clearFolders()
}