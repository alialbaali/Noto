package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.SelectLibraryDialogFragmentBinding
import com.noto.app.domain.model.Library
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectLibraryDialogFragment constructor() : BaseDialogFragment() {

    private var onClick: (Long) -> Unit = {}

    constructor(onClick: (Long) -> Unit = {}) : this() {
        this.onClick = onClick
    }

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
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.itemAnimator = VerticalListItemAnimator()

        combine(
            viewModel.libraries,
            viewModel.isShowNotesCount,
        ) { libraries, isShowNotesCount ->
            setupLibraries(
                libraries.map { it.filterRecursively { entry -> entry.first.id != args.libraryId } },
                isShowNotesCount
            )
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun SelectLibraryDialogFragmentBinding.setupLibraries(state: UiState<List<Pair<Library, Int>>>, isShowNotesCount: Boolean) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val libraries = state.value.filterNot { it.first.isInbox }
                val inboxLibrary = state.value.firstOrNull { it.first.isInbox }
                val callback = { library: Library ->
                    try {
                        navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.LibraryId, library.id)
                    } catch (exception: IllegalStateException) {
                        onClick(library.id)
                    }
                    dismiss()
                }

                rv.withModels {

                    inboxLibrary?.let {
                        libraryItem {
                            id(inboxLibrary.first.id)
                            library(inboxLibrary.first)
                            notesCount(inboxLibrary.second)
                            isSelected(inboxLibrary.first.id == args.selectedLibraryId)
                            isManualSorting(false)
                            isShowNotesCount(isShowNotesCount)
                            onClickListener { _ -> callback(inboxLibrary.first) }
                            onLongClickListener { _ -> false }
                            onDragHandleTouchListener { _, _ -> false }
                        }
                    }

                    context?.let { context ->
                        if (libraries.isEmpty() && inboxLibrary == null) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_libraries_found))
                            }
                        } else {
                            buildLibrariesModels(context, libraries) { libraries ->
                                libraries.forEachRecursively { entry, depth ->
                                    libraryItem {
                                        id(entry.first.id)
                                        library(entry.first)
                                        notesCount(entry.second)
                                        isShowNotesCount(isShowNotesCount)
                                        isSelected(entry.first.id == args.selectedLibraryId)
                                        isManualSorting(false)
                                        depth(depth)
                                        onClickListener { _ -> callback(entry.first) }
                                        onLongClickListener { _ -> false }
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
}