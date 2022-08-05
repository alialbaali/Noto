package com.noto.app.folder

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.FolderFragmentBinding
import com.noto.app.domain.model.*
import com.noto.app.getOrDefault
import com.noto.app.label.labelListItem
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class FolderFragment : Fragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<FolderFragmentArgs>()

    private lateinit var epoxyController: EpoxyController

    private lateinit var itemTouchHelper: ItemTouchHelper

    private lateinit var layoutManager: StaggeredGridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FolderFragmentBinding.inflate(inflater, container, false).withBinding {
            setupMixedTransitions()
            setupState()
            setupListeners()
        }

    @OptIn(FlowPreview::class)
    @Suppress("UNCHECKED_CAST")
    private fun FolderFragmentBinding.setupState() {
        val archiveMenuItem = bab.menu.findItem(R.id.archive)
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL).also(rv::setLayoutManager)

        combine(
            viewModel.folder
                .map { it.filteringType }
                .distinctUntilChanged(),
            viewModel.notes,
            viewModel.labels,
            viewModel.searchTerm,
        ) { filteringType, notesState, labels, searchTerm ->
            val notes = notesState.getOrDefault(emptyList())
            setupNotesCount(notes, labels, filteringType, searchTerm)
        }.launchIn(lifecycleScope)

        combine(
            viewModel.folder,
            viewModel.isRememberScrollingPosition,
        ) { folder, isRememberScrollingPosition ->
            setupFolder(folder, isRememberScrollingPosition)
            context?.let { context ->
                val text = context.stringResource(R.string.folder_archive, folder.getTitle(context))
                MenuItemCompat.setTooltipText(archiveMenuItem, text)
                MenuItemCompat.setContentDescription(archiveMenuItem, text)
            }
            folder
        }.distinctUntilChangedBy { folder -> folder.layout }
            .onEach { folder -> setupLayoutManager(folder.layout) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.notes,
            viewModel.labels,
            viewModel.font,
            viewModel.folder,
            viewModel.searchTerm,
            viewModel.isSelection,
        ) { values ->
            val notes = values[0] as UiState<List<NoteItemModel>>
            val labels = values[1] as Map<Label, Boolean>
            val font = values[2] as Font
            val folder = values[3] as Folder
            val searchTerm = values[4] as String
            val isSelection = values[5] as Boolean
            val filteredNotes = notes.map {
                it.filterSelectedLabels(labels.filterSelected(), folder.filteringType).filterContent(searchTerm)
            }
            setupNotesAndLabels(
                filteredNotes,
                labels,
                font,
                folder,
                searchTerm,
                isSelection,
            )
            setupItemTouchHelper(folder.layout)
        }.launchIn(lifecycleScope)

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled -> if (isSearchEnabled) enableSearch() else disableSearch() }
            .launchIn(lifecycleScope)

        etSearch.textAsFlow()
            .asSearchFlow()
            .onEach { searchTerm -> viewModel.setSearchTerm(searchTerm) }
            .launchIn(lifecycleScope)

        rv.scrollPositionAsFlow()
            .debounce(DebounceTimeoutMillis)
            .onEach {
                val scrollingPosition = layoutManager.findFirstCompletelyVisibleItemPositions(null).firstOrNull() ?: -1
                if (scrollingPosition != -1) viewModel.updateFolderScrollingPosition(scrollingPosition)
            }
            .launchIn(lifecycleScope)

        root.keyboardVisibilityAsFlow()
            .onEach { isVisible ->
                fab.isVisible = !isVisible
                bab.isVisible = !isVisible
                tilSearch.updateLayoutParams<CoordinatorLayout.LayoutParams> {
                    anchorId = if (isVisible) View.NO_ID else fab.id
                    gravity = if (isVisible) Gravity.BOTTOM else Gravity.TOP
                }
            }
            .launchIn(lifecycleScope)

        if (isCurrentLocaleArabic()) {
            tvFolderNotesCount.isVisible = false
            tvFolderNotesCountRtl.isVisible = true
        } else {
            tvFolderNotesCount.isVisible = true
            tvFolderNotesCountRtl.isVisible = false
        }

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(Constants.IsSelection)
            ?.observe(viewLifecycleOwner) { noteId ->
                viewModel.enableSelection()
                viewModel.selectNote(noteId)
            }

        viewModel.isSelection
            .onEach { isSelection ->
                if (isSelection) {
                    fabOptions.isVisible = true
                    fab.isVisible = false
                    bab.isVisible = false
                } else {
                    fabOptions.isVisible = false
                    fab.isVisible = true
                    bab.isVisible = true
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun FolderFragmentBinding.setupListeners() {
        tb.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.notes_view -> navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToNoteListViewDialogFragment(args.folderId))
            }
            false
        }

        tb.setOnClickListener {
            navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToMainFragment())
        }

        fab.setOnClickListener {
            val selectedLabelsIds = viewModel.labels.value
                .filter { it.value }
                .map { it.key.id }
                .toLongArray()
            navController?.navigateSafely(
                FolderFragmentDirections.actionFolderFragmentToNoteFragment(
                    args.folderId,
                    labelsIds = selectedLabelsIds
                )
            )
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToMainFragment())
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.archive -> setupArchivedNotesMenuItem()
                R.id.search -> setupSearchMenuItem()
                R.id.more -> setupMoreMenuItem()
                else -> false
            }
        }

        bab.setOnSwipeGestureListener {
            navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToMainFragment())
        }

        activity?.onBackPressedDispatcher?.addCallback {
            if (viewModel.isSearchEnabled.value) {
                viewModel.disableSearch()
            } else if (viewModel.isSelection.value) {
                viewModel.disableSelection()
            } else {
                navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToMainFragment(exit = true))
            }
        }

        fabOptions.setOnClickListener {
            val selectedNoteIds = viewModel.notes.value.getOrDefault(emptyList())
                .filter { model -> model.isSelected }
                .map { model -> model.note.id }
                .toLongArray()
            if (selectedNoteIds.count() == 1) {
                navController
                    ?.navigateSafely(
                        FolderFragmentDirections.actionFolderFragmentToNoteDialogFragment(
                            args.folderId,
                            selectedNoteIds.first(),
                            R.id.folderFragment,
                        )
                    )
            } else {
                navController?.navigateSafely(
                    FolderFragmentDirections.actionFolderFragmentToNoteSelectionDialogFragment(
                        folderId = args.folderId,
                        selectedNoteIds = selectedNoteIds,
                    )
                )
            }
        }
    }

    private fun FolderFragmentBinding.setupLayoutManager(layout: Layout) {
        when (layout) {
            Layout.Linear -> layoutManager.spanCount = 1
            Layout.Grid -> layoutManager.spanCount = 2
        }
        rv.resetAdapter()
    }

    private fun FolderFragmentBinding.setupNotesAndLabels(
        state: UiState<List<NoteItemModel>>,
        labels: Map<Label, Boolean>,
        font: Font,
        folder: Folder,
        searchTerm: String,
        isSelection: Boolean,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator(folder.color)
            is UiState.Success -> {
                val notes = state.value

                rv.withModels {
                    epoxyController = this

                    labelListItem {
                        id("labels")
                        labels(labels)
                        color(folder.color)
                        onAllLabelClickListener { _ ->
                            viewModel.clearLabelSelection()
                        }
                        onLabelClickListener { label ->
                            if (labels.toList().first { it.first == label }.second)
                                viewModel.deselectLabel(label.id)
                            else
                                viewModel.selectLabel(label.id)
                        }
                        onLabelLongClickListener { label ->
                            navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToLabelDialogFragment(args.folderId,
                                label.id))
                            true
                        }
                        onNewLabelClickListener { _ ->
                            navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToNewLabelDialogFragment(args.folderId))
                        }
                    }

                    context?.let { context ->
                        if (notes.isEmpty())
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_notes_found))
                            }
                        else
                            buildNotesModels(
                                context,
                                folder,
                                notes,
                                onCreateClick = {
                                    navController?.navigateSafely(
                                        FolderFragmentDirections.actionFolderFragmentToNoteFragment(
                                            args.folderId,
                                            labelsIds = it.toLongArray()
                                        )
                                    )
                                }
                            ) { notes ->
                                notes.forEach { model ->
                                    noteItem {
                                        id(model.note.id)
                                        model(model)
                                        font(font)
                                        color(folder.color)
                                        previewSize(folder.notePreviewSize)
                                        isShowCreationDate(folder.isShowNoteCreationDate)
                                        searchTerm(searchTerm)
                                        isManualSorting(folder.sortingType == NoteListSortingType.Manual)
                                        isSelection(isSelection)
                                        onClickListener { _ ->
                                            navController
                                                ?.navigateSafely(
                                                    FolderFragmentDirections.actionFolderFragmentToNoteFragment(
                                                        model.note.folderId,
                                                        model.note.id
                                                    )
                                                )
                                        }
                                        onLongClickListener { _ ->
                                            navController
                                                ?.navigateSafely(
                                                    FolderFragmentDirections.actionFolderFragmentToNoteDialogFragment(
                                                        model.note.folderId,
                                                        model.note.id,
                                                        R.id.folderFragment,
                                                        isSelectionEnabled = true,
                                                    )
                                                )
                                            true
                                        }
                                        onDragHandleTouchListener { view, event ->
                                            if (event.action == MotionEvent.ACTION_DOWN)
                                                rv.findContainingViewHolder(view)?.let { viewHolder ->
                                                    if (this@FolderFragment::itemTouchHelper.isInitialized)
                                                        itemTouchHelper.startDrag(viewHolder)
                                                }
                                            view.performClick()
                                        }
                                        onSelectListener { _ -> viewModel.selectNote(model.note.id) }
                                        onDeselectListener { _ -> viewModel.deselectNote(model.note.id) }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    private fun FolderFragmentBinding.setupArchivedNotesMenuItem(): Boolean {
        navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToFolderArchiveFragment(args.folderId))
        return true
    }

    private fun FolderFragmentBinding.setupSearchMenuItem(): Boolean {
        if (viewModel.isSearchEnabled.value)
            viewModel.disableSearch()
        else
            viewModel.enableSearch()
        return true
    }

    private fun FolderFragmentBinding.setupMoreMenuItem(): Boolean {
        navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToFolderDialogFragment(args.folderId))
        return true
    }

    private fun FolderFragmentBinding.setupFolder(folder: Folder, isRememberScrollingPosition: Boolean) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            tvFolderTitle.text = folder.getTitle(context)
            tvFolderTitle.setTextColor(colorStateList)
            tvFolderNotesCount.typeface = context.tryLoadingFontResource(R.font.nunito_semibold)
            tvFolderNotesCount.animationInterpolator = DefaultInterpolator()
            fab.backgroundTintList = colorStateList
            fabOptions.backgroundTintList = colorStateList
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                fab.outlineAmbientShadowColor = color
                fab.outlineSpotShadowColor = color
                fabOptions.outlineAmbientShadowColor = color
                fabOptions.outlineSpotShadowColor = color
            }
            if (folder.isArchived || folder.isVaulted) {
                val drawableId = if (folder.isVaulted) R.drawable.ic_round_lock_24 else R.drawable.ic_round_archive_24
                val bitmapDrawable = context.drawableResource(drawableId)
                    ?.mutate()
                    ?.toBitmap(20.dp, 20.dp)
                    ?.toDrawable(resources)
                    ?.also { it.setTint(color) }
                tvFolderTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, bitmapDrawable, null)
                tvFolderTitle.compoundDrawablePadding = context.dimenResource(R.dimen.spacing_small).toInt()
            } else {
                tvFolderTitle.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
                tvFolderTitle.compoundDrawablePadding = 0
            }
        }
        rv.post {
            if (!viewModel.isSelection.value) {
                if (isRememberScrollingPosition) {
                    layoutManager.scrollToPosition(folder.scrollingPosition)
                }
            }
        }
    }

    private fun FolderFragmentBinding.setupNotesCount(
        notes: List<NoteItemModel>,
        labels: Map<Label, Boolean>,
        filteringType: FilteringType,
        searchTerm: String,
    ) {
        val selectedLabels = labels.filterSelected()
        val filteredNotes = notes.filterSelectedLabels(selectedLabels, filteringType).filterContent(searchTerm)
        val isFiltering = selectedLabels.isNotEmpty() || searchTerm.isNotBlank()
        val selectedNotes = notes.filter { it.isSelected }
        val notesCount = notes.count()
        val filteredNotesCount = filteredNotes.count()
        val selectedNotesCount = selectedNotes.count()
        when {
            // Filtering and selection.
            isFiltering && selectedNotes.isNotEmpty() -> {
                tvFolderNotesCount.text = context?.quantityStringResource(
                    R.plurals.notes_filtered_selected_count,
                    notesCount,
                    notesCount,
                    filteredNotesCount,
                    selectedNotesCount,
                )
                tvFolderNotesCountRtl.text = context?.quantityStringResource(
                    R.plurals.notes_filtered_selected_count,
                    notesCount,
                    notesCount,
                    filteredNotesCount,
                    selectedNotesCount,
                )
            }
            // Filtering only without selection.
            isFiltering && selectedNotes.isEmpty() -> {
                tvFolderNotesCount.text = context?.quantityStringResource(
                    R.plurals.notes_filtered_count,
                    notesCount,
                    notesCount,
                    filteredNotesCount,
                )
                tvFolderNotesCountRtl.text = context?.quantityStringResource(
                    R.plurals.notes_filtered_count,
                    notesCount,
                    notesCount,
                    filteredNotesCount,
                )
            }
            // Selection only without filtering.
            !isFiltering && selectedNotes.isNotEmpty() -> {
                tvFolderNotesCount.text = context?.quantityStringResource(
                    R.plurals.notes_selected_count,
                    notesCount,
                    notesCount,
                    selectedNotesCount,
                )
                tvFolderNotesCountRtl.text = context?.quantityStringResource(
                    R.plurals.notes_selected_count,
                    notesCount,
                    notesCount,
                    selectedNotesCount,
                )
            }
            // Without filtering or selection.
            else -> {
                tvFolderNotesCount.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
                tvFolderNotesCountRtl.text = context?.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
            }
        }
    }

    private fun FolderFragmentBinding.setupItemTouchHelper(layout: Layout) {
        if (this@FolderFragment::epoxyController.isInitialized) {
            val itemTouchHelperCallback = NoteItemTouchHelperCallback(epoxyController, layout) {
                rv.forEach { view ->
                    val viewHolder = rv.findContainingViewHolder(view) as EpoxyViewHolder
                    val item = viewHolder.model as? NoteItem
                    if (item != null) viewModel.updateNotePosition(item.model.note, viewHolder.bindingAdapterPosition)
                }
            }
            itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                .apply { attachToRecyclerView(rv) }
        }
    }

    private fun FolderFragmentBinding.enableSearch() {
        tilSearch.isVisible = true
        tilSearch.postDelayed({ etSearch.requestFocus() }, DefaultAnimationDuration)
        activity?.showKeyboard(etSearch)
    }

    private fun FolderFragmentBinding.disableSearch() {
        tilSearch.isVisible = false
        activity?.hideKeyboard(etSearch)
        etSearch.text = null
    }
}
