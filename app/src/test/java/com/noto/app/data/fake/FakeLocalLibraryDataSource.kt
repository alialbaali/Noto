package com.noto.app.data.fake

import com.noto.app.domain.model.Folder
import com.noto.app.domain.source.LocalLibraryDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeLocalLibraryDataSource : LocalLibraryDataSource {
    private val libraries = MutableStateFlow<MutableList<Folder>>(mutableListOf())

    override fun getLibraries(): Flow<List<Folder>> = libraries

    override fun getLibraryById(libraryId: Long): Flow<Folder> = libraries
        .map { it.first { it.id == libraryId } }

    override suspend fun createLibrary(folder: Folder) {
        libraries.value.add(folder)
    }

    override suspend fun updateLibrary(folder: Folder) {
        val libraryIndex = libraries.value.indexOfFirst { it.id == folder.id }
        libraries.value[libraryIndex] = folder
    }

    override suspend fun deleteLibrary(folder: Folder) {
        libraries.value.remove(folder)
    }

    override suspend fun clearLibraries() = libraries.value.clear()
}