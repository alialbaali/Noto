package com.noto.app.library

import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import androidx.activity.addCallback
import androidx.core.graphics.ColorUtils
import androidx.core.view.MenuItemCompat
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
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
                context?.let { context ->
                    val text = context.stringResource(R.string.archive, library.title)
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
            etSearch.textAsFlow()
                .onStart { emit(etSearch.text) }
                .filterNotNull()
                .map { it.trim() },
        ) { notes, labels, font, library, searchTerm ->
            setupNotesAndLabels(
                notes
                    .filter { it.first.title.contains(searchTerm, ignoreCase = true) || it.first.body.contains(searchTerm, ignoreCase = true) }
                    .filterSelectedLabels(labels),
                labels,
                font,
                library
            )
            setupItemTouchHelper(library.layout)
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
            findNavController().navigateSafely(
                LibraryFragmentDirections.actionLibraryFragmentToNoteFragment(
                    args.libraryId,
                    labelsIds = selectedLabelsIds
                )
            )
        }

        tb.setNavigationOnClickListener {
            disableSearch()
            findNavController().navigateUp()
        }

        bab.setNavigationOnClickListener {
            findNavController().navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToLibraryDialogFragment(args.libraryId))
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

        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner) {
            disableSearch()
            if (isEnabled) {
                isEnabled = false
                activity?.onBackPressed()
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
        activity?.hideKeyboard(root)
    }

    private fun LibraryFragmentBinding.setupLayoutManager(layout: Layout) {
        when (layout) {
            Layout.Linear -> rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            Layout.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        rv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show))
    }

    private lateinit var tracker: SelectionTracker<Long>

    private fun LibraryFragmentBinding.setupNotesAndLabels(
        notes: List<Pair<Note, List<Label>>>,
        labels: Map<Label, Boolean>,
        font: Font,
        library: Library,
    ) {
        rv.withModels {
            epoxyController = this

            if (!this@LibraryFragment::tracker.isInitialized)
                tracker = SelectionTracker.Builder(
                    "MySelection",
                    rv,
                    StableIdKeyProvider(rv),
                    NoteItemDetailsLookup(rv),
                    StorageStrategy.createLongStorage(),
                ).withSelectionPredicate(SelectionPredicates.createSelectAnything())
                    .build()

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
                        .navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToLabelDialogFragment(args.libraryId, label.id))
                    true
                }
                onNewLabelClickListener { _ ->
                    findNavController().navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToNewLabelDialogFragment(args.libraryId))
                }
            }

            noteListSortingItem {
                id(0)
                sortingType(library.sortingType)
                sortingOrder(library.sortingOrder)
                notesCount(notes.size)
                notoColor(library.color)
                onClickListener { _ ->
                    findNavController().navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToNoteListSortingDialogFragment(args.libraryId))
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
                        isManualSorting(library.sortingType == NoteListSortingType.Manual)
//                        isSelected(tracker.isSelected(entry.first.id))
                        onClickListener { _ ->
                            findNavController()
                                .navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToNoteFragment(entry.first.libraryId, entry.first.id))
                        }
                        onLongClickListener { _ ->
                            findNavController()
                                .navigateSafely(
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

            context?.let { context ->
                if (notes.isEmpty())
                    placeholderItem {
                        id("placeholder")
                        placeholder(context.stringResource(R.string.no_notes_found))
                    }
                else
                    buildNotesModels(context, library, notes, items)
            }
        }
    }

    private fun LibraryFragmentBinding.setupArchivedNotesMenuItem(): Boolean {
        findNavController().navigateSafely(LibraryFragmentDirections.actionLibraryFragmentToLibraryArchiveFragment(args.libraryId))
        return true
    }

    private fun LibraryFragmentBinding.setupSearchMenuItem(): Boolean {
        viewModel.toggleIsSearchEnabled()
        return true
    }

    private fun LibraryFragmentBinding.setupLibrary(library: Library) {
        context?.let { context ->
            val color = context.colorResource(library.color.toResource())
            val colorStateList = context.colorStateResource(library.color.toResource())
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
