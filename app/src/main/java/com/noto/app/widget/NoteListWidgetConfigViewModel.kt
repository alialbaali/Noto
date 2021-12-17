package com.noto.app.widget

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noto.app.domain.model.Label
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
import com.noto.app.util.Constants.Widget.NewItemButton
import com.noto.app.util.Constants.Widget.Radius
import com.noto.app.util.NoteWithLabels
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

    private val mutableNotes = MutableStateFlow(emptyList<NoteWithLabels>())
    val notes get() = mutableNotes.asStateFlow()

    private val mutableLabels = MutableStateFlow(emptyMap<Label, Boolean>())
    val labels get() = mutableLabels.asStateFlow()

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

        storage.get(appWidgetId.Radius)
            .filterNotNull()
            .map { it.toInt() }
            .onEach { mutableWidgetRadius.value = it }
            .launchIn(viewModelScope)
    }

    fun getWidgetData(libraryId: Long) {
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

    fun createOrUpdateWidget() = viewModelScope.launch {
        val labelIds = labels.value.filterValues { it }.map { it.key.id }.joinToString()
        storage.put(appWidgetId.LabelIds(library.value.id), labelIds)
        storage.put(appWidgetId.Id, true.toString())
        storage.put(appWidgetId.Header, isWidgetHeaderEnabled.value.toString())
        storage.put(appWidgetId.EditButton, isEditWidgetButtonEnabled.value.toString())
        storage.put(appWidgetId.AppIcon, isAppIconEnabled.value.toString())
        storage.put(appWidgetId.NewItemButton, isNewLibraryButtonEnabled.value.toString())
        storage.put(appWidgetId.Radius, widgetRadius.value.toString())
    }
}
