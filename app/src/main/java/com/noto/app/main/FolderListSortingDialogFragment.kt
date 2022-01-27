package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.FolderListSortingDialogFragmentBinding
import com.noto.app.domain.model.FolderListSortingType
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
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

        BaseDialogFragmentBinding.bind(root).apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.folders_sorting)
            }
        }

        viewModel.sortingType
            .onEach { sortingType ->
                when (sortingType) {
                    FolderListSortingType.Manual -> tvSortingOrder.isVisible = false
                    else -> tvSortingOrder.isVisible = true
                }
            }
            .launchIn(lifecycleScope)

        tvSortingType.setOnClickListener {
            navController
                ?.navigateSafely(FolderListSortingDialogFragmentDirections.actionFolderListSortingDialogFragmentToFolderListSortingTypeDialogFragment())
        }

        tvSortingOrder.setOnClickListener {
            navController
                ?.navigateSafely(FolderListSortingDialogFragmentDirections.actionFolderListSortingDialogFragmentToFolderListSortingOrderDialogFragment())
        }

        btnDone.setOnClickListener {
            dismiss()
        }
    }
}