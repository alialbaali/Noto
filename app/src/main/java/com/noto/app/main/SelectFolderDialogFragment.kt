package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.genericItem
import com.noto.app.components.placeholderItem
import com.noto.app.databinding.SelectFolderDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.map
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectFolderDialogFragment constructor() : BaseDialogFragment(isCollapsable = true) {

    private var onClick: (Long, String) -> Unit = { _, _ -> }

    constructor(onClick: (Long, String) -> Unit = { _, _ -> }) : this() {
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
            setupState()
        }

    private fun SelectFolderDialogFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.itemAnimator = VerticalListItemAnimator()
        tb.tvDialogTitle.text = context?.stringResource(if (args.isMainInterface) R.string.select_main_interface else R.string.select_folder)

        combine(
            viewModel.folders,
            viewModel.isShowNotesCount,
        ) { folders, isShowNotesCount ->
            setupFolders(
                folders.map { it.filterRecursively { entry -> args.filteredFolderIds.none { it == entry.first.id } } },
                isShowNotesCount
            )
        }.launchIn(lifecycleScope)

        rv.isScrollingAsFlow()
            .onEach { isScrolling -> tb.ll.isSelected = isScrolling }
            .launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun SelectFolderDialogFragmentBinding.setupFolders(state: UiState<List<Pair<Folder, Int>>>, isShowNotesCount: Boolean) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val folders = state.value.filterNot { it.first.isGeneral }
                val generalFolder = state.value.firstOrNull { it.first.isGeneral }
                val callback = { id: Long, title: String ->
                    try {
                        navController?.previousBackStackEntry?.savedStateHandle?.apply {
                            set(Constants.FolderTitle, title)
                            set(Constants.FolderId, id)
                        }
                        onClick(id, title)
                    } catch (exception: IllegalStateException) {
                        onClick(id, title)
                    }
                    dismiss()
                }

                rv.withModels {

                    context?.let { context ->
                        if (args.isNoParentEnabled) {
                            noParentItem {
                                id("no_parent")
                                isSelected(args.selectedFolderId == 0L)
                                onClickListener { _ -> callback(0L, context.stringResource(R.string.no_parent)) }
                            }
                        }

                        if (args.isMainInterface) {
                            genericItem {
                                id("all_folders")
                                title(context.stringResource(R.string.all_folders))
                                icon(context.drawableResource(R.drawable.ic_round_all_folders_24))
                                isManualSorting(false)
                                isShowNotesCount(false)
                                isSelected(args.selectedFolderId == AllFoldersId)
                                onClickListener { _ -> callback(AllFoldersId, context.stringResource(R.string.all_folders)) }
                            }

                            genericItem {
                                id("all_notes")
                                title(context.stringResource(R.string.all_notes))
                                icon(context.drawableResource(R.drawable.ic_round_all_notes_24))
                                isManualSorting(false)
                                isShowNotesCount(false)
                                isSelected(args.selectedFolderId == AllNotesItemId)
                                onClickListener { _ -> callback(AllNotesItemId, context.stringResource(R.string.all_notes)) }
                            }

                            genericItem {
                                id("recent_notes")
                                title(context.getString(R.string.recent_notes))
                                icon(context.drawableResource(R.drawable.ic_round_schedule_24))
                                isManualSorting(false)
                                isShowNotesCount(false)
                                isSelected(args.selectedFolderId == RecentNotesItemId)
                                onClickListener { _ -> callback(RecentNotesItemId, context.stringResource(R.string.recent_notes)) }
                            }
                        }

                        generalFolder?.let {
                            folderItem {
                                id(generalFolder.first.id)
                                folder(generalFolder.first)
                                notesCount(generalFolder.second)
                                isSelected(generalFolder.first.id == args.selectedFolderId)
                                isManualSorting(false)
                                isShowNotesCount(isShowNotesCount && !args.isMainInterface)
                                onClickListener { _ -> callback(generalFolder.first.id, generalFolder.first.getTitle(context)) }
                                onLongClickListener { _ -> false }
                                onDragHandleTouchListener { _, _ -> false }
                            }
                        }

                        if (folders.isEmpty() && generalFolder == null && !args.isNoParentEnabled) {
                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(R.string.no_folders_found))
                            }
                        } else {
                            buildFoldersModels(context, folders) { folders ->
                                folders.forEachRecursively { entry, depth ->
                                    folderItem {
                                        id(entry.first.id)
                                        folder(entry.first)
                                        notesCount(entry.second)
                                        isShowNotesCount(isShowNotesCount && !args.isMainInterface)
                                        isSelected(entry.first.id == args.selectedFolderId)
                                        isManualSorting(false)
                                        depth(depth)
                                        onClickListener { _ -> callback(entry.first.id, entry.first.getTitle(context)) }
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