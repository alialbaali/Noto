package com.noto.app.main

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.R
import com.noto.app.databinding.MainFragmentBinding
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.LibraryListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.*
import kotlinx.coroutines.flow.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private val viewModel by viewModel<MainViewModel>()

    private lateinit var epoxyController: EpoxyController

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        MainFragmentBinding.inflate(inflater, container, false).withBinding {
            setupListeners()
            setupState()
        }

    private fun MainFragmentBinding.setupListeners() {
        fab.setOnClickListener {
            findNavController().navigateSafely(MainFragmentDirections.actionMainFragmentToNewLibraryDialogFragment())
        }

        bab.setNavigationOnClickListener {
            findNavController().navigateSafely(MainFragmentDirections.actionMainFragmentToMainDialogFragment())
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.layout -> setupLayoutMenuItem()
                R.id.libraries_archive -> setupLibrariesArchiveMenuItem()
                else -> false
            }
        }
    }

    private fun MainFragmentBinding.setupState() {
        val layoutManagerMenuItem = bab.menu.findItem(R.id.layout)
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

        combine(
            viewModel.libraries,
            viewModel.sorting,
            viewModel.sortingOrder,
            viewModel.isShowNotesCount,
        ) { libraries, sorting, sortingOrder, isShowNotesCount ->
            setupLibraries(libraries.sorted(sorting, sortingOrder), sorting, sortingOrder, isShowNotesCount)
        }.launchIn(lifecycleScope)

        viewModel.layout
            .onEach { layout -> setupLayoutManager(layout, layoutManagerMenuItem) }
            .combine(viewModel.libraries) { layout, _ -> layout }
            .onEach { layout -> setupItemTouchHelper(layout) }
            .launchIn(lifecycleScope)
    }

    private fun MainFragmentBinding.setupLayoutManager(layout: Layout, layoutManagerMenuItem: MenuItem) {
        when (layout) {
            Layout.Linear -> {
                layoutManagerMenuItem.icon = resources.drawableResource(R.drawable.ic_round_view_grid_24)
                rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            }
            Layout.Grid -> {
                layoutManagerMenuItem.icon = resources.drawableResource(R.drawable.ic_round_view_agenda_24)
                rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            }
        }
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
    }

    private fun MainFragmentBinding.setupLibraries(
        libraries: List<Library>,
        sorting: LibraryListSorting,
        sortingOrder: SortingOrder,
        isShowNotesCount: Boolean,
    ) {
        rv.withModels {
            epoxyController = this

            libraryListSortingItem {
                id(0)
                sorting(sorting)
                sortingOrder(sortingOrder)
                librariesCount(libraries.size)
                onClickListener { _ ->
                    findNavController().navigateSafely(MainFragmentDirections.actionMainFragmentToLibraryListSortingDialogFragment())
                }
            }

            val items = { items: List<Library> ->
                items.forEach { library ->
                    libraryItem {
                        id(library.id)
                        library(library)
                        notesCount(viewModel.countNotes(library.id))
                        isManualSorting(sorting == LibraryListSorting.Manual)
                        isShowNotesCount(isShowNotesCount)
                        onClickListener { _ ->
                            findNavController().navigateSafely(MainFragmentDirections.actionMainFragmentToLibraryFragment(library.id))
                        }
                        onLongClickListener { _ ->
                            findNavController().navigateSafely(MainFragmentDirections.actionMainFragmentToLibraryDialogFragment(library.id))
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


            if (libraries.isEmpty()) {
                placeholderItem {
                    id("placeholder")
                    placeholder(resources.stringResource(R.string.no_libraries_found))
                }
            } else {
                val pinnedLibraries = libraries.filter { it.isPinned }
                val notPinnedLibraries = libraries.filterNot { it.isPinned }

                if (pinnedLibraries.isNotEmpty()) {
                    tb.title = resources.stringResource(R.string.app_name)

                    headerItem {
                        id("pinned")
                        title(resources.stringResource(R.string.pinned))
                    }

                    items(pinnedLibraries)

                    if (notPinnedLibraries.isNotEmpty())
                        headerItem {
                            id("libraries")
                            title(resources.stringResource(R.string.libraries))
                        }
                } else {
                    tb.title = resources.stringResource(R.string.libraries)
                }
                items(notPinnedLibraries)
            }
        }
    }

    private fun MainFragmentBinding.setupLayoutMenuItem(): Boolean {
        when (viewModel.layout.value) {
            Layout.Linear -> {
                viewModel.updateLayout(Layout.Grid)
                root.snackbar(
                    getString(R.string.layout_is_grid_mode),
                    anchorView = fab
                )
            }
            Layout.Grid -> {
                viewModel.updateLayout(Layout.Linear)
                root.snackbar(
                    getString(R.string.layout_is_list_mode),
                    anchorView = fab
                )
            }
        }
        return true
    }

    private fun MainFragmentBinding.setupItemTouchHelper(layout: Layout) {
        if (this@MainFragment::epoxyController.isInitialized) {
            val itemTouchHelperCallback = LibraryItemTouchHelperCallback(epoxyController, layout) {
                rv.forEach { view ->
                    val viewHolder = rv.findContainingViewHolder(view) as EpoxyViewHolder
                    val model = viewHolder.model as? LibraryItem
                    if (model != null) viewModel.updateLibraryPosition(model.library, viewHolder.bindingAdapterPosition)
                }
            }
            itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
                .apply { attachToRecyclerView(rv) }
        }
    }

    private fun MainFragmentBinding.setupLibrariesArchiveMenuItem(): Boolean {
        findNavController().navigateSafely(MainFragmentDirections.actionMainFragmentToMainArchiveFragment())
        return true
    }

}