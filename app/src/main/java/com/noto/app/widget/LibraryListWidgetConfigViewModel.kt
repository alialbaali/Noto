package com.noto.app.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Layout
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants.Widget.AppIcon
import com.noto.app.util.Constants.Widget.EditWidgetButton
import com.noto.app.util.Constants.Widget.NewLibraryButton
import com.noto.app.util.Constants.Widget.NotesCount
import com.noto.app.util.Constants.Widget.WidgetHeader
import com.noto.app.util.Constants.Widget.WidgetId
import com.noto.app.util.Constants.Widget.WidgetLayout
import com.noto.app.util.Constants.Widget.WidgetRadius
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class LibraryListWidgetConfigViewModel(
    private val widgetId: Int,
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    val libraries = libraryRepository.getLibraries()
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isWidgetCreated = storage.get(widgetId.WidgetId)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isWidgetHeaderEnabled = storage.get(widgetId.WidgetHeader)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isEditWidgetButtonEnabled = storage.get(widgetId.EditWidgetButton)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isAppIconEnabled = storage.get(widgetId.AppIcon)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isNewLibraryButtonEnabled = storage.get(widgetId.NewLibraryButton)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val widgetRadius = storage.get(widgetId.WidgetRadius)
        .filterNotNull()
        .map { it.toInt() }
        .stateIn(viewModelScope, SharingStarted.Lazily, 16)

    val widgetLayout = storage.get(widgetId.WidgetLayout)
        .filterNotNull()
        .map { Layout.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Layout.Linear)

    val isNotesCountEnabled = storage.get(widgetId.NotesCount)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun setIsWidgetHeaderEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(widgetId.WidgetHeader, value.toString())
    }

    fun setIsEditWidgetButtonEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(widgetId.EditWidgetButton, value.toString())
    }

    fun setIsAppIconEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(widgetId.AppIcon, value.toString())
    }

    fun setIsNewLibraryButtonEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(widgetId.NewLibraryButton, value.toString())
    }

    fun setIsNotesCountEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(widgetId.NotesCount, value.toString())
    }

    fun setWidgetRadius(value: Int) = viewModelScope.launch {
        storage.put(widgetId.WidgetRadius, value.toString())
    }

    fun setWidgetLayout(value: Layout) = viewModelScope.launch {
        storage.put(widgetId.WidgetLayout, value.toString())
    }

    fun setIsWidgetCreated() = viewModelScope.launch {
        storage.put(widgetId.WidgetId, true.toString())
    }

    fun countNotes(libraryId: Long) = runBlocking {
        noteRepository.countNotesByLibraryId(libraryId)
    }
}