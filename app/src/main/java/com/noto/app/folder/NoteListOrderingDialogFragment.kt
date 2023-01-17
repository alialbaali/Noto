package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteListOrderingDialogFragmentBinding
import com.noto.app.domain.model.GroupingOrder
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListOrderingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListOrderingDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteListOrderingDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.ordering)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    tb.tvDialogTitle.setTextColor(color)
                    tb.vHead.background?.mutate()?.setTint(color)
                    rbAscending.background = context.createDialogItemStateListDrawable()
                    rbDescending.background = context.createDialogItemStateListDrawable()
                }

                if (args.isSorting) {
                    when (folder.sortingOrder) {
                        SortingOrder.Ascending -> rbAscending.isChecked = true
                        SortingOrder.Descending -> rbDescending.isChecked = true
                    }
                } else {
                    when (folder.groupingOrder) {
                        GroupingOrder.Ascending -> rbAscending.isChecked = true
                        GroupingOrder.Descending -> rbDescending.isChecked = true
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbAscending.setOnClickListener {
            if (args.isSorting) {
                viewModel.updateSortingOrder(SortingOrder.Ascending)
            } else {
                viewModel.updateGroupingOrder(GroupingOrder.Ascending)
            }.invokeOnCompletion { dismiss() }
        }

        rbDescending.setOnClickListener {
            if (args.isSorting) {
                viewModel.updateSortingOrder(SortingOrder.Descending)
            } else {
                viewModel.updateGroupingOrder(GroupingOrder.Descending)
            }.invokeOnCompletion { dismiss() }
        }

    }
}