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
    private val appWidgetId: Int,
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    val libraries = libraryRepository.getLibraries()
        .filterNotNull()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val isWidgetCreated = storage.get(appWidgetId.WidgetId)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isWidgetHeaderEnabled = storage.get(appWidgetId.WidgetHeader)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isEditWidgetButtonEnabled = storage.get(appWidgetId.EditWidgetButton)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isAppIconEnabled = storage.get(appWidgetId.AppIcon)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isNewLibraryButtonEnabled = storage.get(appWidgetId.NewLibraryButton)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val widgetRadius = storage.get(appWidgetId.WidgetRadius)
        .filterNotNull()
        .map { it.toInt() }
        .stateIn(viewModelScope, SharingStarted.Lazily, 16)

    val widgetLayout = storage.get(appWidgetId.WidgetLayout)
        .filterNotNull()
        .map { Layout.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Layout.Linear)

    val isNotesCountEnabled = storage.get(appWidgetId.NotesCount)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    fun setIsWidgetHeaderEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(appWidgetId.WidgetHeader, value.toString())
    }

    fun setIsEditWidgetButtonEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(appWidgetId.EditWidgetButton, value.toString())
    }

    fun setIsAppIconEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(appWidgetId.AppIcon, value.toString())
    }

    fun setIsNewLibraryButtonEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(appWidgetId.NewLibraryButton, value.toString())
    }

    fun setIsNotesCountEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(appWidgetId.NotesCount, value.toString())
    }

    fun setWidgetRadius(value: Int) = viewModelScope.launch {
        storage.put(appWidgetId.WidgetRadius, value.toString())
    }

    fun setWidgetLayout(value: Layout) = viewModelScope.launch {
        storage.put(appWidgetId.WidgetLayout, value.toString())
    }

    fun setIsWidgetCreated() = viewModelScope.launch {
        storage.put(appWidgetId.WidgetId, true.toString())
    }

    fun countNotes(libraryId: Long) = runBlocking {
        noteRepository.countNotesByLibraryId(libraryId)
    }
}