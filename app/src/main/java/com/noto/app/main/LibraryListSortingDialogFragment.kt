package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LibraryListSortingDialogFragmentBinding
import com.noto.app.domain.model.LibraryListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.colorResource
import com.noto.app.util.colorStateResource
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
            tvDialogTitle.text = resources.stringResource(R.string.libraries_sorting)
        }

        viewModel.state
            .onEach { state ->
                when (state.sortingOrder) {
                    SortingOrder.Ascending -> rbSortingAsc.isChecked = true
                    SortingOrder.Descending -> rbSortingDesc.isChecked = true
                }
                when (state.sorting) {
                    LibraryListSorting.Alphabetical -> {
                        rbAlphabetical.isChecked = true
                        enableSortingOrder()
                    }
                    LibraryListSorting.CreationDate -> {
                        rbCreationDate.isChecked = true
                        enableSortingOrder()
                    }
                    LibraryListSorting.Manual -> {
                        rbManual.isChecked = true
                        disableSortingOrder()
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbSortingAsc.setOnClickListener { viewModel.updateSortingOrder(SortingOrder.Ascending) }
        rbSortingDesc.setOnClickListener { viewModel.updateSortingOrder(SortingOrder.Descending) }
        rbManual.setOnClickListener { viewModel.updateSorting(LibraryListSorting.Manual) }
        rbCreationDate.setOnClickListener { viewModel.updateSorting(LibraryListSorting.CreationDate) }
        rbAlphabetical.setOnClickListener { viewModel.updateSorting(LibraryListSorting.Alphabetical) }

        btnDone.setOnClickListener {
            dismiss()
        }
    }

    private fun LibraryListSortingDialogFragmentBinding.disableSortingOrder() {
        val disabledColor = resources.colorResource(R.color.colorOnPrimary)
        val disableColorState = resources.colorStateResource(R.color.colorOnPrimary)
        viewModel.updateSortingOrder(SortingOrder.Ascending)
        rbSortingAsc.isChecked = true
        rbSortingAsc.isEnabled = false
        rbSortingDesc.isEnabled = false
        rbSortingAsc.buttonTintList = disableColorState
        rbSortingDesc.buttonTintList = disableColorState
        rbSortingAsc.setTextColor(disabledColor)
        rbSortingDesc.setTextColor(disabledColor)
        tvSortingOrder.setTextColor(disabledColor)
    }

    private fun LibraryListSortingDialogFragmentBinding.enableSortingOrder() {
        val enabledColor = resources.colorResource(R.color.colorPrimary)
        val enabledColorState = resources.colorStateResource(R.color.colorPrimary)
        rbSortingAsc.isEnabled = true
        rbSortingDesc.isEnabled = true
        rbSortingAsc.buttonTintList = enabledColorState
        rbSortingDesc.buttonTintList = enabledColorState
        rbSortingAsc.setTextColor(enabledColor)
        rbSortingDesc.setTextColor(enabledColor)
        tvSortingOrder.setTextColor(enabledColor)
    }
}