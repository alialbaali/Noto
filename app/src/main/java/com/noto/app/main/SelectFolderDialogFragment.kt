package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.epoxy.group
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.placeholderItem
import com.noto.app.databinding.SelectFolderDialogFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.filtered.*
import com.noto.app.getOrDefault
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
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.itemAnimator = VerticalListItemAnimator()
        tb.tvDialogTitle.text = args.title

        combine(
            viewModel.folders,
            viewModel.isShowNotesCount,
        ) { folders, isShowNotesCount ->
            setupFolders(
                folders,
                isShowNotesCount,
                folders.getOrDefault(emptyList()).isEmpty(),
            )
        }.launchIn(lifecycleScope)

        rv.isScrollingAsFlow()
            .onEach { isScrolling -> tb.ll.isSelected = isScrolling }
            .launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun SelectFolderDialogFragmentBinding.setupFolders(
        state: UiState<List<Pair<Folder, Int>>>,
        isShowNotesCount: Boolean,
        isEmpty: Boolean,
    ) {
        when (state) {
            is UiState.Loading -> rv.setupProgressIndicator()
            is UiState.Success -> {
                val folders = state.value.filterNot { it.first.isGeneral }
                val generalFolder = state.value.firstOrNull { it.first.isGeneral }
                val callback = { id: Long, title: String ->
                    try {
                        navController?.previousBackStackEntry?.savedStateHandle?.apply {
                            set(Constants.FolderTitle, title)
                            set(args.key ?: Constants.FolderId, id)
                        }
                        onClick(id, title)
                    } catch (exception: IllegalStateException) {
                        onClick(id, title)
                    }
                    dismiss()
                }

                rv.withModels {

                    context?.let { context ->
                        if (args.isNoneEnabled) {
                            noneItem {
                                id("none")
                                isSelected(args.selectedFolderId == 0L)
                                onClickListener { _ -> callback(0L, context.stringResource(R.string.none)) }
                            }
                        }

                        if (args.isMainInterface) {
                            group(R.layout.vertical_linear_layout_group) {
                                id("header")

                                allFoldersItem {
                                    id("all_folders")
                                    isSelected(args.selectedFolderId == AllFoldersId)
                                    onClickListener { _ -> callback(AllFoldersId, context.stringResource(R.string.all_folders)) }
                                }

                                group(R.layout.horizontal_linear_layout_group) {
                                    id("sub_header_1")
                                    spanSizeOverride { _, _, _ -> 2 }

                                    filteredItem {
                                        id("all")
                                        model(FilteredItemModel.All)
                                        isShowNotesCount(false)
                                        isSelected(FilteredItemModel.All.id == args.selectedFolderId)
                                        onClickListener { _ -> callback(FilteredItemModel.All.id, context.stringResource(R.string.all)) }
                                    }

                                    filteredItem {
                                        id("recent")
                                        model(FilteredItemModel.Recent)
                                        isShowNotesCount(false)
                                        isSelected(FilteredItemModel.Recent.id == args.selectedFolderId)
                                        onClickListener { _ -> callback(FilteredItemModel.Recent.id, context.stringResource(R.string.recent)) }
                                    }
                                }

                                group(R.layout.horizontal_linear_layout_group) {
                                    id("sub_header_2")
                                    spanSizeOverride { _, _, _ -> 2 }

                                    filteredItem {
                                        id("scheduled")
                                        model(FilteredItemModel.Scheduled)
                                        isShowNotesCount(false)
                                        isSelected(FilteredItemModel.Scheduled.id == args.selectedFolderId)
                                        onClickListener { _ -> callback(FilteredItemModel.Scheduled.id, context.stringResource(R.string.scheduled)) }
                                    }

                                    filteredItem {
                                        id("archived")
                                        model(FilteredItemModel.Archived)
                                        isShowNotesCount(false)
                                        isSelected(FilteredItemModel.Archived.id == args.selectedFolderId)
                                        onClickListener { _ -> callback(FilteredItemModel.Archived.id, context.stringResource(R.string.archived)) }
                                    }
                                }
                            }
                        }

                        generalFolder?.let {
                            folderItem {
                                id(generalFolder.first.id)
                                folder(generalFolder.first)
                                notesCount(generalFolder.second)
                                isSelected(generalFolder.first.id == args.selectedFolderId)
                                isEnabled(if (args.filteredFolderIds.isEmpty()) true else generalFolder.first.id !in args.filteredFolderIds)
                                isManualSorting(false)
                                isShowNotesCount(isShowNotesCount && !args.isMainInterface)
                                onClickListener { _ -> callback(generalFolder.first.id, generalFolder.first.getTitle(context)) }
                                onLongClickListener { _ -> false }
                                onDragHandleTouchListener { _, _ -> false }
                            }
                        }

                        if (folders.isEmpty() && generalFolder == null && !args.isNoneEnabled) {
                            val placeholderId = when {
                                isEmpty -> R.string.no_folders_found
                                else -> R.string.no_relevant_folders_found
                            }

                            placeholderItem {
                                id("placeholder")
                                placeholder(context.stringResource(placeholderId))
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
                                        isEnabled(if (args.filteredFolderIds.isEmpty()) true else entry.first.id !in args.filteredFolderIds)
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