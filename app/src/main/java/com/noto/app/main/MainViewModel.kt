package com.noto.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.LayoutManager
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.LibraryListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants
import com.noto.app.util.sortByOrder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    private val mutableState = MutableStateFlow(State())
    val state get() = mutableState.asStateFlow()

    init {
        combine(
            libraryRepository.getLibraries()
                .filterNotNull(),
            storage.get(Constants.LibraryListLayoutManagerKey)
                .filterNotNull()
                .map { LayoutManager.valueOf(it) },
            storage.get(Constants.LibraryListSortingKey)
                .filterNotNull()
                .map { LibraryListSorting.valueOf(it) },
            storage.get(Constants.LibraryListSortingOrderKey)
                .filterNotNull()
                .map { SortingOrder.valueOf(it) },
        ) { libraries, layoutManager, sorting, sortingOrder ->
            State(
                libraries.sortByOrder(sortingOrder) {
                    when (sorting) {
                        LibraryListSorting.Manual -> it.position
                        LibraryListSorting.CreationDate -> it.creationDate
                        LibraryListSorting.Alphabetical -> it.title
                    }
                },
                layoutManager,
                sorting,
                sortingOrder,
            )
        }.onEach {
            mutableState.value = it
        }.launchIn(viewModelScope)
    }

    fun countNotes(libraryId: Long): Int = runBlocking {
        noteRepository.countNotesByLibraryId(libraryId)
    }

    fun updateLayoutManager(value: LayoutManager) = viewModelScope.launch {
        storage.put(Constants.LibraryListLayoutManagerKey, value.toString())
    }

    fun updateSorting(value: LibraryListSorting) = viewModelScope.launch {
        storage.put(Constants.LibraryListSortingKey, value.toString())
    }

    fun updateSortingOrder(value: SortingOrder) = viewModelScope.launch {
        storage.put(Constants.LibraryListSortingOrderKey, value.toString())
    }

    fun updateLibraryPosition(library: Library, position: Int) = viewModelScope.launch {
        libraryRepository.updateLibrary(library.copy(position = position))
    }

    data class State(
        val libraries: List<Library> = emptyList(),
        val layoutManager: LayoutManager = LayoutManager.Grid,
        val sorting: LibraryListSorting = LibraryListSorting.CreationDate,
        val sortingOrder: SortingOrder = SortingOrder.Descending,
    )
}