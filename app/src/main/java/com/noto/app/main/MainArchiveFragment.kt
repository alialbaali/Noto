package com.noto.app.main

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.UiState
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.placeholderItem
import com.noto.app.databinding.MainArchiveFragmentBinding
import com.noto.app.domain.model.Folder
import com.noto.app.util.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainArchiveFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<MainViewModel>()

    private val selectedDestinationId by lazy { navController?.lastDestinationIdOrNull }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = MainArchiveFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
    }

    private fun MainArchiveFragmentBinding.setupState() {
//        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        rv.itemAnimator = VerticalListItemAnimator()
        tb.tvDialogTitle.text = context?.stringResource(R.string.folders_archive)

        combine(
            viewModel.archivedFolders,
            viewModel.isShowNotesCount,
        ) { folders, isShowNotesCount ->
            setupFolders(folders, isShowNotesCount)
        }.launchIn(lifecycleScope)

        rv.isScrollingAsFlow()
            .onEach { isScrolling -> tb.ll.isSelected = isScrolling }
            .launchIn(lifecycleScope)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun MainArchiveFragmentBinding.setupFolders(state: UiState<List<Pair<Folder, Int>>>, isShowNotesCount: Boolean) {
        if (state is UiState.Success) {
            val folders = state.value
            rv.withModels {
                context?.let { context ->
                    if (folders.isEmpty()) {
                        placeholderItem {
                            id("placeholder")
                            placeholder(context.stringResource(R.string.archive_is_empty))
                        }
                    } else {
                        buildFoldersModels(context, folders) { folders ->
                            folders.forEachRecursively { entry, depth ->
                                folderItem {
                                    id(entry.first.id)
                                    folder(entry.first)
                                    notesCount(entry.second)
                                    isManualSorting(false)
                                    isSelected(entry.first.id == selectedDestinationId)
                                    isShowNotesCount(isShowNotesCount)
                                    depth(depth)
                                    onClickListener { _ ->
                                        dismiss()
                                        if (entry.first.id != selectedDestinationId)
                                            navController?.navigateSafely(MainArchiveFragmentDirections.actionMainArchiveFragmentToFolderFragment(
                                                entry.first.id))
                                    }
                                    onLongClickListener { _ ->
                                        dismiss()
                                        navController?.navigateSafely(
                                            MainArchiveFragmentDirections.actionMainArchiveFragmentToFolderDialogFragment(entry.first.id)
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