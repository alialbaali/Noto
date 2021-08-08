package com.noto.app.domain.source

import com.noto.app.domain.model.Library
import kotlinx.coroutines.flow.Flow

interface LocalLibraryDataSource {

    fun getLibraries(): Flow<List<Library>>

    fun getLibrary(libraryId: Long): Flow<Library>

    suspend fun createLibrary(library: Library)

    suspend fun updateLibrary(library: Library)

    suspend fun deleteLibrary(library: Library)

    suspend fun countLibraryNotes(libraryId: Long): Int
}