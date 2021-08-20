package com.noto.app.main

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.R
import com.noto.app.databinding.MainFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.LibraryListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.*
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainFragment : Fragment() {

    private val viewModel by viewModel<MainViewModel>()

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
                R.id.layout_manager -> setupLayoutMangerMenuItem()
                R.id.theme -> setupThemeMenuItem()
                else -> false
            }
        }
    }

    private fun MainFragmentBinding.setupState() {
        val layoutManagerMenuItem = bab.menu.findItem(R.id.layout_manager)
        val layoutItems = listOf(tvLibrariesCount, rv)

        viewModel.state
            .onEach { state -> setupLibraries(state.libraries, state.layoutManager, state.sortingOrder, state.sorting, layoutItems) }
            .distinctUntilChangedBy { it.layoutManager }
            .onEach { state -> setupLayoutManager(state.layoutManager, layoutManagerMenuItem) }
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
        layoutManager: LayoutManager,
        sortingOrder: SortingOrder,
        sorting: LibraryListSorting,
        layoutItems: List<View>,
    ) {
        if (libraries.isEmpty()) {
            layoutItems.forEach { it.visibility = View.GONE }
            tvPlaceHolder.visibility = View.VISIBLE
        } else {
            layoutItems.forEach { it.visibility = View.VISIBLE }
            tvPlaceHolder.visibility = View.GONE
            setupModels(libraries, layoutManager, sorting, sortingOrder)
            tvLibrariesCount.text = libraries.size.toCountText(
                resources.stringResource(R.string.library),
                resources.stringResource(R.string.libraries)
            )
        }
    }

    private fun MainFragmentBinding.setupLayoutMangerMenuItem(): Boolean {
        when (viewModel.state.value.layoutManager) {
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
        layoutManager: LayoutManager,
        sorting: LibraryListSorting,
        sortingOrder: SortingOrder
    ) {
        rv.withModels {
            val itemTouchHelperCallback = LibraryItemTouchHelperCallback(this, layoutManager) { _, viewHolder, target ->
                val viewHolderModel = viewHolder.model as LibraryItem
                val targetModel = target.model as LibraryItem
                viewModel.updateLibraryPosition(viewHolderModel.library, viewHolder.bindingAdapterPosition)
                viewModel.updateLibraryPosition(targetModel.library, target.bindingAdapterPosition)
            }
            val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback).apply {
                attachToRecyclerView(rv)
            }

            libraryListSortingItem {
                id(0)
                sorting(sorting)
                sortingOrder(sortingOrder)
                onClickListener { _ ->
                    findNavController().navigate(MainFragmentDirections.actionMainFragmentToLibraryListSortingDialogFragment())
                }
            }

            libraries.forEach { library ->
                libraryItem {
                    id(library.id)
                    library(library)
                    notesCount(viewModel.countNotes(library.id))
                    isManualSorting(sorting == LibraryListSorting.Manually)
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
    }

    private fun MainFragmentBinding.setupThemeMenuItem(): Boolean {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToThemeDialogFragment())
        return true
    }

}