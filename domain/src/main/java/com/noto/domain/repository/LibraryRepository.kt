package com.noto.domain.repository

import com.noto.domain.model.Library
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    suspend fun createLibrary(library: Library)

    suspend fun deleteLibrary(library: Library)

    suspend fun updateLibrary(library: Library)

    suspend fun getLibraries(): Result<Flow<List<Library>>>

    suspend fun getLibraryById(libraryId: Long): Result<Flow<Library>>

    suspend fun countLibraryNotos(libraryId: Long): Int

    suspend fun countLibraries(): Int

}