package com.noto.app.library

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.addCallback
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.LibraryFragmentBinding
import com.noto.app.domain.model.*
import com.noto.app.label.labelListItem
import com.noto.app.map
import com.noto.app.util.*
import jp.wasabeef.recyclerview.animators.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryFragment : Fragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<LibraryFragmentArgs>()

    private lateinit var epoxyController: EpoxyController

    private lateinit var itemTouchHelper: ItemTouchHelper

    private lateinit var layoutManager: StaggeredGridLayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LibraryFragmentBinding.inflate(inflater, container, false).withBinding {
            setupMixedTransitions()
            setupState()
            setupListeners()
        }

    @Suppress("UNCHECKED_CAST")
    private fun LibraryFragmentBinding.setupState() {
        val archiveMenuItem = bab.menu.findItem(R.id.archive)
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL).also(rv::setLayoutManager)
        bab.setRoundedCorners()

        viewModel.library
            .onEach { library ->
                setupLibrary(library)
                context?.let { context ->
                    val text = context.stringResource(R.string.archive, library.getTitle(context))
                    MenuItemCompat.setTooltipText(archiveMenuItem, text)
                    MenuItemCompat.setContentDescription(archiveMenuItem, text)
                }
            }
            .distinctUntilChangedBy { library -> library.layout }
            .onEach { library -> setupLayoutManager(library.layout) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.notes,
            viewModel.labels,
            viewModel.font,
            viewModel.library,
            viewModel.isSearchEnabled,
            viewModel.searchTerm,
        ) { array ->
            val notes = array[0] as UiState<List<NoteWithLabels>>
            val labels = array[1] as Map<Label, Boolean>
            val font = array[2] as Font
            val library = array[3] as Folder
            val isSearchEnabled = array[4] as Boolean
            val searchTerm = array[5] as String
            setupNotesAndLabels(
                notes.map { it.filterSelectedLabels(labels) },
                labels,
                font,
                library,
                isSearchEnabled,
                searchTerm,
            )
            setupItemTouchHelper(library.layout)
        }.launchIn(lifecycleScope)

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled ->
                if (isSearchEnabled)
                    rv.smoothScrollToPosition(0)
            }
            .launchIn(lifecycleScope)

        viewModel.isCollapseToolbar
            .onEach { isCollapseToolbar -> abl.setExpanded(!isCollapseToolbar && abl.isExpanded, false) }
            .launchIn(lifecycleScope)
    }

    private fun LibraryFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            val selectedLabelsIds = viewModel.labels.value
                .filter { it.value }
                .map { it.key.id }
                .toLongArray()
            navController?.navigateSafely(
                LibraryFragmentDirections.actionLibraryFragmentToNoteFragment(
                    args.libraryId,
                    labelsIds = selectedLabelsIds
                )
            )
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToMainFragment())
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
            navController?.navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToMainFragment())
        }
    }

    private fun LibraryFragmentBinding.setupLayoutManager(layout: Layout) {
        when (layout) {
            Layout.Linear -> layoutManager.spanCount = 1
            Layout.Grid -> layoutManager.spanCount = 2
        }
        rv.resetAdapter()
    }

    private fun LibraryFragmentBinding.setupNotesAndLabels(
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
                        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
                            viewModel.disableSearch()
                            if (isEnabled) isEnabled = false
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
                            navController?.navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToLabelDialogFragment(args.libraryId,
                                label.id))
                            true
                        }
                        onNewLabelClickListener { _ ->
                            navController?.navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToNewLabelDialogFragment(args.libraryId))
                        }
                    }

                    noteListSortingItem {
                        id(0)
                        sortingType(folder.sortingType)
                        sortingOrder(folder.sortingOrder)
                        notesCount(notes.size)
                        notoColor(folder.color)
                        onClickListener { _ ->
                            navController?.navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToNoteListSortingDialogFragment(args.libraryId))
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
                                                ?.navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToNoteFragment(entry.first.libraryId,
                                                    entry.first.id))
                                        }
                                        onLongClickListener { _ ->
                                            navController
                                                ?.navigateSafely(
                                                    LibraryFragmentDirections.actionLibraryFragmentToNoteDialogFragment(
                                                        entry.first.libraryId,
                                                        entry.first.id,
                                                        R.id.libraryFragment
                                                    )
                                                )
                                            true
                                        }
                                        onDragHandleTouchListener { view, event ->
                                            if (event.action == MotionEvent.ACTION_DOWN)
                                                rv.findContainingViewHolder(view)?.let { viewHolder ->
                                                    if (this@LibraryFragment::itemTouchHelper.isInitialized)
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

    private fun LibraryFragmentBinding.setupArchivedNotesMenuItem(): Boolean {
        navController?.navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToLibraryArchiveFragment(args.libraryId))
        return true
    }

    private fun LibraryFragmentBinding.setupSearchMenuItem(): Boolean {
        if (viewModel.isSearchEnabled.value)
            viewModel.disableSearch()
        else
            viewModel.enableSearch()
        return true
    }

    private fun LibraryFragmentBinding.setupMoreMenuItem(): Boolean {
        navController?.navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToLibraryDialogFragment(args.libraryId))
        return true
    }

    private fun LibraryFragmentBinding.setupLibrary(folder: Folder) {
        context?.let { context ->
            val backgroundColor = context.attributeColoResource(R.attr.notoBackgroundColor)
            val color = context.colorResource(folder.color.toResource())
            val colorStateList = color.toColorStateList()
            ctb.title = folder.getTitle(context)
            ctb.setCollapsedTitleTextColor(colorStateList)
            ctb.setExpandedTitleTextColor(colorStateList)
            fab.backgroundTintList = colorStateList
            bab.backgroundTint = colorStateList
            bab.menu.forEach { it.icon?.mutate()?.setTint(backgroundColor) }
            bab.navigationIcon?.mutate()?.setTint(backgroundColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                fab.outlineAmbientShadowColor = color
                fab.outlineSpotShadowColor = color
            }
        }
    }

    private fun LibraryFragmentBinding.setupItemTouchHelper(layout: Layout) {
        if (this@LibraryFragment::epoxyController.isInitialized) {
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
