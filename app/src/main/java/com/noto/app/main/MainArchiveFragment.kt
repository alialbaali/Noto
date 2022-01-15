package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.MainArchiveFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainArchiveFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<MainViewModel>()

    private val selectedLibraryId by lazy { navController?.lastLibraryId }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        MainArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
        }

    private fun MainArchiveFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = context?.stringResource(R.string.libraries_archive)
    }

    private fun MainArchiveFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.itemAnimator = VerticalListItemAnimator()

        combine(
            viewModel.archivedLibraries,
            viewModel.sortingType,
            viewModel.sortingOrder,
            viewModel.isShowNotesCount,
        ) { libraries, sortingType, sortingOrder, isShowNotesCount ->
            setupLibraries(libraries.map { it.sorted(sortingType, sortingOrder) }, isShowNotesCount)
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun MainArchiveFragmentBinding.setupLibraries(state: UiState<List<Pair<Library, Int>>>, isShowNotesCount: Boolean) {
        if (state is UiState.Success) {
            val libraries = state.value
            rv.withModels {
                context?.let { context ->
                    if (libraries.isEmpty()) {
                        placeholderItem {
                            id("placeholder")
                            placeholder(context.stringResource(R.string.archive_is_empty))
                        }
                    } else {
                        buildLibrariesModels(context, libraries) { libraries ->
                            libraries.forEach { entry ->
                                libraryItem {
                                    id(entry.first.id)
                                    library(entry.first)
                                    notesCount(entry.second)
                                    isManualSorting(false)
                                    isSelected(entry.first.id == selectedLibraryId)
                                    isShowNotesCount(isShowNotesCount)
                                    onClickListener { _ ->
                                        if (entry.first.id != selectedLibraryId)
                                            navController?.navigateSafely(MainArchiveFragmentDirections.actionMainArchiveFragmentToLibraryFragment(
                                                entry.first.id))
                                        dismiss()
                                    }
                                    onLongClickListener { _ ->
                                        navController?.navigateSafely(
                                            MainArchiveFragmentDirections.actionMainArchiveFragmentToLibraryDialogFragment(entry.first.id)
                                        )
                                        true
                                    }
                                    onDragHandleTouchListener { _, _ -> false }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}