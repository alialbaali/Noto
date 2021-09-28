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
        val archiveMenuItem = bab.menu.findItem(R.id.archive)
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

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
            .onEach { library -> setupLayoutManager(library.layoutManager) }
            .launchIn(lifecycleScope)

        combine(
            viewModel.notes,
            viewModel.labels,
            viewModel.font,
            viewModel.library,
            etSearch.textAsFlow()
                .onStart { emit(etSearch.text) }
                .filterNotNull()
                .map { it.trim() },
        ) { notes, labels, font, library, searchTerm ->
            setupNotesAndLabels(
                notes.filter { it.first.title.contains(searchTerm, ignoreCase = true) || it.first.body.contains(searchTerm, ignoreCase = true) },
                labels,
                font,
                library
            )
            setupItemTouchHelper(library.layoutManager)
        }.launchIn(lifecycleScope)

        viewModel.isSearchEnabled
            .onEach { isSearchEnabled ->
                if (isSearchEnabled)
                    enableSearch()
                else
                    disableSearch()
            }
            .launchIn(lifecycleScope)
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
            disableSearch()
            findNavController().navigateUp()
        }

        bab.setNavigationOnClickListener {
            findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToLibraryDialogFragment(args.libraryId))
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.archive -> setupArchivedNotesMenuItem()
                R.id.search -> setupSearchMenuItem()
                else -> false
            }
        }
    }

    private fun LibraryFragmentBinding.enableSearch() {
        val rvAnimation = TranslateAnimation(0F, 0F, -50F, 0F).apply {
            duration = 250
        }
        tilSearch.isVisible = true
        rv.startAnimation(rvAnimation)
        etSearch.requestFocus()
        etSearch.showKeyboardUsingImm()

        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                disableSearch()
                if (isEnabled) {
                    isEnabled = false
                    requireActivity().onBackPressed()
                }
            }
    }

    private fun LibraryFragmentBinding.disableSearch() {
        if (tilSearch.isVisible) {
            val rvAnimation = TranslateAnimation(0F, 0F, 50F, 0F).apply {
                duration = 250
            }
            tilSearch.isVisible = false
            rv.startAnimation(rvAnimation)
        }
        etSearch.text = null
        requireActivity().hideKeyboard(root)
    }

    private fun LibraryFragmentBinding.setupLayoutManager(layoutManager: LayoutManager) {
        when (layoutManager) {
            LayoutManager.Linear -> rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            LayoutManager.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
    }

    private fun LibraryFragmentBinding.setupNotesAndLabels(
        notes: List<Pair<Note, List<Label>>>,
        labels: Map<Label, Boolean>,
        font: Font,
        library: Library,
    ) {
        if (notes.isEmpty()) {
            if (viewModel.isSearchEnabled.value)
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
                        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToNoteListSortingGroupingDialogFragment(args.libraryId))
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

                buildNotesModels(library, filteredNotes, resources, items)
            }
        }
    }

    private fun LibraryFragmentBinding.setupArchivedNotesMenuItem(): Boolean {
        findNavController().navigate(LibraryFragmentDirections.actionLibraryFragmentToLibraryArchiveFragment(args.libraryId))
        return true
    }

    private fun LibraryFragmentBinding.setupSearchMenuItem(): Boolean {
        viewModel.toggleIsSearchEnabled()
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
