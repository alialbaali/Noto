package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteListViewDialogFragmentBinding
import com.noto.app.domain.model.*
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
                    listOf(llSorting, llGrouping).onEach { ll ->
                        ll.background?.setRippleColor(colorStateList)
                    }
                }
                when (folder.filteringType) {
                    FilteringType.Inclusive -> tlFilteringType.getTabAt(0)?.select()
                    FilteringType.Exclusive -> tlFilteringType.getTabAt(1)?.select()
                    FilteringType.Strict -> tlFilteringType.getTabAt(2)?.select()
                }
                val sortingStringId = when (folder.sortingType) {
                    NoteListSortingType.Manual -> R.string.manual
                    NoteListSortingType.CreationDate -> R.string.creation_date
                    NoteListSortingType.Alphabetical -> R.string.alphabetical
                    NoteListSortingType.AccessDate -> R.string.access_date
                }
                val sortingDrawableId = if (folder.sortingType != NoteListSortingType.Manual) {
                    when (folder.sortingOrder) {
                        SortingOrder.Ascending -> R.drawable.ic_round_arrow_upward_24
                        SortingOrder.Descending -> R.drawable.ic_round_arrow_downward_24
                    }
                } else {
                    0
                }
                tvSortingValue.text = context?.stringResource(sortingStringId)
                tvSortingValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, sortingDrawableId, 0)
                val groupingStringId = when (folder.grouping) {
                    Grouping.Default -> R.string.default_grouping
                    Grouping.CreationDate -> R.string.creation_date
                    Grouping.Label -> R.string.label
                    Grouping.AccessDate -> R.string.access_date
                }
                val groupingDrawableId = when (folder.groupingOrder) {
                    GroupingOrder.Ascending -> R.drawable.ic_round_arrow_upward_24
                    GroupingOrder.Descending -> R.drawable.ic_round_arrow_downward_24
                }
                tvGroupingValue.text = context?.stringResource(groupingStringId)
                tvGroupingValue.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, groupingDrawableId, 0)
            }
            .launchIn(lifecycleScope)

        llSorting.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListSortingDialogFragment(
                    args.folderId
                )
            )
        }

        llGrouping.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListGroupingDialogFragment(
                    args.folderId
                )
            )
        }

        btnApply.setOnClickListener {
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
