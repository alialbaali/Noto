package com.noto.domain.repository

import com.noto.domain.model.Library

interface LibraryRepository {

    suspend fun createLibrary(library: Library)

    suspend fun deleteLibrary(library: Library)

    suspend fun updateLibrary(library: Library)

    suspend fun getLibraries(): Result<List<Library>>

    suspend fun getLibraryById(libraryId: Long): Result<Library>

}