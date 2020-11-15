package com.noto.domain.repository

import com.noto.domain.model.Library
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    fun getLibraries(): Flow<List<Library>>

    fun getLibraryById(libraryId: Long): Flow<Library>

    suspend fun createLibrary(library: Library)

    suspend fun updateLibrary(library: Library)

    suspend fun deleteLibrary(library: Library)

    suspend fun countLibraryNotes(libraryId: Long): Int

    suspend fun updateLibraries(libraries: List<Library>)

}