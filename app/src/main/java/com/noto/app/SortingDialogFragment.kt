package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.SortingDialogFragmentBinding
import com.noto.app.library.LibraryViewModel
import com.noto.app.util.*
import com.noto.domain.model.SortingMethod
import com.noto.domain.model.SortingType
import org.koin.android.viewmodel.ext.android.sharedViewModel

private const val RIGHT_DRAWABLE_INDEX = 2

class SortingDialogFragment : BaseDialogFragment() {

    val viewModel by sharedViewModel<LibraryViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = SortingDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        val baseDialog = BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = stringResource(R.string.library_sorting)
        }

        val showAnim = AlphaAnimation(0F, 1F).apply {
            duration = 500
        }

        viewModel.library.observe(viewLifecycleOwner) { library ->
            library?.let {

                val resourceColor = colorResource(it.color.toResource())
                val resourceColorState = colorStateResource(it.color.toResource())

                baseDialog.tvDialogTitle.setTextColor(resourceColor)
                baseDialog.vHead.backgroundTintList = colorStateResource(it.color.toResource())
                listOf(tvAlphabetically, tvCreationDate)
                    .onEach {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            it.compoundDrawableTintList = resourceColorState
                        }
                    }

                val selectedElevation = dimenResource(R.dimen.elevation_normal)

                when (library.sortingMethod) {
                    SortingMethod.Asc -> {
                        rbSortingAsc.isChecked = true
                        rbSortingAsc.elevation = selectedElevation
                        rbSortingAsc.backgroundTintList = resourceColorState

                        rbSortingDesc.backgroundTintList = colorStateResource(R.color.colorSurface)
                        rbSortingDesc.elevation = 0F
                    }
                    SortingMethod.Desc -> {
                        rbSortingDesc.isChecked = true
                        rbSortingDesc.elevation = selectedElevation
                        rbSortingDesc.backgroundTintList = resourceColorState

                        rbSortingAsc.backgroundTintList = colorStateResource(R.color.colorSurface)
                        rbSortingAsc.elevation = 0F
                    }
                }

                when (library.sortingType) {
                    SortingType.Alphabetically -> tvAlphabetically.compoundDrawables[RIGHT_DRAWABLE_INDEX] = drawableResource(R.drawable.ic_sort_checked)
                    SortingType.CreationDate -> tvCreationDate.compoundDrawables[RIGHT_DRAWABLE_INDEX] = drawableResource(R.drawable.ic_sort_checked)
                }

            }
        }

        rgSortingMethod.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                rbSortingDesc.id -> rbSortingDesc.startAnimation(showAnim)
                rbSortingAsc.id -> rbSortingAsc.startAnimation(showAnim)
            }
        }

        rbSortingAsc.setOnClickListener { viewModel.setSortingMethod(SortingMethod.Asc) }
        rbSortingDesc.setOnClickListener { viewModel.setSortingMethod(SortingMethod.Desc) }

        tvAlphabetically.setOnClickListener { viewModel.setSortingType(SortingType.Alphabetically) }
        tvCreationDate.setOnClickListener { viewModel.setSortingType(SortingType.CreationDate) }

        btnConfirm.setOnClickListener {
            dismiss()
            viewModel.updateLibrary()
        }

    }

}