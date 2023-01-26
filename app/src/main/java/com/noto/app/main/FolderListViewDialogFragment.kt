package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.FolderListViewDialogFragmentBinding
import com.noto.app.domain.model.FolderListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class FolderListViewDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FolderListViewDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.folders_view)

        viewModel.sortingType
            .onEach { sortingType ->
                context?.let { context ->
                    tvSortingTypeValue.text = when (sortingType) {
                        FolderListSortingType.Manual -> R.string.manual
                        FolderListSortingType.CreationDate -> R.string.creation_date
                        FolderListSortingType.Alphabetical -> R.string.alphabetical
                    }.let(context::stringResource)
                }

                if (sortingType == FolderListSortingType.Manual) {
                    llSortingOrder.isClickable = false
                    llSortingOrder.disable()
                } else {
                    llSortingOrder.isClickable = true
                    llSortingOrder.enable()
                }
            }
            .launchIn(lifecycleScope)

        viewModel.sortingOrder
            .onEach { sortingOrder ->
                context?.let { context ->
                    tvSortingOrderValue.text = when (sortingOrder) {
                        SortingOrder.Ascending -> R.string.ascending
                        SortingOrder.Descending -> R.string.descending
                    }.let(context::stringResource)
                }
            }
            .launchIn(lifecycleScope)

        llSortingType.setOnClickListener {
            navController?.navigateSafely(FolderListViewDialogFragmentDirections.actionFolderListViewDialogFragmentToFolderListSortingDialogFragment())
        }

        llSortingOrder.setOnClickListener {
            navController?.navigateSafely(FolderListViewDialogFragmentDirections.actionFolderListViewDialogFragmentToFolderListOrderingDialogFragment())
        }

        btnApply.setOnClickListener {
            dismiss()
        }
    }
}