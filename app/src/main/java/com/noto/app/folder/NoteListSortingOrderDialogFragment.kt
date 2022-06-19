package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.NoteListSortingOrderDialogFragmentBinding
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListSortingOrderDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListSortingOrderDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteListSortingOrderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.sorting_order)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    tb.tvDialogTitle.setTextColor(color)
                    tb.vHead.background?.mutate()?.setTint(color)
                    rbSortingAsc.background = context.createDialogItemStateListDrawable(folder.color)
                    rbSortingDesc.background = context.createDialogItemStateListDrawable(folder.color)
                    when (folder.sortingOrder) {
                        SortingOrder.Ascending -> rbSortingAsc.isChecked = true
                        SortingOrder.Descending -> rbSortingDesc.isChecked = true
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbSortingAsc.setOnClickListener {
            viewModel.updateSortingOrder(SortingOrder.Ascending).invokeOnCompletion { dismiss() }
        }

        rbSortingDesc.setOnClickListener {
            viewModel.updateSortingOrder(SortingOrder.Descending).invokeOnCompletion { dismiss() }
        }
    }
}