package com.noto.app.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Label
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.Note
import com.noto.app.domain.repository.LabelRepository
import com.noto.app.domain.repository.LibraryRepository
import com.noto.app.domain.repository.NoteLabelRepository
import com.noto.app.domain.repository.NoteRepository
import com.noto.app.domain.source.LocalStorage
import com.noto.app.util.Constants.Widget.AppIcon
import com.noto.app.util.Constants.Widget.EditButton
import com.noto.app.util.Constants.Widget.Header
import com.noto.app.util.Constants.Widget.Id
import com.noto.app.util.Constants.Widget.LabelIds
import com.noto.app.util.Constants.Widget.Layout
import com.noto.app.util.Constants.Widget.NewItemButton
import com.noto.app.util.Constants.Widget.Radius
import com.noto.app.util.mapWithLabels
import com.noto.app.util.toLongList
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NoteListWidgetConfigViewModel(
    private val appWidgetId: Int,
    private val libraryRepository: LibraryRepository,
    private val noteRepository: NoteRepository,
    private val labelRepository: LabelRepository,
    private val noteLabelRepository: NoteLabelRepository,
    private val storage: LocalStorage,
) : ViewModel() {

    private val mutableLibrary = MutableStateFlow(Library(title = "Library", position = 0))
    val library get() = mutableLibrary.asStateFlow()

    private val mutableNotes = MutableStateFlow(emptyList<Pair<Note, List<Label>>>())
    val notes get() = mutableNotes.asStateFlow()

    private val mutableLabels = MutableStateFlow(emptyMap<Label, Boolean>())
    val labels get() = mutableLabels.asStateFlow()

    val isWidgetCreated = storage.get(appWidgetId.Id)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val isWidgetHeaderEnabled = storage.get(appWidgetId.Header)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isEditWidgetButtonEnabled = storage.get(appWidgetId.EditButton)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isAppIconEnabled = storage.get(appWidgetId.AppIcon)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val isNewLibraryButtonEnabled = storage.get(appWidgetId.NewItemButton)
        .filterNotNull()
        .map { it.toBoolean() }
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val widgetRadius = storage.get(appWidgetId.Radius)
        .filterNotNull()
        .map { it.toInt() }
        .stateIn(viewModelScope, SharingStarted.Lazily, 16)

    val widgetLayout = storage.get(appWidgetId.Layout)
        .filterNotNull()
        .map { Layout.valueOf(it) }
        .stateIn(viewModelScope, SharingStarted.Lazily, Layout.Linear)

    fun getData(libraryId: Long) {
        libraryRepository.getLibraryById(libraryId)
            .filterNotNull()
            .onEach { mutableLibrary.value = it }
            .launchIn(viewModelScope)

        combine(
            noteRepository.getNotesByLibraryId(libraryId)
                .filterNotNull(),
            labelRepository.getLabelsByLibraryId(libraryId)
                .filterNotNull(),
            noteLabelRepository.getNoteLabels()
                .filterNotNull()
        ) { notes, labels, noteLabels ->
            mutableNotes.value = notes.mapWithLabels(labels, noteLabels)
        }.launchIn(viewModelScope)

        combine(
            labelRepository.getLabelsByLibraryId(libraryId)
                .filterNotNull(),
            storage.get(appWidgetId.LabelIds(libraryId))
                .filterNotNull()
                .map { it.toLongList() }
                .onStart { emit(emptyList()) },
        ) { labels, labelIds ->
            // For some reason this is getting called on every action with storage
            mutableLabels.value = labels.sortedBy { it.position }.map { label ->
                val value = labelIds.any { it == label.id }
                label to value
            }.toMap()
        }.launchIn(viewModelScope)
    }

    fun selectLabel(id: Long) {
        mutableLabels.value = labels.value
            .map { it.key to if (it.value) true else (it.key.id == id) }
            .toMap()
    }

    fun deselectLabel(id: Long) {
        mutableLabels.value = labels.value
            .map { it.key to if (it.key.id == id) false else it.value }
            .toMap()
    }

    fun setIsWidgetHeaderEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(appWidgetId.Header, value.toString())
    }

    fun setIsEditWidgetButtonEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(appWidgetId.EditButton, value.toString())
    }

    fun setIsAppIconEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(appWidgetId.AppIcon, value.toString())
    }

    fun setIsNewLibraryButtonEnabled(value: Boolean) = viewModelScope.launch {
        storage.put(appWidgetId.NewItemButton, value.toString())
    }

    fun setWidgetRadius(value: Int) = viewModelScope.launch {
        storage.put(appWidgetId.Radius, value.toString())
    }

    fun setWidgetLayout(value: Layout) = viewModelScope.launch {
        storage.put(appWidgetId.Layout, value.toString())
    }

    fun setIsWidgetCreated() = viewModelScope.launch {
        storage.put(appWidgetId.Id, true.toString())
    }

    fun saveLabelIds() = viewModelScope.launch {
        val labelIds = labels.value.filterValues { it }.map { it.key.id }.joinToString()
        storage.put(appWidgetId.LabelIds(library.value.id), labelIds)
    }
}