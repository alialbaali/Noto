package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.FolderListSortingTypeDialogFragmentBinding
import com.noto.app.domain.model.FolderListSortingType
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class FolderListSortingTypeDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FolderListSortingTypeDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.sorting_type)

        viewModel.sortingType
            .onEach { sortingType ->
                when (sortingType) {
                    FolderListSortingType.Alphabetical -> rbAlphabetical.isChecked = true
                    FolderListSortingType.CreationDate -> rbCreationDate.isChecked = true
                    FolderListSortingType.Manual -> rbManual.isChecked = true
                }
            }
            .launchIn(lifecycleScope)

        rbManual.setOnClickListener {
            viewModel.updateSortingType(FolderListSortingType.Manual)
                .invokeOnCompletion { dismiss() }
        }
        rbCreationDate.setOnClickListener {
            viewModel.updateSortingType(FolderListSortingType.CreationDate)
            dismiss()
        }
        rbAlphabetical.setOnClickListener {
            viewModel.updateSortingType(FolderListSortingType.Alphabetical)
            dismiss()
        }
    }
}