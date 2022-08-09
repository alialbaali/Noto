package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.NoteListViewDialogFragmentBinding
import com.noto.app.domain.model.FilteringType
import com.noto.app.domain.model.NoteListSortingType
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListViewDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListViewDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteListViewDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.notes_view)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    val colorStateList = color.toColorStateList()
                    tb.tvDialogTitle.setTextColor(color)
                    tb.vHead.background?.mutate()?.setTint(color)
                    listOf(tvGrouping, tvGroupingOrder, tvSortingType, tvSortingOrder).onEach { tv ->
                        tv.background.setRippleColor(colorStateList)
                    }
                }
                when (folder.sortingType) {
                    NoteListSortingType.Manual -> tvSortingOrder.isVisible = false
                    else -> tvSortingOrder.isVisible = true
                }
                when (folder.filteringType) {
                    FilteringType.Inclusive -> tlFilteringType.getTabAt(0)?.select()
                    FilteringType.Exclusive -> tlFilteringType.getTabAt(1)?.select()
                    FilteringType.Strict -> tlFilteringType.getTabAt(2)?.select()
                }
            }
            .launchIn(lifecycleScope)

        tvGrouping.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListGroupingDialogFragment(
                    args.folderId
                )
            )
        }

        tvGroupingOrder.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListGroupingOrderDialogFragment(
                    args.folderId
                )
            )
        }

        tvSortingType.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListSortingTypeDialogFragment(
                    args.folderId
                )
            )
        }

        tvSortingOrder.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListSortingOrderDialogFragment(
                    args.folderId
                )
            )
        }

        btnDone.setOnClickListener {
            val filteringType = when (tlFilteringType.selectedTabPosition) {
                0 -> FilteringType.Inclusive
                1 -> FilteringType.Exclusive
                else -> FilteringType.Strict
            }
            viewModel.updateFolderFilteringType(filteringType)
                .invokeOnCompletion { dismiss() }
        }
    }
}
