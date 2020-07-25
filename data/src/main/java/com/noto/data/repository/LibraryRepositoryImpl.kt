package com.noto.data.repository

import com.noto.data.source.local.LibraryLocalDataSource
import com.noto.domain.model.Library
import com.noto.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LibraryRepositoryImpl(private val libraryLocalDataSource: LibraryLocalDataSource) : LibraryRepository {

    override fun getLibraries(): Flow<List<Library>> = libraryLocalDataSource.getLibraries()

    override fun getLibrary(libraryId: Long): Flow<Library> = libraryLocalDataSource.getLibrary(libraryId)

    override suspend fun createLibrary(library: Library) = withContext(Dispatchers.IO) {
        libraryLocalDataSource.createLibrary(library.copy(libraryTitle = library.libraryTitle.trim()))
    }

    override suspend fun updateLibrary(library: Library) = withContext(Dispatchers.IO) {
        libraryLocalDataSource.updateLibrary(library.copy(libraryTitle = library.libraryTitle.trim()))
    }

    override suspend fun deleteLibrary(library: Library) = withContext(Dispatchers.IO) {
        libraryLocalDataSource.deleteLibrary(library)
    }

    override suspend fun countLibraryNotos(libraryId: Long): Int = withContext(Dispatchers.IO) {
        libraryLocalDataSource.countLibraryNotos(libraryId)
    }

}