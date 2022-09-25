package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.FolderListSortingDialogFragmentBinding
import com.noto.app.domain.model.FolderListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.disable
import com.noto.app.util.enable
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class FolderListSortingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FolderListSortingDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.folders_sorting)

        viewModel.sortingType
            .onEach { sortingType ->
                when (sortingType) {
                    FolderListSortingType.Alphabetical -> rbAlphabetical.isChecked = true
                    FolderListSortingType.CreationDate -> rbCreationDate.isChecked = true
                    FolderListSortingType.Manual -> rbManual.isChecked = true
                }
                if (sortingType == FolderListSortingType.Manual) {
                    rbSortingAsc.isClickable = false
                    rbSortingDesc.isClickable = false
                    rgOrder.disable()
                    tvOrder.disable()
                } else {
                    rbSortingAsc.isClickable = true
                    rbSortingDesc.isClickable = true
                    rgOrder.enable()
                    tvOrder.enable()
                }
            }
            .launchIn(lifecycleScope)

        viewModel.sortingOrder
            .onEach { sortingOrder ->
                when (sortingOrder) {
                    SortingOrder.Ascending -> rbSortingAsc.isChecked = true
                    SortingOrder.Descending -> rbSortingDesc.isChecked = true
                }
            }
            .launchIn(lifecycleScope)

        rbManual.setOnClickListener {
            viewModel.updateSortingType(FolderListSortingType.Manual)
        }

        rbCreationDate.setOnClickListener {
            viewModel.updateSortingType(FolderListSortingType.CreationDate)
        }

        rbAlphabetical.setOnClickListener {
            viewModel.updateSortingType(FolderListSortingType.Alphabetical)
        }

        rbSortingAsc.setOnClickListener {
            viewModel.updateSortingOrder(SortingOrder.Ascending)
        }

        rbSortingDesc.setOnClickListener {
            viewModel.updateSortingOrder(SortingOrder.Descending)
        }

        btnApply.setOnClickListener {
            dismiss()
        }

    }
}