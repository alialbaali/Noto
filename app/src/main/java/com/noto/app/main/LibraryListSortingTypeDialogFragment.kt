package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LibraryListSortingTypeDialogFragmentBinding
import com.noto.app.domain.model.LibraryListSortingType
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryListSortingTypeDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LibraryListSortingTypeDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        BaseDialogFragmentBinding.bind(root).apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.sorting_type)
            }
        }

        viewModel.sortingType
            .onEach { sortingType ->
                when (sortingType) {
                    LibraryListSortingType.Alphabetical -> rbAlphabetical.isChecked = true
                    LibraryListSortingType.CreationDate -> rbCreationDate.isChecked = true
                    LibraryListSortingType.Manual -> rbManual.isChecked = true
                }
            }
            .launchIn(lifecycleScope)

        rbManual.setOnClickListener {
            viewModel.updateSortingType(LibraryListSortingType.Manual)
                .invokeOnCompletion { dismiss() }
        }
        rbCreationDate.setOnClickListener {
            viewModel.updateSortingType(LibraryListSortingType.CreationDate)
            dismiss()
        }
        rbAlphabetical.setOnClickListener {
            viewModel.updateSortingType(LibraryListSortingType.Alphabetical)
            dismiss()
        }
    }
}