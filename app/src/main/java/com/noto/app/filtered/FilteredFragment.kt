package com.noto.app.filtered

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.components.*
import com.noto.app.databinding.FilteredFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.domain.model.Font
import com.noto.app.folder.NoteItemModel
import com.noto.app.folder.noteItem
import com.noto.app.getOrDefault
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FilteredFragment : Fragment() {

    private val viewModel by viewModel<FilteredViewModel> { parametersOf(args.model) }

    private val args by navArgs<FilteredFragmentArgs>()

    private val alarmManager by lazy { context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager? }

    private val anchorViewId by lazy { R.id.bab }

    private val modelColor by lazy { args.model.color }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FilteredFragmentBinding.inflate(inflater, container, false).withBinding {
        setupMixedTransitions()
        setupState()
        setupListeners()
    }

    private fun FilteredFragmentBinding.setupListeners() {
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle

        tb.setOnClickListener {
            rv.smoothScrollToPosition(0)
        }

        fab.setOnClickListener {
            context?.let { context ->
                navController?.navigateSafely(
                    FilteredFragmentDirections.actionFilteredFragmentToSelectFolderDialogFragment(
                        filteredFolderIds = longArrayOf(),
                        title = context.stringResource(R.string.select_folder)
                    )
                )
            }
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(FilteredFragmentDirections.actionFilteredFragmentToMainFragment())
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    if (viewModel.isSearchEnabled.value)
                        viewModel.disableSearch()
                    else
                        viewModel.enableSearch()
                    true
                }

                R.id.change_visibility -> {
                    if (viewModel.notesGroupedByFolderVisibility.value.any { it.value } || viewModel.notesGroupedByDateVisibility.value.any { it.value })
                        viewModel.collapseAll()
                    else
                        viewModel.expandAll()
                    true
                }

                R.id.unarchive -> {
                    val selectedNotes = viewModel.selectedNotesGroupedByFolder
                    context?.let { context ->
                        viewModel.unarchiveSelectedNotes()
                        val text = context.quantityStringResource(R.plurals.note_is_unarchived, selectedNotes.count(), selectedNotes.count())
                        val drawableId = R.drawable.ic_round_unarchive_24
                        root.snackbar(text, drawableId, anchorViewId, modelColor)
                    }
                    true
                }

                R.id.delete -> {
                    val selectedNotes = viewModel.selectedNotesGroupedByFolder
                    context?.let { context ->
                        val confirmationText = context.quantityStringResource(R.plurals.delete_note_confirmation, selectedNotes.count())
                        val descriptionText = context.quantityStringResource(R.plurals.delete_note_description, selectedNotes.count())
                        val btnText = context.quantityStringResource(R.plurals.delete_note, selectedNotes.count())
                        savedStateHandle?.getLiveData<Int>(Constants.ClickListener)
                            ?.observe(viewLifecycleOwner) {
                                viewModel.deleteSelectedNotes().invokeOnCompletion {
                                    val text = context.quantityStringResource(R.plurals.note_is_deleted, selectedNotes.count(), selectedNotes.count())
                                    val drawableId = R.drawable.ic_round_delete_24
                                    root.snackbar(text, drawableId, anchorViewId, modelColor)
                                    selectedNotes.filter { model -> model.note.reminderDate != null }
                                        .forEach { model -> alarmManager?.cancelAlarm(context, model.note.id) }
                                    context.updateAllWidgetsData()
                                    context.updateNoteListWidgets()
                                }
                            }

                        navController?.navigateSafely(
                            FilteredFragmentDirections.actionFilteredFragmentToConfirmationDialogFragment(
                                confirmationText,
                                descriptionText,
                                btnText,
                            )
                        )
                    }
                    true
                }

                else -> false
            }
        }

        bab.setOnSwipeGestureListener {
            navController?.navigateSafely(FilteredFragmentDirections.actionFilteredFragmentToMainFragment())
        }

        activity?.onBackPressedDispatcher?.addCallback {
            when {
                viewModel.isSelection.value -> if (viewModel.isSearchEnabled.value) {
                    viewModel.disableSearch()
                } else {
                    viewModel.disableSelection()
                    viewModel.deselectAllNotes()
                }

                viewModel.isSearchEnabled.value -> viewModel.disableSearch()
                viewModel.quickExit.value -> activity?.finish()
                else -> navController?.navigateSafely(FilteredFragmentDirections.actionFilteredFragmentToMainFragment(exit = true))
            }
        }
    }


    @OptIn(FlowPreview::class)
    private fun FilteredFragmentBinding.setupState() {
        if (args.model == FilteredItemModel.Archived) {
            bab.menu?.findItem(R.id.unarchive)?.isVisible = true
            bab.menu?.findItem(R.id.delete)?.isVisible = true
            fab.hide()
        } else {
            bab.menu?.findItem(R.id.unarchive)?.isVisible = false
            bab.menu?.findItem(R.id.delete)?.isVisible = false
            fab.show()
        }

        context?.let { context ->
            val colorResource = context.colorResource(args.model.color.toResource())
            tvTitle.setTextColor(colorResource)
            fab.backgroundTintList = colorResource.toColorStateList()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                etSearch.textCursorDrawable?.mutate()?.setTint(colorResource)
            }
            tvTitle.text = when (args.model) {
                FilteredItemModel.All -> R.string.all
                FilteredItemModel.Recent -> R.string.recent
                FilteredItemModel.Scheduled -> R.string.scheduled
                FilteredItemModel.Archived -> R.string.archived
            }.let(context::stringResource)
        }
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        tvNotesCount.animationInterpolator = DefaultInterpolator()
        tvNotesCount.typeface = context?.tryLoadingFontResource(R.font.nunito_semibold)
        val layoutManager = rv.layoutManager as LinearLayoutManager
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle

        when (args.model) {
            FilteredItemModel.All, FilteredItemModel.Archived -> {
                combine(
                    viewModel.notesGroupedByFolder,
                    viewModel.notesGroupedByFolderVisibility,
                    viewModel.font,
                    viewModel.searchTerm,
                    viewModel.isSelection,
                ) { notes, notesVisibility, font, searchTerm, isSelection ->
                    val isEmpty = notes.getOrDefault(emptyMap()).isEmpty()
                    val isEmptySelection = notes.getOrDefault(emptyMap()).flatMap { it.value }.none { it.isSelected }
                    bab.menu?.forEach { menuItem ->
                        menuItem.isEnabled = !isEmpty
                        menuItem.icon?.alpha = if (isEmpty) DisabledAlpha else EnabledAlpha
                        if (menuItem.itemId == R.id.unarchive || menuItem.itemId == R.id.delete) {
                            menuItem.isEnabled = !isEmptySelection
                            menuItem.icon?.alpha = if (isEmptySelection) DisabledAlpha else EnabledAlpha
                        }
                    }
                    setupNotesGroupedByFolder(notes, notesVisibility, font, searchTerm, isSelection)
                }.launchIn(lifecycleScope)
            }

            FilteredItemModel.Recent, FilteredItemModel.Scheduled -> {
                combine(
                    viewModel.notesGroupedByDate,
                    viewModel.notesGroupedByDateVisibility,
                    viewModel.font,
                    viewModel.searchTerm,
                ) { notes, notesVisibility, font, searchTerm ->
                    val isEmpty = notes.getOrDefault(emptyMap()).isEmpty()
                    val isEmptySelection = notes.getOrDefault(emptyMap()).flatMap { it.value }.none { it.second.isSelected }
                    bab.menu?.forEach { menuItem ->
                        menuItem.isEnabled = !isEmpty
                        menuItem.icon?.alpha = if (isEmpty) DisabledAlpha else EnabledAlpha
                        if (menuItem.itemId == R.id.unarchive || menuItem.itemId == R.id.delete) {
                            menuItem.isEnabled = !isEmptySelection
                            menuItem.icon?.alpha = if (isEmptySelection) DisabledAlpha else EnabledAlpha
                        }
                    }
                    setupNotesGroupedByDate(notes, notesVisibility, font, searchTerm)
                }.launchIn(lifecycleScope)
            }
        }

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled -> if (isSearchEnabled) enableSearch() else disableSearch() }
            .launchIn(lifecycleScope)

        etSearch.textAsFlow()
            .asSearchFlow()
            .onEach { searchTerm -> viewModel.setSearchTerm(searchTerm) }
            .launchIn(lifecycleScope)

        val menuItem = bab.menu.findItem(R.id.change_visibility)
        val expandText = context?.stringResource(R.string.expand)
        val collapseText = context?.stringResource(R.string.collapse)

        viewModel.notesGroupedByFolderVisibility
            .onEach { visibility ->
                if (visibility.any { it.value }) {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_collapse_24)
                    menuItem.title = collapseText
                    MenuItemCompat.setContentDescription(menuItem, collapseText)
                } else {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_expand_24)
                    menuItem.title = expandText
                    MenuItemCompat.setContentDescription(menuItem, expandText)
                }
            }
            .launchIn(lifecycleScope)

        viewModel.notesGroupedByDateVisibility
            .onEach { visibility ->
                if (visibility.any { it.value }) {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_collapse_24)
                    menuItem.title = collapseText
                    MenuItemCompat.setContentDescription(menuItem, collapseText)
                } else {
                    menuItem.icon = context?.drawableResource(R.drawable.ic_round_expand_24)
                    menuItem.title = expandText
                    MenuItemCompat.setContentDescription(menuItem, expandText)
                }
            }
            .launchIn(lifecycleScope)

        combine(
            viewModel.isRememberScrollingPosition,
            viewModel.scrollingPosition
        ) { isRememberScrollingPosition, scrollingPosition ->
            layoutManager.postOnAnimation {
                rv.post {
                    if (isRememberScrollingPosition) {
                        layoutManager.scrollToPosition(scrollingPosition)
                    }
                }
            }
        }.launchIn(lifecycleScope)

        rv.scrollPositionAsFlow()
            .debounce(DebounceTimeoutMillis)
            .onEach {
                val scrollingPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                if (scrollingPosition != -1) viewModel.updateScrollingPosition(scrollingPosition)
            }
            .launchIn(lifecycleScope)

        combine(
            root.keyboardVisibilityAsFlow(),
            bab.isHiddenAsFlow()
                .onStart { emit(false) },
        ) { isKeyboardVisible, isBabHidden ->
            if (args.model != FilteredItemModel.Archived) fab.isVisible = !isKeyboardVisible
            bab.isVisible = !isKeyboardVisible
        }.launchIn(lifecycleScope)

        savedStateHandle?.getLiveData<Long>(Constants.FolderId)
            ?.observe(viewLifecycleOwner) { folderId ->
                if (folderId != null) {
                    lifecycleScope.launch {
                        delay(150) // Wait for the fragment to be destroyed
                        savedStateHandle.remove<Long>(Constants.FolderId)
                        navController?.navigateSafely(
                            FilteredFragmentDirections.actionFilteredFragmentToNoteFragment(
                                folderId,
                                selectedNoteIds = longArrayOf()
                            )
                        )
                    }
                }
            }

        if (isCurrentLocaleArabic()) {
            tvNotesCount.isVisible = false
            tvNotesCountRtl.isVisible = true
        } else {
            tvNotesCount.isVisible = true
            tvNotesCountRtl.isVisible = false
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun FilteredFragmentBinding.setupNotesGroupedByDate(
        state: UiState<NotesGroupedByDate>,
        notesVisibility: Map<LocalDate, Boolean>,
        font: Font,
        searchTerm: String,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val notes = state.value
                val notesCount = notes.map { it.value.count() }.sum()
                tvNotesCount.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
                tvNotesCountRtl.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)

                rv.withModels {

                    context?.let { context ->
                        if (notes.values.all { it.isEmpty() }) {
                            val placeholderId = when {
                                searchTerm.isNotBlank() -> R.string.no_notes_found_search
                                args.model == FilteredItemModel.Recent -> R.string.no_recent_notes_found
                                args.model == FilteredItemModel.Scheduled -> R.string.no_scheduled_notes_found
                                else -> R.string.no_notes_found
                            }

                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(placeholderId))
                            }
                        } else {
                            notes.forEach { (date, notes) ->
                                val isVisible = notesVisibility[date] ?: true
                                val noteIds = notes.map { it.second.note.id }.toLongArray()

                                headerItem {
                                    id(date.dayOfYear)
                                    title(date.format())
                                    isVisible(isVisible)
                                    onClickListener { _ -> viewModel.toggleVisibilityForDate(date) }
                                }

                                if (isVisible)
                                    notes.forEach { pair ->
                                        noteItem {
                                            id(pair.second.note.id)
                                            model(pair.second)
                                            font(font)
                                            color(pair.first.color)
                                            searchTerm(searchTerm)
                                            previewSize(pair.first.notePreviewSize)
                                            isShowCreationDate(pair.first.isShowNoteCreationDate)
                                            isShowAccessDate(true)
                                            isManualSorting(false)
                                            onClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        FilteredFragmentDirections.actionFilteredFragmentToNoteFragment(
                                                            pair.second.note.folderId,
                                                            noteId = pair.second.note.id,
                                                            selectedNoteIds = noteIds,
                                                            searchTerm = searchTerm.ifBlank { null },
                                                        )
                                                    )
                                            }
                                            onLongClickListener { _ ->
                                                navController
                                                    ?.navigateSafely(
                                                        FilteredFragmentDirections.actionFilteredFragmentToNoteDialogFragment(
                                                            pair.second.note.folderId,
                                                            pair.second.note.id,
                                                            R.id.folderFragment,
                                                            selectedNoteIds = noteIds,
                                                        )
                                                    )
                                                true
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun FilteredFragmentBinding.setupNotesGroupedByFolder(
        state: UiState<Map<Folder, List<NoteItemModel>>>,
        notesVisibility: Map<Folder, Boolean>,
        font: Font,
        searchTerm: String,
        isSelection: Boolean,
    ) {
        val isArchivedModel = args.model == FilteredItemModel.Archived

        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val notes = state.value
                val notesCount = notes.map { it.value.count() }.sum()
                tvNotesCount.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
                tvNotesCountRtl.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)

                rv.withModels {

                    context?.let { context ->
                        if (notes.values.all { it.isEmpty() }) {
                            val placeholderId = when {
                                searchTerm.isNotBlank() -> R.string.no_notes_found_search
                                args.model == FilteredItemModel.Archived -> R.string.no_archived_notes_found
                                args.model == FilteredItemModel.All -> R.string.no_relevant_notes_found
                                else -> R.string.no_notes_found
                            }

                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(placeholderId))
                            }
                        } else {
                            notes.forEach { (folder, notes) ->
                                val isVisible = notesVisibility[folder] ?: true
                                val noteIds = notes.map { it.note.id }.toLongArray()

                                headerItem {
                                    id("folder ${folder.id}")
                                    title(folder.getTitle(context))
                                    color(folder.color)
                                    isVisible(isVisible)
                                    onClickListener { _ -> viewModel.toggleVisibilityForFolder(folder.id) }
                                    onCreateClickListener { _ ->
                                        navController?.navigateSafely(
                                            FilteredFragmentDirections.actionFilteredFragmentToNoteFragment(
                                                folder.id,
                                                selectedNoteIds = longArrayOf()
                                            )
                                        )
                                    }
                                    onLongClickListener { _ ->
                                        navController?.navigateSafely(FilteredFragmentDirections.actionFilteredFragmentToFolderFragment(folder.id))
                                        true
                                    }
                                }

                                if (isVisible)
                                    notes.forEach { model ->
                                        noteItem {
                                            id(model.note.id)
                                            model(model)
                                            font(font)
                                            color(folder.color)
                                            searchTerm(searchTerm)
                                            previewSize(folder.notePreviewSize)
                                            isShowCreationDate(folder.isShowNoteCreationDate)
                                            isManualSorting(false)
                                            isSelection(isArchivedModel && isSelection)
                                            onClickListener { _ ->
                                                if (isArchivedModel) {
                                                    if (isSelection) {
                                                        if (model.isSelected) viewModel.deselectNote(model.note.id) else viewModel.selectNote(model.note.id)
                                                    } else {
                                                        navController?.navigateSafely(
                                                            FilteredFragmentDirections.actionFilteredFragmentToNotePagerFragment(
                                                                model.note.folderId,
                                                                model.note.id,
                                                                notes.map { it.note.id }.toLongArray(),
                                                                isArchive = true,
                                                            )
                                                        )
                                                    }
                                                } else {
                                                    navController
                                                        ?.navigateSafely(
                                                            FilteredFragmentDirections.actionFilteredFragmentToNoteFragment(
                                                                model.note.folderId,
                                                                noteId = model.note.id,
                                                                selectedNoteIds = noteIds,
                                                                searchTerm = searchTerm.ifBlank { null },
                                                            )
                                                        )
                                                }
                                            }
                                            onLongClickListener { _ ->
                                                if (isArchivedModel) {
                                                    viewModel.enableSelection()
                                                    if (model.isSelected) viewModel.deselectNote(model.note.id) else viewModel.selectNote(model.note.id)
                                                } else {
                                                    navController
                                                        ?.navigateSafely(
                                                            FilteredFragmentDirections.actionFilteredFragmentToNoteDialogFragment(
                                                                model.note.folderId,
                                                                model.note.id,
                                                                R.id.folderFragment,
                                                                selectedNoteIds = noteIds,
                                                            )
                                                        )
                                                }
                                                true
                                            }
                                        }
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun FilteredFragmentBinding.enableSearch() {
        tilSearch.isVisible = true
        tilSearch.postDelayed({ etSearch.requestFocus() }, DefaultAnimationDuration)
        activity?.showKeyboard(etSearch)
    }

    private fun FilteredFragmentBinding.disableSearch() {
        tilSearch.isVisible = false
        activity?.hideKeyboard(etSearch)
        etSearch.text = null
    }

}