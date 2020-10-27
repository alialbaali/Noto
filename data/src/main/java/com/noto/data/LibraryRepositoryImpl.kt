package com.noto.data

import com.noto.domain.local.LibraryLocalDataSource
import com.noto.domain.model.Library
import com.noto.domain.repository.LibraryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber

class LibraryRepositoryImpl(private val dataSource: LibraryLocalDataSource) : LibraryRepository {

    override fun getLibraries(): Flow<List<Library>> = dataSource.getLibraries()

    override fun getLibraryById(libraryId: Long): Flow<Library> = dataSource.getLibrary(libraryId)

    override suspend fun createLibrary(library: Library) = withContext(Dispatchers.IO) {
        dataSource.createLibrary(library.copy(libraryTitle = library.libraryTitle.trim()))
    }

    override suspend fun updateLibrary(library: Library) = withContext(Dispatchers.IO) {
        dataSource.updateLibrary(library.copy(libraryTitle = library.libraryTitle.trim()))
    }

    override suspend fun deleteLibrary(library: Library) = withContext(Dispatchers.IO) {
        dataSource.deleteLibrary(library)
    }

    override suspend fun countLibraryNotos(libraryId: Long): Int = withContext(Dispatchers.IO) {
        dataSource.countLibraryNotos(libraryId)
    }

    override suspend fun updateLibraries(libraries: List<Library>) = withContext(Dispatchers.IO) {
        dataSource.updateLibraries(libraries)
    }

}