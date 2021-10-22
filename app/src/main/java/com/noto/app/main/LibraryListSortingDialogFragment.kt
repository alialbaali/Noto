package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LibraryListSortingDialogFragmentBinding
import com.noto.app.domain.model.LibraryListSortingType
import com.noto.app.util.navigateSafely
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class LibraryListSortingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LibraryListSortingDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        BaseDialogFragmentBinding.bind(root).apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.libraries_sorting)
            }
        }

        viewModel.sortingType
            .onEach { sortingType ->
                when (sortingType) {
                    LibraryListSortingType.Manual -> tvSortingOrder.isVisible = false
                    else -> tvSortingOrder.isVisible = true
                }
            }
            .launchIn(lifecycleScope)

        tvSortingType.setOnClickListener {
            findNavController()
                .navigateSafely(LibraryListSortingDialogFragmentDirections.actionLibraryListSortingDialogFragmentToLibraryListSortingTypeDialogFragment())
        }

        tvSortingOrder.setOnClickListener {
            findNavController()
                .navigateSafely(LibraryListSortingDialogFragmentDirections.actionLibraryListSortingDialogFragmentToLibraryListSortingOrderDialogFragment())
        }

        btnDone.setOnClickListener {
            dismiss()
        }
    }
}