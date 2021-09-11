package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteListSortingDialogFragmentBinding
import com.noto.app.domain.model.NoteListSorting
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.colorResource
import com.noto.app.util.colorStateResource
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListSortingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteListSortingDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteListSortingDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = resources.stringResource(R.string.notes_sorting)
        }

        viewModel.state
            .onEach { state ->
                when (state.library.sortingOrder) {
                    SortingOrder.Ascending -> rbSortingAsc.isChecked = true
                    SortingOrder.Descending -> rbSortingDesc.isChecked = true
                }
                when (state.library.sorting) {
                    NoteListSorting.Alphabetical -> {
                        rbAlphabetical.isChecked = true
                        enableSortingOrder()
                    }
                    NoteListSorting.CreationDate -> {
                        rbCreationDate.isChecked = true
                        enableSortingOrder()
                    }
                    NoteListSorting.Manual -> {
                        rbManual.isChecked = true
                        disableSortingOrder()
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbSortingAsc.setOnClickListener { viewModel.updateSortingOrder(SortingOrder.Ascending) }
        rbSortingDesc.setOnClickListener { viewModel.updateSortingOrder(SortingOrder.Descending) }
        rbManual.setOnClickListener { viewModel.updateSorting(NoteListSorting.Manual) }
        rbCreationDate.setOnClickListener { viewModel.updateSorting(NoteListSorting.CreationDate) }
        rbAlphabetical.setOnClickListener { viewModel.updateSorting(NoteListSorting.Alphabetical) }

        btnDone.setOnClickListener {
            dismiss()
        }
    }

    private fun NoteListSortingDialogFragmentBinding.disableSortingOrder() {
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

    private fun NoteListSortingDialogFragmentBinding.enableSortingOrder() {
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