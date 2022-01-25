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
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainArchiveFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<MainViewModel>()

    private val selectedDestinationId by lazy { navController?.lastDestinationId }

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
            viewModel.isShowNotesCount,
        ) { libraries, isShowNotesCount ->
            setupLibraries(libraries, isShowNotesCount)
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
                            libraries.forEachRecursively { entry, depth ->
                                libraryItem {
                                    id(entry.first.id)
                                    library(entry.first)
                                    notesCount(entry.second)
                                    isManualSorting(false)
                                    isSelected(entry.first.id == selectedDestinationId)
                                    isShowNotesCount(isShowNotesCount)
                                    depth(depth)
                                    onClickListener { _ ->
                                        dismiss()
                                        if (entry.first.id != selectedDestinationId)
                                            navController?.navigateSafely(MainArchiveFragmentDirections.actionMainArchiveFragmentToLibraryFragment(
                                                entry.first.id))
                                    }
                                    onLongClickListener { _ ->
                                        dismiss()
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