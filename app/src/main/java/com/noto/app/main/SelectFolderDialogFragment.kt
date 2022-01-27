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
import com.noto.app.databinding.SelectFolderDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectFolderDialogFragment constructor() : BaseDialogFragment() {

    private var onClick: (Long) -> Unit = {}

    constructor(onClick: (Long) -> Unit = {}) : this() {
        this.onClick = onClick
    }

    private val viewModel by viewModel<MainViewModel>()

    private val args by navArgs<SelectFolderDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        SelectFolderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            if (!args.isDismissible) {
                dialog?.setCanceledOnTouchOutside(false)
                dialog?.setOnCancelListener { activity?.finish() }
            }
            setupBaseDialogFragment()
            setupState()
        }

    private fun SelectFolderDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)
        .apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.select_folder)
            }
        }

    private fun SelectFolderDialogFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.itemAnimator = VerticalListItemAnimator()

        combine(
            viewModel.folders,
            viewModel.isShowNotesCount,
        ) { folders, isShowNotesCount ->
            setupFolders(
                folders.map { it.filterRecursively { entry -> args.filteredFolderIds.none { it == entry.first.id } } },
                isShowNotesCount
            )
        }.launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun SelectFolderDialogFragmentBinding.setupFolders(state: UiState<List<Pair<Folder, Int>>>, isShowNotesCount: Boolean) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val folders = state.value.filterNot { it.first.isGeneral }
                val generalFolder = state.value.firstOrNull { it.first.isGeneral }
                val callback = { id: Long ->
                    try {
                        navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.FolderId, id)
                    } catch (exception: IllegalStateException) {
                        onClick(id)
                    }
                    dismiss()
                }

                rv.withModels {

                    if (args.isNoParentEnabled) {
                        noParentItem {
                            id("no_parent")
                            isSelected(args.selectedFolderId == 0L)
                            onClickListener { _ -> callback(0L) }
                        }
                    }

                    generalFolder?.let {
                        folderItem {
                            id(generalFolder.first.id)
                            folder(generalFolder.first)
                            notesCount(generalFolder.second)
                            isSelected(generalFolder.first.id == args.selectedFolderId)
                            isManualSorting(false)
                            isShowNotesCount(isShowNotesCount)
                            onClickListener { _ -> callback(generalFolder.first.id) }
                            onLongClickListener { _ -> false }
                            onDragHandleTouchListener { _, _ -> false }
                        }
                    }

                    context?.let { context ->
                        if (folders.isEmpty() && generalFolder == null) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_folders_found))
                            }
                        } else {
                            buildFoldersModels(context, folders) { libraries ->
                                libraries.forEachRecursively { entry, depth ->
                                    folderItem {
                                        id(entry.first.id)
                                        folder(entry.first)
                                        notesCount(entry.second)
                                        isShowNotesCount(isShowNotesCount)
                                        isSelected(entry.first.id == args.selectedFolderId)
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