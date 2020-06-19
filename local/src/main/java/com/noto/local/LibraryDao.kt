package com.noto.local

import androidx.room.*
import com.noto.data.source.local.LibraryLocalDataSource
import com.noto.domain.model.Library
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao : LibraryLocalDataSource {

    @Delete
    override suspend fun deleteLibrary(library: Library)

    @Query("SELECT * FROM libraries ORDER BY library_id DESC")
    override fun getLibraries(): Flow<List<Library>>

    @Insert
    override suspend fun createLibrary(library: Library)

    @Query(value = "SELECT * FROM libraries WHERE library_id = :libraryId")
    override fun getLibraryById(libraryId: Long): Flow<Library>

    @Query("SELECT COUNT(*) FROM notos WHERE library_id = :libraryId")
    override suspend fun countNotos(libraryId: Long): Int

    @Transaction
    @Update
    override suspend fun updateLibraries(libraries: List<Library>)

    @Update
    override suspend fun updateLibrary(library: Library)

    @Insert
    override suspend fun createLibrary(libraries: List<Library>)
}