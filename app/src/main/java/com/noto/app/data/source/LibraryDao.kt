package com.noto.app.data.source

import androidx.room.*
import com.noto.app.domain.model.Library
import com.noto.app.domain.source.LibraryLocalDataSource
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao : LibraryLocalDataSource {

    @Query("SELECT * FROM libraries ORDER BY id DESC")
    override fun getLibraries(): Flow<List<Library>>

    @Query("SELECT * FROM libraries WHERE id = :libraryId")
    override fun getLibrary(libraryId: Long): Flow<Library>

    @Insert
    override suspend fun createLibrary(library: Library)

    @Update
    override suspend fun updateLibrary(library: Library)

    @Delete
    override suspend fun deleteLibrary(library: Library)

    @Query("SELECT COUNT(*) FROM notes WHERE library_id = :libraryId AND is_archived = 0")
    override suspend fun countLibraryNotes(libraryId: Long): Int

    @Update
    override suspend fun updateLibraries(libraries: List<Library>)

}