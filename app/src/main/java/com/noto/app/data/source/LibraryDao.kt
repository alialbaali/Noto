package com.noto.app.data.source

import androidx.room.*
import com.noto.app.domain.model.Library
import com.noto.app.domain.source.LocalLibraryDataSource
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao : LocalLibraryDataSource {

    @Query("SELECT * FROM libraries ORDER BY id DESC")
    override fun getLibraries(): Flow<List<Library>>

    @Query("SELECT * FROM libraries WHERE id = :libraryId")
    override fun getLibraryById(libraryId: Long): Flow<Library>

    @Insert
    override suspend fun createLibrary(library: Library)

    @Update
    override suspend fun updateLibrary(library: Library)

    @Delete
    override suspend fun deleteLibrary(library: Library)

    @Query("DELETE FROM libraries")
    override suspend fun clearLibraries()
}