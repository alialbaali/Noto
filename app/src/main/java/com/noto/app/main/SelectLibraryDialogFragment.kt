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
import com.noto.app.domain.model.Folder
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
                libraries.map { it.filterRecursively { entry -> args.filteredLibraryIds.none { it == entry.first.id } } },
                isShowNotesCount
            )
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun SelectLibraryDialogFragmentBinding.setupLibraries(state: UiState<List<Pair<Folder, Int>>>, isShowNotesCount: Boolean) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val libraries = state.value.filterNot { it.first.isInbox }
                val inboxLibrary = state.value.firstOrNull { it.first.isInbox }
                val callback = { id: Long ->
                    try {
                        navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.LibraryId, id)
                    } catch (exception: IllegalStateException) {
                        onClick(id)
                    }
                    dismiss()
                }

                rv.withModels {

                    if (args.isNoParentEnabled) {
                        noParentItem {
                            id("no_parent")
                            isSelected(args.selectedLibraryId == 0L)
                            onClickListener { _ -> callback(0L) }
                        }
                    }

                    inboxLibrary?.let {
                        folderItem {
                            id(inboxLibrary.first.id)
                            folder(inboxLibrary.first)
                            notesCount(inboxLibrary.second)
                            isSelected(inboxLibrary.first.id == args.selectedLibraryId)
                            isManualSorting(false)
                            isShowNotesCount(isShowNotesCount)
                            onClickListener { _ -> callback(inboxLibrary.first.id) }
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
                                    folderItem {
                                        id(entry.first.id)
                                        folder(entry.first)
                                        notesCount(entry.second)
                                        isShowNotesCount(isShowNotesCount)
                                        isSelected(entry.first.id == args.selectedLibraryId)
                                        isManualSorting(false)
                                        depth(depth)
                                        onClickListener { _ -> callback(entry.first.id) }
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