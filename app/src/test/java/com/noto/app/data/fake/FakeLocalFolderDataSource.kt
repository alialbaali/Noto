package com.noto.app.data.fake

import com.noto.app.domain.model.Folder
import com.noto.app.domain.source.LocalFolderDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeLocalFolderDataSource : LocalFolderDataSource {
    private val libraries = MutableStateFlow<MutableList<Folder>>(mutableListOf())

    override fun getFolders(): Flow<List<Folder>> = libraries

    override fun getFolderById(folderId: Long): Flow<Folder> = libraries
        .map { it.first { it.id == folderId } }

    override suspend fun createFolder(folder: Folder) {
        libraries.value.add(folder)
    }

    override suspend fun updateFolder(folder: Folder) {
        val libraryIndex = libraries.value.indexOfFirst { it.id == folder.id }
        libraries.value[libraryIndex] = folder
    }

    override suspend fun deleteFolder(folder: Folder) {
        libraries.value.remove(folder)
    }

    override suspend fun clearFolders() = libraries.value.clear()
}