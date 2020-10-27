package com.noto.app.library

import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.noto.domain.local.LocalStorage
import com.noto.domain.model.Library
import com.noto.domain.repository.LibraryRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking

private const val LAYOUT_MANAGER_KEY = "Library_List_Layout_Manager"

class LibraryListViewModel(private val libraryRepository: LibraryRepository, private val storage: LocalStorage) : ViewModel() {

    val libraries = liveData<List<Library>> {
        val source = libraryRepository.getLibraries()
            .asLiveData()
        emitSource(source)
    }

    @ExperimentalCoroutinesApi
    val layoutManager = liveData<Int> {
        emit(LINEAR_LAYOUT_MANAGER)
    }

    fun countNotos(libraryId: Long): Int = runBlocking {
        libraryRepository.countLibraryNotos(libraryId)
    }

    fun setLayoutManager(value: Int) = {  }

}