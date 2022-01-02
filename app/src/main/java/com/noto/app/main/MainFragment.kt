package com.noto.app.main

import android.os.Bundle
import android.view.*
import android.view.animation.AnimationUtils
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyViewHolder
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.MainFragmentBinding
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.domain.model.LibraryListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.map
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
            navController?.navigateSafely(MainFragmentDirections.actionMainFragmentToNewLibraryDialogFragment())
        }

        bab.setNavigationOnClickListener {
            navController?.navigateSafely(MainFragmentDirections.actionMainFragmentToMainDialogFragment())
        }

        bab.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.layout -> setupLayoutMenuItem()
                R.id.libraries_archive -> setupLibrariesArchiveMenuItem()
                R.id.libraries_vault -> setupLibrariesVaultMenuItem()
                else -> false
            }
        }
    }

    private fun MainFragmentBinding.setupState() {
        val layoutManagerMenuItem = bab.menu.findItem(R.id.layout)
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

        combine(
            viewModel.libraries,
            viewModel.sortingType,
            viewModel.sortingOrder,
            viewModel.isShowNotesCount,
        ) { libraries, sortingType, sortingOrder, isShowNotesCount ->
            setupLibraries(libraries.map { it.sorted(sortingType, sortingOrder) }, sortingType, sortingOrder, isShowNotesCount)
        }.launchIn(lifecycleScope)

        viewModel.layout
            .onEach { layout -> setupLayoutManager(layout, layoutManagerMenuItem) }
            .combine(viewModel.libraries) { layout, _ -> layout }
            .onEach { layout -> setupItemTouchHelper(layout) }
            .launchIn(lifecycleScope)
    }

    private fun MainFragmentBinding.setupLayoutManager(layout: Layout, layoutManagerMenuItem: MenuItem) {
        context?.let { context ->
            when (layout) {
                Layout.Linear -> {
                    layoutManagerMenuItem.icon = context.drawableResource(R.drawable.ic_round_view_grid_24)
                    rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                }
                Layout.Grid -> {
                    layoutManagerMenuItem.icon = context.drawableResource(R.drawable.ic_round_view_agenda_24)
                    rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
                }
            }
        }
        rv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show))
    }

    private fun MainFragmentBinding.setupLibraries(
        state: UiState<List<Pair<Library, Int>>>,
        sortingType: LibraryListSortingType,
        sortingOrder: SortingOrder,
        isShowNotesCount: Boolean,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                rv.withModels {
                    epoxyController = this
                    val libraries = state.value

                    libraryListSortingItem {
                        id(0)
                        sortingType(sortingType)
                        sortingOrder(sortingOrder)
                        librariesCount(libraries.size)
                        onClickListener { _ ->
                            navController?.navigateSafely(MainFragmentDirections.actionMainFragmentToLibraryListSortingDialogFragment())
                        }
                    }

                    val items = { items: List<Pair<Library, Int>> ->
                        items.forEach { entry ->
                            libraryItem {
                                id(entry.first.id)
                                library(entry.first)
                                notesCount(entry.second)
                                isManualSorting(sortingType == LibraryListSortingType.Manual)
                                isShowNotesCount(isShowNotesCount)
                                onClickListener { _ ->
                                    navController?.navigateSafely(MainFragmentDirections.actionMainFragmentToLibraryFragment(entry.first.id))
                                }
                                onLongClickListener { _ ->
                                    navController?.navigateSafely(MainFragmentDirections.actionMainFragmentToLibraryDialogFragment(entry.first.id))
                                    true
                                }
                                onDragHandleTouchListener { view, event ->
                                    if (event.action == MotionEvent.ACTION_DOWN)
                                        rv.findContainingViewHolder(view)?.let { viewHolder ->
                                            if (this@MainFragment::itemTouchHelper.isInitialized)
                                                itemTouchHelper.startDrag(viewHolder)
                                        }
                                    view.performClick()
                                }
                            }
                        }
                    }

                    context?.let { context ->
                        if (libraries.isEmpty()) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_libraries_found_create))
                            }
                        } else {
                            val pinnedLibraries = libraries.filter { it.first.isPinned }
                            val notPinnedLibraries = libraries.filterNot { it.first.isPinned }

                            if (pinnedLibraries.isNotEmpty()) {
                                tb.title = context.stringResource(R.string.app_name)

                                headerItem {
                                    id("pinned")
                                    title(context.stringResource(R.string.pinned))
                                }

                                items(pinnedLibraries)

                                if (notPinnedLibraries.isNotEmpty())
                                    headerItem {
                                        id("libraries")
                                        title(context.stringResource(R.string.libraries))
                                    }
                            } else {
                                tb.title = context.stringResource(R.string.libraries)
                            }
                            items(notPinnedLibraries)
                        }
                    }
                }
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
        navController?.navigateSafely(MainFragmentDirections.actionMainFragmentToMainArchiveFragment())
        return true
    }

    private fun MainFragmentBinding.setupLibrariesVaultMenuItem(): Boolean {
        navController?.navigateSafely(MainFragmentDirections.actionMainFragmentToMainVaultFragment())
        return true
    }
}