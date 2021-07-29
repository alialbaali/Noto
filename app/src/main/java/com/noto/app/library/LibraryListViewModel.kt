package com.noto.app.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Library
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.LayoutManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val LAYOUT_MANAGER_KEY = "Library_List_Layout_Manager"

class LibraryListViewModel(private val libraryRepository: LibraryRepository, private val storage: LocalStorage) : ViewModel() {

    private val mutableLibraries = MutableStateFlow<List<Library>>(emptyList())
    val libraries get() = mutableLibraries.asStateFlow()

    private val mutableLayoutManager = MutableStateFlow(LayoutManager.Linear)
    val layoutManager get() = mutableLayoutManager.asStateFlow()

    init {
        libraryRepository.getLibraries()
            .onEach { mutableLibraries.value = it }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            storage.get(LAYOUT_MANAGER_KEY)
                .map { it.map { LayoutManager.valueOf(it) } }
                .getOrElse { flowOf(LayoutManager.Linear) }
                .onEach { mutableLayoutManager.value = it }
                .launchIn(viewModelScope)
        }
    }

    fun countNotos(libraryId: Long): Int = runBlocking {
        libraryRepository.countLibraryNotes(libraryId)
    }

    fun setLayoutManager(value: LayoutManager) = viewModelScope.launch {
        storage.put(LAYOUT_MANAGER_KEY, value.toString())
    }

}