package com.alialbaali.noto.data.source.local

import com.noto.domain.model.Library
import kotlinx.coroutines.flow.Flow

interface LibraryLocalDataSource {
    fun getLibraries(): Flow<List<Library>>

    suspend fun createLibrary(library: Library)

    fun getLibraryById(libraryId: Long): Flow<Library>

    suspend fun deleteLibrary(library: Library)

    suspend fun countNotos(libraryId: Long): Int

    suspend fun updateLibraries(libraries: List<Library>)

    suspend fun updateLibrary(library: Library)
}