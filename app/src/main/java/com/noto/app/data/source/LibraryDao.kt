package com.noto.app.data.source

import androidx.room.*
import com.noto.app.domain.model.Library
import com.noto.app.domain.source.LocalLibraryDataSource
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao : LocalLibraryDataSource {

    @Query("SELECT * FROM libraries")
    override fun getAllLibraries(): Flow<List<Library>>

    @Query("SELECT * FROM libraries WHERE is_archived = 0 AND is_vaulted = 0")
    override fun getLibraries(): Flow<List<Library>>

    @Query("SELECT * FROM libraries WHERE is_archived = 1 AND is_vaulted = 0")
    override fun getArchivedLibraries(): Flow<List<Library>>

    @Query("SELECT * FROM libraries WHERE is_vaulted = 1 AND is_archived = 0")
    override fun getVaultedLibraries(): Flow<List<Library>>

    @Query("SELECT * FROM libraries WHERE id = :libraryId")
    override fun getLibraryById(libraryId: Long): Flow<Library>

    @Insert
    override suspend fun createLibrary(library: Library): Long

    @Update
    override suspend fun updateLibrary(library: Library)

    @Delete
    override suspend fun deleteLibrary(library: Library)

    @Query("DELETE FROM libraries")
    override suspend fun clearLibraries()
}