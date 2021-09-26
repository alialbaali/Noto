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
import com.noto.app.domain.model.LayoutManager
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

        viewModel.layoutManager
            .onEach { layoutManager -> setupLayoutManger(layoutManager) }
            .launchIn(lifecycleScope)
    }

    private fun MainArchiveFragmentBinding.setupLayoutManger(layoutManager: LayoutManager) {
        when (layoutManager) {
            LayoutManager.Linear -> rv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            LayoutManager.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
        rv.visibility = View.VISIBLE
        rv.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.show))
    }

    private fun MainArchiveFragmentBinding.setupLibraries(libraries: List<Library>, isShowNotesCount: Boolean) {
        if (libraries.isEmpty()) {
            rv.visibility = View.GONE
            tvPlaceHolder.visibility = View.VISIBLE
        } else {
            rv.visibility = View.VISIBLE
            tvPlaceHolder.visibility = View.GONE
            setupModels(libraries, isShowNotesCount)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun MainArchiveFragmentBinding.setupModels(libraries: List<Library>, isShowNotesCount: Boolean) {
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
                            findNavController().navigate(MainArchiveFragmentDirections.actionMainArchiveFragmentToLibraryFragment(library.id))
                        }
                        onLongClickListener { _ ->
                            findNavController().navigate(MainArchiveFragmentDirections.actionMainArchiveFragmentToLibraryDialogFragment(library.id))
                            true
                        }
                        onDragHandleTouchListener { _, _ -> false }
                    }
                }
            }

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