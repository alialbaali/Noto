package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.FolderListOrderingDialogFragmentBinding
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class FolderListOrderingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = FolderListOrderingDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.folders_ordering)

        viewModel.sortingOrder
            .onEach { sortingOrder ->
                when (sortingOrder) {
                    SortingOrder.Ascending -> rbAscending.isChecked = true
                    SortingOrder.Descending -> rbDescending.isChecked = true
                }
            }
            .launchIn(lifecycleScope)

        rbAscending.setOnClickListener {
            viewModel.updateSortingOrder(SortingOrder.Ascending)
                .invokeOnCompletion { dismiss() }
        }

        rbDescending.setOnClickListener {
            viewModel.updateSortingOrder(SortingOrder.Descending)
                .invokeOnCompletion { dismiss() }
        }
    }
}