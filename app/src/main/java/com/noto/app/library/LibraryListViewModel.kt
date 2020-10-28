package com.noto.app.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.noto.app.util.LayoutManager
import com.noto.domain.local.LocalStorage
import com.noto.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val LAYOUT_MANAGER_KEY = "Library_List_Layout_Manager"

class LibraryListViewModel(private val libraryRepository: LibraryRepository, private val storage: LocalStorage) : ViewModel() {

    val libraries = liveData {
        libraryRepository.getLibraries()
            .asLiveData()
            .let { emitSource(it) }
    }

    val layoutManager = liveData {
        storage.get(LAYOUT_MANAGER_KEY)
            .mapCatching { flow -> flow.map { LayoutManager.valueOf(it) } }
            .getOrDefault(flowOf(LayoutManager.Linear))
            .onStart { emit(LayoutManager.Linear) }
            .asLiveData()
            .let { emitSource(it) }
    }

    fun countNotos(libraryId: Long): Int = runBlocking {
        libraryRepository.countLibraryNotos(libraryId)
    }

    fun setLayoutManager(value: LayoutManager) = viewModelScope.launch {
        storage.put(LAYOUT_MANAGER_KEY, value.toString())
    }

}