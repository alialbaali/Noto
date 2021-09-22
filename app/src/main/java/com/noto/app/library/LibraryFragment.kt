package com.noto.app.library

import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.activity.addCallback
import androidx.core.graphics.ColorUtils
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.*
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.R
import com.noto.app.databinding.LibraryFragmentBinding
import com.noto.app.domain.model.*
import com.noto.app.label.labelListItem
import com.noto.app.util.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LibraryFragment : Fragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<LibraryFragmentArgs>()

    private lateinit var epoxyController: EpoxyController

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LibraryFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun LibraryFragmentBinding.setupState() {
        val layoutManagerMenuItem = bab.menu.findItem(R.id.layout_manager)
        val archiveMenuItem = bab.menu.findItem(R.id.archive)

        viewModel.library
            .onEach { library ->
                setupLibrary(library)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val text = library.getArchiveText(resources.stringResource(R.string.archive))
                    archiveMenuItem.contentDescription = text
                    archiveMenuItem.tooltipText = text
                }
            }
            .distinctUntilChangedBy { library -> library.layoutManager }
            .onEach { library -> setupLayoutManager(library.layoutManager, layoutManagerMenuItem) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.notes,
            viewModel.labels,
            viewModel.font,
            viewModel.library,
        ) { notes, labels, font, library ->
            setupNotesAndLabels(notes.sorted(library.sorting, library.sortingOrder), labels, font, library)
            setupItemTouchHelper(library.layoutManager)
        }.launchIn(lifecycleScope)
    }

    private fun LibraryFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            val selectedLabelsIds = viewModel.labels.value
                .filter { it.value }
                .map { it.key.id }
                .toLongArray()
            findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNoteFragment(args.libraryId, labelsIds = selectedLabelsIds))
        }
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        bab.setNavigationOnClickListener {
            findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToLibraryDialogFragment(args.libraryId))
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.archive -> setupArchivedNotesMenuItem()
                R.id.layout_manager -> setupLayoutManagerMenuItem()
                R.id.search -> setupSearchMenuItem()
                else -> false
            }
        }

        etSearch.doAfterTextChanged {
            viewModel.searchNotes(it.toString())
        }
    }

    private fun LibraryFragmentBinding.enableSearch() {
        val rvAnimation = TranslateAnimation(0F, 0F, -50F, 0F).apply {
            duration = 250
        }
        rv.startAnimation(rvAnimation)
        etSearch.requestFocus()
        requireActivity().showKeyboard(root)

        if (!requireActivity().onBackPressedDispatcher.hasEnabledCallbacks())
            requireActivity().onBackPressedDispatcher
                .addCallback(viewLifecycleOwner) {
                    disableSearch()
                    if (isEnabled) isEnabled = false
                }
    }

    private fun LibraryFragmentBinding.disableSearch() {
        val rvAnimation = TranslateAnimation(0F, 0F, 50F, 0F).apply {
            duration = 250
        }
        tilSearch.isVisible = false
        rv.startAnimation(rvAnimation)
        requireActivity().hideKeyboard(root)
    }

    private fun LibraryFragmentBinding.setupLayoutManager(layoutManager: LayoutManager, layoutManagerMenuItem: MenuItem) {
        val color = viewModel.library.value.color.toResource()
        val resource = resources.colorResource(color)
        when (layoutManager) {
            LayoutManager.Linear -> {
                layoutManagerMenuItem.icon = resources.drawableResource(R.drawable.ic_round_view_grid_24)
                rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            LayoutManager.Grid -> {
                layoutManagerMenuItem.icon = resources.drawableResource(R.drawable.ic_round_view_agenda_24)
                rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
        }
        layoutManagerMenuItem.icon?.mutate()?.setTint(resource)

        rv.visibility = View.INVISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
    }

    private fun LibraryFragmentBinding.setupNotesAndLabels(
        notes: List<Pair<Note, List<Label>>>,
        labels: Map<Label, Boolean>,
        font: Font,
        library: Library
    ) {
        if (notes.isEmpty()) {
            if (tilSearch.isVisible)
                tvPlaceHolder.text = resources.stringResource(R.string.no_note_matches_search_term)
            rv.visibility = View.GONE
            tvPlaceHolder.visibility = View.VISIBLE
        } else {
            val filteredNotes = notes.filterSelectedLabels(labels)
            rv.visibility = View.VISIBLE
            tvPlaceHolder.visibility = View.GONE
            rv.withModels {
                epoxyController = this

                labelListItem {
                    id("labels")
                    labels(labels)
                    color(library.color)
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
                        findNavController()
                            .navigate(LibraryFragmentDirections.actionLibraryFragmentToLabelDialogFragment(args.libraryId, label.id))
                        true
                    }
                    onNewLabelClickListener { _ ->
                        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNewLabelDialogFragment(args.libraryId))
                    }
                }

                noteListSortingItem {
                    id(0)
                    sorting(library.sorting)
                    sortingOrder(library.sortingOrder)
                    notesCount(filteredNotes.size)
                    notoColor(library.color)
                    onClickListener { _ ->
                        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNoteListSortingDialogFragment(args.libraryId))
                    }
                }

                val items = { items: List<Pair<Note, List<Label>>> ->
                    items.forEach { entry ->
                        noteItem {
                            id(entry.first.id)
                            note(entry.first)
                            font(font)
                            labels(entry.second)
                            color(library.color)
                            previewSize(library.notePreviewSize)
                            isShowCreationDate(library.isShowNoteCreationDate)
                            isManualSorting(library.sorting == NoteListSorting.Manual)
                            onClickListener { _ ->
                                findNavController()
                                    .navigate(LibraryFragmentDirections.actionLibraryFragmentToNoteFragment(entry.first.libraryId, entry.first.id))
                            }
                            onLongClickListener { _ ->
                                findNavController()
                                    .navigate(
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
                                        itemTouchHelper.startDrag(viewHolder)
                                    }
                                view.performClick()
                            }
                        }
                    }
                }

                val pinnedNotes = filteredNotes.filter { it.first.isPinned }
                val notPinnedNotes = filteredNotes.filterNot { it.first.isPinned }

                if (pinnedNotes.isNotEmpty()) {
                    headerItem {
                        id("pinned")
                        title(resources.stringResource(R.string.pinned))
                    }

                    items(pinnedNotes)

                    if (notPinnedNotes.isNotEmpty())
                        headerItem {
                            id("notes")
                            title(resources.stringResource(R.string.notes))
                        }
                }

                items(notPinnedNotes)
            }
        }
    }

    private fun LibraryFragmentBinding.setupArchivedNotesMenuItem(): Boolean {
        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToLibraryArchiveFragment(args.libraryId))
        return true
    }

    private fun LibraryFragmentBinding.setupLayoutManagerMenuItem(): Boolean {
        when (viewModel.library.value.layoutManager) {
            LayoutManager.Linear -> {
                viewModel.updateLayoutManager(LayoutManager.Grid)
                root.snackbar(getString(R.string.layout_is_grid_mode), anchorView = fab)
            }
            LayoutManager.Grid -> {
                viewModel.updateLayoutManager(LayoutManager.Linear)
                root.snackbar(getString(R.string.layout_is_list_mode), anchorView = fab)
            }
        }
        return true
    }

    private fun LibraryFragmentBinding.setupSearchMenuItem(): Boolean {
        val searchEt = tilSearch
        searchEt.isVisible = !searchEt.isVisible
        if (searchEt.isVisible)
            enableSearch()
        else
            disableSearch()
        return true
    }

    private fun LibraryFragmentBinding.setupLibrary(library: Library) {
        val color = resources.colorResource(library.color.toResource())
        val colorStateList = resources.colorStateResource(library.color.toResource())
        tb.title = library.title
        tb.setTitleTextColor(color)
        tb.navigationIcon?.mutate()?.setTint(color)
        bab.navigationIcon?.mutate()?.setTint(color)
        fab.backgroundTintList = colorStateList
        bab.menu.forEach { it.icon?.mutate()?.setTint(color) }
        tilSearch.boxBackgroundColor = ColorUtils.setAlphaComponent(color, 25)
        etSearch.setHintTextColor(colorStateList)
        etSearch.setTextColor(colorStateList)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            fab.outlineAmbientShadowColor = color
            fab.outlineSpotShadowColor = color
        }
    }

    private fun LibraryFragmentBinding.setupItemTouchHelper(layoutManager: LayoutManager) {
        if (this@LibraryFragment::epoxyController.isInitialized) {
            val itemTouchHelperCallback = NoteItemTouchHelperCallback(epoxyController, layoutManager) {
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
