package com.noto.app.data

import com.noto.app.domain.model.Library
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.source.LibraryLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LibraryRepositoryImpl(private val dataSource: LibraryLocalDataSource) : LibraryRepository {

    override fun getLibraries(): Flow<List<Library>> = dataSource.getLibraries()

    override fun getLibraryById(libraryId: Long): Flow<Library> = dataSource.getLibrary(libraryId)

    override suspend fun createLibrary(library: Library) = withContext(Dispatchers.IO) {
        dataSource.createLibrary(library.copy(title = library.title.trim()))
    }

    override suspend fun updateLibrary(library: Library) = withContext(Dispatchers.IO) {
        dataSource.updateLibrary(library.copy(title = library.title.trim()))
    }

    override suspend fun deleteLibrary(library: Library) = withContext(Dispatchers.IO) {
        dataSource.deleteLibrary(library)
    }

    override suspend fun countLibraryNotes(libraryId: Long): Int = withContext(Dispatchers.IO) {
        dataSource.countLibraryNotes(libraryId)
    }

    override suspend fun updateLibraries(libraries: List<Library>) = withContext(Dispatchers.IO) {
        dataSource.updateLibraries(libraries)
    }

}