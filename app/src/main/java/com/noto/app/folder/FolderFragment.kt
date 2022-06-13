package com.noto.app.folder

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    @Suppress("UNCHECKED_CAST")
    private fun FolderFragmentBinding.setupState() {
        val archiveMenuItem = bab.menu.findItem(R.id.archive)
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL).also(rv::setLayoutManager)

        combine(
            viewModel.folder,
            viewModel.notes,
            viewModel.labels
        ) { folder, notes, labels ->
            val notesCount = notes.getOrDefault(emptyList()).filterSelectedLabels(labels).count()
            setupFolder(folder, notesCount)
            context?.let { context ->
                val text = context.stringResource(R.string.archive, folder.getTitle(context))
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
            viewModel.isSearchEnabled,
            viewModel.searchTerm,
        ) { array ->
            val notes = array[0] as UiState<List<NoteWithLabels>>
            val labels = array[1] as Map<Label, Boolean>
            val font = array[2] as Font
            val folder = array[3] as Folder
            val isSearchEnabled = array[4] as Boolean
            val searchTerm = array[5] as String
            setupNotesAndLabels(
                notes.map { it.filterSelectedLabels(labels) },
                labels,
                font,
                folder,
                isSearchEnabled,
                searchTerm,
            )
            setupItemTouchHelper(folder.layout)
        }.launchIn(lifecycleScope)

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled ->
                if (isSearchEnabled)
                    rv.smoothScrollToPosition(0)
            }
            .launchIn(lifecycleScope)
    }

    private fun FolderFragmentBinding.setupListeners() {
        tb.setOnMenuItemClickListener {
            if (it.itemId == R.id.sorting) navController?.navigateSafely(FolderFragmentDirections.actionFolderFragmentToNoteListSortingDialogFragment(
                args.folderId))
            false
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
        isSearchEnabled: Boolean,
        searchTerm: String,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator(folder.color)
            is UiState.Success -> {
                val notes = state.value

                rv.withModels {
                    epoxyController = this

                    if (isSearchEnabled) {
                        searchItem {
                            id("search")
                            color(folder.color)
                            searchTerm(searchTerm)
                            callback { searchTerm -> viewModel.setSearchTerm(searchTerm) }
                            onBind { _, view, _ ->
                                if (view.binding.etSearch.text.toString().isBlank()) {
                                    view.binding.etSearch.requestFocus()
                                    activity?.showKeyboard(view.binding.etSearch)
                                }
                            }
                            onUnbind { _, view ->
                                activity?.hideKeyboard(view.binding.etSearch)
                            }
                        }
                        if (activity?.onBackPressedDispatcher?.hasEnabledCallbacks() == false) {
                            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
                                viewModel.disableSearch()
                                if (isEnabled) isEnabled = false
                            }
                        }
                    }

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
                            buildNotesModels(context, folder, notes) { notes ->
                                notes.forEach { entry ->
                                    noteItem {
                                        id(entry.first.id)
                                        note(entry.first)
                                        font(font)
                                        labels(entry.second)
                                        color(folder.color)
                                        previewSize(folder.notePreviewSize)
                                        isShowCreationDate(folder.isShowNoteCreationDate)
                                        searchTerm(if (isSearchEnabled) searchTerm.trim() else null)
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

    private fun FolderFragmentBinding.setupFolder(folder: Folder, notesCount: Int) {
        context?.let { context ->
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            tvFolderTitle.text = folder.getTitle(context)
            tvFolderTitle.setTextColor(colorStateList)
            tvFolderNotesCount.text = context.quantityStringResource(R.plurals.notes_count, notesCount, notesCount).lowercase()
            tvFolderNotesCount.typeface = context.tryLoadingFontResource(R.font.nunito_semibold_italic)
            fab.backgroundTintList = colorStateList
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                fab.outlineAmbientShadowColor = color
                fab.outlineSpotShadowColor = color
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
}
