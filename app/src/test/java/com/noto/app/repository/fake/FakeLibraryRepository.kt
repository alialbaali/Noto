package com.noto.app.repository.fake

import com.noto.domain.model.Library
import com.noto.domain.replaceWith
import com.noto.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLibraryRepository : LibraryRepository {

    private val libraries = mutableListOf<Library>()

    override fun getLibraries(): Flow<List<Library>> = flowOf(libraries)

    override fun getLibraryById(libraryId: Long): Flow<Library> = flowOf(libraries.first { it.libraryId == libraryId })

    override suspend fun createLibrary(library: Library) {
        libraries.add(library)
    }

    override suspend fun updateLibrary(library: Library) = libraries.replaceWith(library) {
        it.libraryId == library.libraryId
    }

    override suspend fun deleteLibrary(library: Library) {
        libraries.remove(library)
    }

    override suspend fun countLibraryNotos(libraryId: Long): Int {
        TODO("Not yet implemented")
    }

    override suspend fun updateLibraries(libraries: List<Library>) {
        TODO("Not yet implemented")
    }

}