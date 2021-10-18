package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.R
import com.noto.app.databinding.MainArchiveFragmentBinding
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainArchiveFragment : Fragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        MainArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
            setupListeners()
            setupState()
        }

    private fun MainArchiveFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun MainArchiveFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

        combine(
            viewModel.archivedLibraries,
            viewModel.sorting,
            viewModel.sortingOrder,
            viewModel.isShowNotesCount,
        ) { libraries, sorting, sortingOrder, isShowNotesCount ->
            setupLibraries(libraries.sorted(sorting, sortingOrder), isShowNotesCount)
        }
            .launchIn(lifecycleScope)

        viewModel.layout
            .onEach { layout -> setupLayoutManager(layout) }
            .launchIn(lifecycleScope)
    }

    private fun MainArchiveFragmentBinding.setupLayoutManager(layout: Layout) {
        when (layout) {
            Layout.Linear -> rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            Layout.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(context, R.anim.show))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun MainArchiveFragmentBinding.setupLibraries(libraries: List<Library>, isShowNotesCount: Boolean) {
        rv.withModels {
            val items = { items: List<Library> ->
                items.forEach { library ->
                    libraryItem {
                        id(library.id)
                        library(library)
                        notesCount(viewModel.countNotes(library.id))
                        isManualSorting(false)
                        isShowNotesCount(isShowNotesCount)
                        onClickListener { _ ->
                            findNavController().navigateSafely(MainArchiveFragmentDirections.actionMainArchiveFragmentToLibraryFragment(library.id))
                        }
                        onLongClickListener { _ ->
                            findNavController().navigateSafely(MainArchiveFragmentDirections.actionMainArchiveFragmentToLibraryDialogFragment(library.id))
                            true
                        }
                        onDragHandleTouchListener { _, _ -> false }
                    }
                }
            }

            if (libraries.isEmpty()) {
                placeholderItem {
                    id("placeholder")
                    placeholder(resources.stringResource(R.string.archive_is_empty))
                }
            } else {
                val pinnedLibraries = libraries.filter { it.isPinned }
                val notPinnedLibraries = libraries.filterNot { it.isPinned }

                if (pinnedLibraries.isNotEmpty()) {
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
                }
                items(notPinnedLibraries)
            }
        }
    }
}