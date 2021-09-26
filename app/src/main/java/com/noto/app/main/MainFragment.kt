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
import com.noto.app.domain.model.LayoutManager
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
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToNewLibraryDialogFragment())
        }

        bab.setNavigationOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToMainDialogFragment())
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.layout -> setupLayoutMangerMenuItem()
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

        viewModel.layoutManager
            .onEach { layoutManager -> setupLayoutManager(layoutManager, layoutManagerMenuItem) }
            .combine(viewModel.libraries) { layoutManager, _ -> layoutManager }
            .onEach { layoutManager -> setupItemTouchHelper(layoutManager) }
            .launchIn(lifecycleScope)
    }

    private fun MainFragmentBinding.setupLayoutManager(layoutManager: LayoutManager, layoutManagerMenuItem: MenuItem) {
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
        rv.visibility = View.INVISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.hide))

        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
    }

    private fun MainFragmentBinding.setupLibraries(
        libraries: List<Library>,
        sorting: LibraryListSorting,
        sortingOrder: SortingOrder,
        isShowNotesCount: Boolean,
    ) {
        if (libraries.isEmpty()) {
            rv.visibility = View.GONE
            tvPlaceHolder.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            tvPlaceHolder.visibility = View.GONE
            setupModels(libraries, sorting, sortingOrder, isShowNotesCount)
        }
    }

    private fun MainFragmentBinding.setupLayoutMangerMenuItem(): Boolean {
        when (viewModel.layoutManager.value) {
            LayoutManager.Linear -> {
                viewModel.updateLayoutManager(LayoutManager.Grid)
                root.snackbar(
                    getString(R.string.layout_is_grid_mode),
                    anchorView = fab
                )
            }
            LayoutManager.Grid -> {
                viewModel.updateLayoutManager(LayoutManager.Linear)
                root.snackbar(
                    getString(R.string.layout_is_list_mode),
                    anchorView = fab
                )
            }
        }
        return true
    }

    private fun MainFragmentBinding.setupModels(
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
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToLibraryListSortingDialogFragment())
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
                            findNavController().navigate(MainFragmentDirections.actionMainFragmentToLibraryFragment(library.id))
                        }
                        onLongClickListener { _ ->
                            findNavController().navigate(MainFragmentDirections.actionMainFragmentToLibraryDialogFragment(library.id))
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

    private fun MainFragmentBinding.setupItemTouchHelper(layoutManager: LayoutManager) {
        if (this@MainFragment::epoxyController.isInitialized) {
            val itemTouchHelperCallback = LibraryItemTouchHelperCallback(epoxyController, layoutManager) {
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
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToMainArchiveFragment())
        return true
    }

}