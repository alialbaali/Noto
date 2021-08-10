package com.noto.app.unit.data.fake

import com.noto.app.domain.model.Library
import com.noto.app.domain.source.LocalLibraryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeLocalLibraryDataSource : LocalLibraryDataSource {
    private val libraries = MutableStateFlow<MutableList<Library>>(mutableListOf())

    override fun getLibraries(): Flow<List<Library>> = libraries

    override fun getLibrary(libraryId: Long): Flow<Library> = libraries
        .map { it.first { it.id == libraryId } }

    override suspend fun createLibrary(library: Library) {
        libraries.value.add(library)
    }

    override suspend fun updateLibrary(library: Library) {
        val libraryIndex = libraries.value.indexOfFirst { it.id == library.id }
        libraries.value[libraryIndex] = library
    }

    override suspend fun deleteLibrary(library: Library) {
        libraries.value.remove(library)
    }
}