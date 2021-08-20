package com.noto.app.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.LibraryListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.LayoutManager
import com.noto.app.util.sortByOrder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

private const val LayoutManagerKey = "Library_List_Layout_Manager"
private const val LibraryListSortingKey = "Library_List_Sorting"
private const val LibraryListSortingOrderKey = "Library_List_Sorting_Order"

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
            storage.getOrNull(LayoutManagerKey)
                .onEach { if (it == null) updateLayoutManager(LayoutManager.Grid) }
                .filterNotNull()
                .map { LayoutManager.valueOf(it) },
            storage.getOrNull(LibraryListSortingKey)
                .onEach { if (it == null) updateSorting(LibraryListSorting.CreationDate) }
                .filterNotNull()
                .map { LibraryListSorting.valueOf(it) },
            storage.getOrNull(LibraryListSortingOrderKey)
                .onEach { if (it == null) updateSortingOrder(SortingOrder.Descending) }
                .filterNotNull()
                .map { SortingOrder.valueOf(it) },
        ) { libraries, layoutManager, sorting, sortingOrder ->
            State(
                libraries.sortByOrder(sortingOrder) {
                    when (sorting) {
                        LibraryListSorting.Manually -> it.position
                        LibraryListSorting.CreationDate -> it.creationDate
                        LibraryListSorting.Alphabetically -> it.title
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
        storage.put(LayoutManagerKey, value.toString())
    }

    fun updateSortingOrder(value: SortingOrder) = viewModelScope.launch {
        storage.put(LibraryListSortingOrderKey, value.toString())
    }

    fun updateSorting(value: LibraryListSorting) = viewModelScope.launch {
        storage.put(LibraryListSortingKey, value.toString())
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