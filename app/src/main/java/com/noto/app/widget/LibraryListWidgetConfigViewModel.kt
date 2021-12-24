package com.noto.app.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants.Widget.AppIcon
import com.noto.app.util.Constants.Widget.EditButton
import com.noto.app.util.Constants.Widget.Header
import com.noto.app.util.Constants.Widget.Id
import com.noto.app.util.Constants.Widget.NewItemButton
import com.noto.app.util.Constants.Widget.NotesCount
import com.noto.app.util.Constants.Widget.Radius
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibraryListWidgetConfigViewModel(
    private val appWidgetId: Int,
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    val libraries = libraryRepository.getLibraries()
        .combine(noteRepository.getLibrariesNotesCount()) { libraries, librariesNotesCount ->
            libraries.map { library ->
                val notesCount = librariesNotesCount.firstOrNull { it.libraryId == library.id }?.notesCount ?: 0
                library to notesCount
            }
        }
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isWidgetCreated = storage.get(appWidgetId.Id)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    private val mutableIsWidgetHeaderEnabled = MutableStateFlow(true)
    val isWidgetHeaderEnabled get() = mutableIsWidgetHeaderEnabled.asStateFlow()

    private val mutableIsEditWidgetButtonEnabled = MutableStateFlow(true)
    val isEditWidgetButtonEnabled get() = mutableIsEditWidgetButtonEnabled.asStateFlow()

    private val mutableIsAppIconEnabled = MutableStateFlow(true)
    val isAppIconEnabled get() = mutableIsAppIconEnabled.asStateFlow()

    private val mutableIsNewLibraryButtonEnabled = MutableStateFlow(true)
    val isNewLibraryButtonEnabled get() = mutableIsNewLibraryButtonEnabled.asStateFlow()

    private val mutableIsNotesCountEnabled = MutableStateFlow(true)
    val isNotesCountEnabled get() = mutableIsNotesCountEnabled.asStateFlow()

    private val mutableWidgetRadius = MutableStateFlow(16)
    val widgetRadius get() = mutableWidgetRadius.asStateFlow()

    init {
        storage.get(appWidgetId.Header)
            .filterNotNull()
            .map { it.toBoolean() }
            .onEach { mutableIsWidgetHeaderEnabled.value = it }
            .launchIn(viewModelScope)

        storage.get(appWidgetId.EditButton)
            .filterNotNull()
            .map { it.toBoolean() }
            .onEach { mutableIsEditWidgetButtonEnabled.value = it }
            .launchIn(viewModelScope)

        storage.get(appWidgetId.AppIcon)
            .filterNotNull()
            .map { it.toBoolean() }
            .onEach { mutableIsAppIconEnabled.value = it }
            .launchIn(viewModelScope)

        storage.get(appWidgetId.NewItemButton)
            .filterNotNull()
            .map { it.toBoolean() }
            .onEach { mutableIsNewLibraryButtonEnabled.value = it }
            .launchIn(viewModelScope)

        storage.get(appWidgetId.NotesCount)
            .filterNotNull()
            .map { it.toBoolean() }
            .onEach { mutableIsNotesCountEnabled.value = it }
            .launchIn(viewModelScope)

        storage.get(appWidgetId.Radius)
            .filterNotNull()
            .map { it.toInt() }
            .onEach { mutableWidgetRadius.value = it }
            .launchIn(viewModelScope)
    }


    fun setIsWidgetHeaderEnabled(value: Boolean) {
        mutableIsWidgetHeaderEnabled.value = value
    }

    fun setIsEditWidgetButtonEnabled(value: Boolean) {
        mutableIsEditWidgetButtonEnabled.value = value
    }

    fun setIsAppIconEnabled(value: Boolean) {
        mutableIsAppIconEnabled.value = value
    }

    fun setIsNewLibraryButtonEnabled(value: Boolean) {
        mutableIsNewLibraryButtonEnabled.value = value
    }

    fun setWidgetRadius(value: Int) {
        mutableWidgetRadius.value = value
    }

    fun setIsNotesCountEnabled(value: Boolean) {
        mutableIsNotesCountEnabled.value = value
    }

    fun createOrUpdateWidget() = viewModelScope.launch {
        storage.put(appWidgetId.Id, true.toString())
        storage.put(appWidgetId.Header, isWidgetHeaderEnabled.value.toString())
        storage.put(appWidgetId.EditButton, isEditWidgetButtonEnabled.value.toString())
        storage.put(appWidgetId.AppIcon, isAppIconEnabled.value.toString())
        storage.put(appWidgetId.NewItemButton, isNewLibraryButtonEnabled.value.toString())
        storage.put(appWidgetId.NotesCount, isNotesCountEnabled.value.toString())
        storage.put(appWidgetId.Radius, widgetRadius.value.toString())
    }
}