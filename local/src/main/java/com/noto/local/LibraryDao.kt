package com.noto.local

import androidx.room.*
import com.alialbaali.noto.data.source.local.LibraryLocalDataSource
import com.noto.domain.model.Library

@Dao
interface LibraryDao: LibraryLocalDataSource {

    @Query("SELECT * FROM libraries")
    override suspend fun getLibraries(): List<Library>

    @Insert
    override suspend fun createLibrary(library: Library)

    @Query(value = "SELECT * FROM libraries WHERE library_id = :libraryId")
    override suspend fun getLibraryById(libraryId: Long): Library

    @Query("SELECT COUNT(*) FROM notos WHERE library_id = :libraryId")
    override suspend fun countNotos(libraryId: Long): Int

    @Transaction
    @Update
    override suspend fun updateLibraries(libraries: List<Library>)

    @Update
    override fun updateLibrary(library: Library)

}