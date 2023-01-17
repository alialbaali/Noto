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
                    btnApply.background?.mutate()?.setTint(color)
                    listOf(llSortingType, llGroupingType, llFiltering, llSortingOrder, llGroupingOrder)
                        .onEach { ll -> ll.background?.setRippleColor(colorStateList) }
                    tvFilteringValue.text = when (folder.filteringType) {
                        FilteringType.Inclusive -> R.string.inclusive
                        FilteringType.Exclusive -> R.string.exclusive
                        FilteringType.Strict -> R.string.strict
                    }.let(context::stringResource)
                    tvSortingTypeValue.text = when (folder.sortingType) {
                        NoteListSortingType.Manual -> R.string.manual
                        NoteListSortingType.CreationDate -> R.string.creation_date
                        NoteListSortingType.Alphabetical -> R.string.alphabetical
                        NoteListSortingType.AccessDate -> R.string.access_date
                    }.let(context::stringResource)
                    tvSortingOrderValue.text = when (folder.sortingOrder) {
                        SortingOrder.Ascending -> R.string.ascending
                        SortingOrder.Descending -> R.string.descending
                    }.let(context::stringResource)
                    tvGroupingTypeValue.text = when (folder.grouping) {
                        Grouping.None -> R.string.none
                        Grouping.CreationDate -> R.string.creation_date
                        Grouping.Label -> R.string.label
                        Grouping.AccessDate -> R.string.access_date
                    }.let(context::stringResource)
                    tvGroupingOrderValue.text = when (folder.groupingOrder) {
                        GroupingOrder.Ascending -> R.string.ascending
                        GroupingOrder.Descending -> R.string.descending
                    }.let(context::stringResource)
                }
                if (folder.sortingType == NoteListSortingType.Manual) {
                    llSortingOrder.isClickable = false
                    llSortingOrder.disable()
                } else {
                    llSortingOrder.isClickable = true
                    llSortingOrder.enable()
                }
                if (folder.grouping == Grouping.None) {
                    llGroupingOrder.isClickable = false
                    llGroupingOrder.disable()
                } else {
                    llGroupingOrder.isClickable = true
                    llGroupingOrder.enable()
                }
            }
            .launchIn(lifecycleScope)

        llFiltering.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListFilteringDialogFragment(
                    args.folderId
                )
            )
        }

        llSortingType.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListSortingDialogFragment(
                    args.folderId
                )
            )
        }

        llSortingOrder.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListOrderingDialogFragment(
                    args.folderId, isSorting = true,
                )
            )
        }

        llGroupingType.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListGroupingDialogFragment(
                    args.folderId
                )
            )
        }

        llGroupingOrder.setOnClickListener {
            navController?.navigateSafely(
                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListOrderingDialogFragment(
                    args.folderId, isSorting = false,
                )
            )
        }

        btnApply.setOnClickListener {
            dismiss()
        }
    }
}
