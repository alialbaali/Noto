package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LibraryListSortingOrderDialogFragmentBinding
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryListSortingOrderDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LibraryListSortingOrderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        BaseDialogFragmentBinding.bind(root).apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.sorting_order)
            }
        }

        viewModel.sortingOrder
            .onEach { sortingOrder ->
                when (sortingOrder) {
                    SortingOrder.Ascending -> rbSortingAsc.isChecked = true
                    SortingOrder.Descending -> rbSortingDesc.isChecked = true
                }
            }
            .launchIn(lifecycleScope)

        rbSortingAsc.setOnClickListener {
            dismiss()
            viewModel.updateSortingOrder(SortingOrder.Ascending)
        }
        rbSortingDesc.setOnClickListener {
            dismiss()
            viewModel.updateSortingOrder(SortingOrder.Descending)
        }
    }
}