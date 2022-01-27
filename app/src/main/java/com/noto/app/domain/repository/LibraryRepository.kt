package com.noto.app.domain.repository

import com.noto.app.domain.model.Folder
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {

    fun getAllLibraries(): Flow<List<Folder>>

    fun getLibraries(): Flow<List<Folder>>

    fun getArchivedLibraries(): Flow<List<Folder>>

    fun getVaultedLibraries(): Flow<List<Folder>>

    fun getLibraryById(libraryId: Long): Flow<Folder>

    suspend fun createLibrary(folder: Folder): Long

    suspend fun updateLibrary(folder: Folder)

    suspend fun deleteLibrary(folder: Folder)

    suspend fun clearLibraries()
}