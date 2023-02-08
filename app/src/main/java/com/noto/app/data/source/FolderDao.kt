package com.noto.app.data.source

import androidx.room.*
import com.noto.app.domain.model.Folder
import com.noto.app.domain.source.LocalFolderDataSource
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao : LocalFolderDataSource {

    @Query("SELECT * FROM folders")
    override fun getAllFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE is_vaulted = 0")
    override fun getAllUnvaultedFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE is_archived = 0 AND is_vaulted = 0")
    override fun getFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE is_archived = 1 AND is_vaulted = 0")
    override fun getArchivedFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE is_vaulted = 1 AND is_archived = 0")
    override fun getVaultedFolders(): Flow<List<Folder>>

    @Query("SELECT * FROM folders WHERE id = :folderId")
    override fun getFolderById(folderId: Long): Flow<Folder>

    @Insert
    override suspend fun createFolder(folder: Folder): Long

    @Update
    override suspend fun updateFolder(folder: Folder)

    @Delete
    override suspend fun deleteFolder(folder: Folder)

    @Query("DELETE FROM folders")
    override suspend fun clearFolders()
}