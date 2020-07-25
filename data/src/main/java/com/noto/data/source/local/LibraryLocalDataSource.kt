package com.noto.data.source.local

import com.noto.domain.model.Library
import kotlinx.coroutines.flow.Flow

interface LibraryLocalDataSource {

    fun getLibraries(): Flow<List<Library>>

    fun getLibrary(libraryId: Long): Flow<Library>

    suspend fun createLibrary(library: Library)

    suspend fun updateLibrary(library: Library)

    suspend fun deleteLibrary(library: Library)

    suspend fun countLibraryNotos(libraryId: Long): Int

}