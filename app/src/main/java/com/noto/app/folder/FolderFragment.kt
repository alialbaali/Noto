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
            viewModel.folder,
            viewModel.notes,
            viewModel.labels,
            viewModel.isRememberScrollingPosition,
        ) { folder, notes, labels, isRememberScrollingPosition ->
            val notesCount = notes.getOrDefault(emptyList()).filterSelectedLabels(labels.filterSelected(), folder.filteringType).count()
            setupFolder(folder, notesCount, isRememberScrollingPosition)
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
        ) { notes, labels, font, folder, searchTerm ->
            setupNotesAndLabels(
                notes.map { it.filterSelectedLabels(labels.filterSelected(), folder.filteringType) },
                labels,
                font,
                folder,
                searchTerm,
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

        viewModel.language
            .onEach { language ->
                when (language) {
                    Language.Arabic -> {
                        tvFolderNotesCount.isVisible = false
                        tvFolderNotesCountRtl.isVisible = true
                    }
                    else -> {
                        tvFolderNotesCount.isVisible = true
                        tvFolderNotesCountRtl.isVisible = false
                    }
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun FolderFragmentBinding.setupListeners() {
        tb.setOnMenuItemClickListener {
            if (it.itemId == R.id.notes_view) navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToNoteListViewDialogFragment(
                args.folderId))
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
            navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToMainFragment(exit = true))
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
        state: UiState<List<NoteWithLabels>>,
        labels: Map<Label, Boolean>,
        font: Font,
        folder: Folder,
        searchTerm: String,
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
                                notes.forEach { entry ->
                                    noteItem {
                                        id(entry.first.id)
                                        note(entry.first)
                                        font(font)
                                        labels(entry.second)
                                        color(folder.color)
                                        previewSize(folder.notePreviewSize)
                                        isShowCreationDate(folder.isShowNoteCreationDate)
                                        searchTerm(searchTerm)
                                        isManualSorting(folder.sortingType == NoteListSortingType.Manual)
                                        onClickListener { _ ->
                                            navController
                                                ?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToNoteFragment(entry.first.folderId,
                                                    entry.first.id))
                                        }
                                        onLongClickListener { _ ->
                                            navController
                                                ?.navigateSafely(
                                                    FolderFragmentDirections.actionFolderFragmentToNoteDialogFragment(
                                                        entry.first.folderId,
                                                        entry.first.id,
                                                        R.id.folderFragment
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

    private fun FolderFragmentBinding.setupFolder(folder: Folder, notesCount: Int, isRememberScrollingPosition: Boolean) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            tvFolderTitle.text = folder.getTitle(context)
            tvFolderTitle.setTextColor(colorStateList)
            tvFolderNotesCount.text = context.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
            tvFolderNotesCount.typeface = context.tryLoadingFontResource(R.font.nunito_semibold)
            tvFolderNotesCount.animationInterpolator = DefaultInterpolator()
            tvFolderNotesCountRtl.text = context.quantityStringResource(R.plurals.notes_count, notesCount, notesCount)
            fab.backgroundTintList = colorStateList
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                fab.outlineAmbientShadowColor = color
                fab.outlineSpotShadowColor = color
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
            if (isRememberScrollingPosition) {
                layoutManager.scrollToPosition(folder.scrollingPosition)
            }
        }
    }

    private fun FolderFragmentBinding.setupItemTouchHelper(layout: Layout) {
        if (this@FolderFragment::epoxyController.isInitialized) {
            val itemTouchHelperCallback = NoteItemTouchHelperCallback(epoxyController, layout) {
                rv.forEach { view ->
                    val viewHolder = rv.findContainingViewHolder(view) as EpoxyViewHolder
                    val model = viewHolder.model as? NoteItem
                    if (model != null) viewModel.updateNotePosition(model.note, viewHolder.bindingAdapterPosition)
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
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            viewModel.disableSearch()
            if (isEnabled) isEnabled = false
        }
    }

    private fun FolderFragmentBinding.disableSearch() {
        tilSearch.isVisible = false
        activity?.hideKeyboard(etSearch)
        etSearch.text = null
    }
}
