package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.SortingDialogFragmentBinding
import com.noto.app.domain.model.SortingMethod
import com.noto.app.domain.model.SortingType
import com.noto.app.library.LibraryViewModel
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val RIGHT_DRAWABLE_INDEX = 2

class SortingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<SortingDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = SortingDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        val baseDialog = BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = resources.stringResource(R.string.library_sorting)
        }

        viewModel.library
            .onEach {
                val resourceColor = resources.colorResource(it.color.toResource())
                val resourceColorState = resources.colorStateResource(it.color.toResource())

                baseDialog.tvDialogTitle.setTextColor(resourceColor)
                baseDialog.vHead.backgroundTintList = resourceColorState

                rbSortingAsc.buttonTintList = resourceColorState
                rbSortingDesc.buttonTintList = resourceColorState
                rbAlphabetically.buttonTintList = resourceColorState
                rbCreationDate.buttonTintList = resourceColorState

                when (it.sortingMethod) {
                    SortingMethod.Asc -> rbSortingAsc.isChecked = true
                    SortingMethod.Desc -> rbSortingDesc.isChecked = true
                }

                when (it.sortingType) {
                    SortingType.Alphabetically -> rbAlphabetically.isChecked = true
                    SortingType.CreationDate -> rbCreationDate.isChecked = true
                }
            }
            .launchIn(lifecycleScope)

        rbSortingAsc.setOnClickListener { viewModel.updateSortingMethod(SortingMethod.Asc) }
        rbSortingDesc.setOnClickListener { viewModel.updateSortingMethod(SortingMethod.Desc) }
        rbAlphabetically.setOnClickListener { viewModel.updateSortingType(SortingType.Alphabetically) }
        rbCreationDate.setOnClickListener { viewModel.updateSortingType(SortingType.CreationDate) }

        btnConfirm.setOnClickListener {
            dismiss()
        }
    }
}