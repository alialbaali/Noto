package com.noto.local

import androidx.room.*
import com.noto.data.source.local.LibraryLocalDataSource
import com.noto.domain.model.Library
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao : LibraryLocalDataSource {

    @Query("SELECT * FROM libraries ORDER BY library_id DESC")
    override fun getLibraries(): Flow<List<Library>>

    @Query(value = "SELECT * FROM libraries WHERE library_id = :libraryId")
    override fun getLibrary(libraryId: Long): Flow<Library>

    @Insert
    override suspend fun createLibrary(library: Library)

    @Update
    override suspend fun updateLibrary(library: Library)

    @Delete
    override suspend fun deleteLibrary(library: Library)

    @Query("SELECT COUNT(*) FROM notos WHERE library_id = :libraryId AND noto_is_archived = 0")
    override suspend fun countLibraryNotos(libraryId: Long): Int

}