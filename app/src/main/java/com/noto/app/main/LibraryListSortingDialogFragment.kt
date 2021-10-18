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
import com.noto.app.domain.model.LibraryListSortingType
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

        viewModel.sortingType
            .onEach { sortingType ->
                when (sortingType) {
                    LibraryListSortingType.Alphabetical -> {
                        rbAlphabetical.isChecked = true
                        enableSortingOrder()
                    }
                    LibraryListSortingType.CreationDate -> {
                        rbCreationDate.isChecked = true
                        enableSortingOrder()
                    }
                    LibraryListSortingType.Manual -> {
                        rbManual.isChecked = true
                        disableSortingOrder()
                    }
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

        rbSortingAsc.setOnClickListener { viewModel.updateSortingOrder(SortingOrder.Ascending) }
        rbSortingDesc.setOnClickListener { viewModel.updateSortingOrder(SortingOrder.Descending) }
        rbManual.setOnClickListener { viewModel.updateSorting(LibraryListSortingType.Manual) }
        rbCreationDate.setOnClickListener { viewModel.updateSorting(LibraryListSortingType.CreationDate) }
        rbAlphabetical.setOnClickListener { viewModel.updateSorting(LibraryListSortingType.Alphabetical) }

        btnDone.setOnClickListener {
            dismiss()
        }
    }

    private fun LibraryListSortingDialogFragmentBinding.disableSortingOrder() {
        val disabledColor = resources.colorResource(R.color.colorSecondary)
        val disableColorState = resources.colorStateResource(R.color.colorSecondary)
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