package com.noto.app.domain.source

import com.noto.app.domain.model.Library
import kotlinx.coroutines.flow.Flow

interface LocalLibraryDataSource {

    fun getAllLibraries(): Flow<List<Library>>

    fun getLibraries(): Flow<List<Library>>

    fun getArchivedLibraries(): Flow<List<Library>>

    fun getLibraryById(libraryId: Long): Flow<Library>

    suspend fun createLibrary(library: Library): Long

    suspend fun updateLibrary(library: Library)

    suspend fun deleteLibrary(library: Library)

    suspend fun clearLibraries()
}