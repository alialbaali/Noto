package com.noto.app.library

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.SelectLibraryDialogFragmentBinding
import com.noto.app.domain.model.Layout
import com.noto.app.domain.model.Library
import com.noto.app.main.MainViewModel
import com.noto.app.main.libraryItem
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectLibraryDialogFragment(private val onClick: (Long) -> Unit = {}) : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    private val args by navArgs<SelectLibraryDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        SelectLibraryDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            if (!args.isDismissible) {
                dialog?.setCanceledOnTouchOutside(false)
                dialog?.setOnCancelListener { activity?.finish() }
            }
            setupBaseDialogFragment()
            setupState()
        }

    private fun SelectLibraryDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)
        .apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.select_library)
            }
        }

    private fun SelectLibraryDialogFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()

        combine(
            viewModel.libraries,
            viewModel.sortingType,
            viewModel.sortingOrder,
            viewModel.isShowNotesCount,
        ) { libraries, sortingType, sortingOrder, isShowNotesCount ->
            setupLibraries(
                libraries.map { it.filter { entry -> entry.first.id != args.libraryId }.sorted(sortingType, sortingOrder) },
                isShowNotesCount
            )
        }.launchIn(lifecycleScope)

        viewModel.layout
            .onEach { layout -> setupLayoutManager(layout) }
            .launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun SelectLibraryDialogFragmentBinding.setupLibraries(state: UiState<List<Pair<Library, Int>>>, isShowNotesCount: Boolean) {
        when (state) {
            is UiState.Loading -> rv.setupLoadingIndicator()
            is UiState.Success -> {
                val libraries = state.value
                rv.withModels {
                    if (libraries.isEmpty()) {
                        placeholderItem {
                            id("placeholder")
                            context?.let { context ->
                                placeholder(context.stringResource(R.string.no_libraries_found))
                            }
                        }
                    } else {
                        libraries.forEach { entry ->
                            libraryItem {
                                id(entry.first.id)
                                library(entry.first)
                                notesCount(entry.second)
                                isShowNotesCount(isShowNotesCount)
                                isManualSorting(false)
                                onClickListener { _ ->
                                    try {
                                        navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.LibraryId, entry.first.id)
                                    } catch (exception: IllegalStateException) {
                                        onClick(entry.first.id)
                                    }
                                    dismiss()
                                }
                                onLongClickListener { _ -> false }
                                onDragHandleTouchListener { _, _ -> false }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun SelectLibraryDialogFragmentBinding.setupLayoutManager(layout: Layout) {
        when (layout) {
            Layout.Linear -> rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            Layout.Grid -> rv.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        }
    }
}