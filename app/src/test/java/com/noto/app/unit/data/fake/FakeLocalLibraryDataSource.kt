package com.noto.app.unit.data.fake

import com.noto.app.domain.model.Library
import com.noto.app.domain.source.LocalLibraryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalLibraryDataSource : LocalLibraryDataSource {

    private val libraries = mutableListOf<Library>()

    override fun getLibraries(): Flow<List<Library>> = flowOf(libraries)

    override fun getLibrary(libraryId: Long): Flow<Library> = flowOf(libraries.first { it.id == libraryId })

    override suspend fun createLibrary(library: Library) {
        libraries.add(library)
    }

    override suspend fun updateLibrary(library: Library) {
        val libraryIndex = libraries.indexOfFirst { it.id == library.id }
        libraries[libraryIndex] = library
    }

    override suspend fun deleteLibrary(library: Library) {
        libraries.remove(library)
    }
}