package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.SortingDialogFragmentBinding
import com.noto.app.domain.model.SortingMethod
import com.noto.app.domain.model.SortingType
import com.noto.app.notelist.NoteListViewModel
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

private const val RIGHT_DRAWABLE_INDEX = 2

class SortingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<NoteListViewModel> { parametersOf(args.libraryId) }

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
                baseDialog.vHead.backgroundTintList = resources.colorStateResource(it.color.toResource())

                listOf(tvAlphabetically, tvCreationDate)
                    .forEach { TextViewCompat.setCompoundDrawableTintList(it, resourceColorState) }

                val selectedElevation = resources.dimenResource(R.dimen.elevation_normal)

                when (it.sortingMethod) {
                    SortingMethod.Asc -> {
                        rbSortingAsc.isChecked = true
                        rbSortingAsc.elevation = selectedElevation
                        rbSortingAsc.backgroundTintList = resourceColorState
                        rbSortingDesc.backgroundTintList = resources.colorStateResource(R.color.colorSurface)
                        rbSortingDesc.elevation = 0F
                    }
                    SortingMethod.Desc -> {
                        rbSortingDesc.isChecked = true
                        rbSortingDesc.elevation = selectedElevation
                        rbSortingDesc.backgroundTintList = resourceColorState
                        rbSortingAsc.backgroundTintList = resources.colorStateResource(R.color.colorSurface)
                        rbSortingAsc.elevation = 0F
                    }
                }

                when (it.sortingType) {
                    SortingType.Alphabetically -> {
                        tvAlphabetically.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_check_circle_24, 0, 0, 0)
                        tvCreationDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_circle_24, 0, 0, 0)
                    }
                    SortingType.CreationDate -> {
                        tvCreationDate.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_check_circle_24, 0, 0, 0)
                        tvAlphabetically.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_round_circle_24, 0, 0, 0)
                    }

                }
            }
            .launchIn(lifecycleScope)


        val showAnim = AlphaAnimation(0F, 1F).apply {
            duration = 250
        }

        rgSortingMethod.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                rbSortingDesc.id -> rbSortingDesc.startAnimation(showAnim)
                rbSortingAsc.id -> rbSortingAsc.startAnimation(showAnim)
            }
        }

        rbSortingAsc.setOnClickListener { viewModel.updateSortingMethod(SortingMethod.Asc) }
        rbSortingDesc.setOnClickListener { viewModel.updateSortingMethod(SortingMethod.Desc) }
        tvAlphabetically.setOnClickListener { viewModel.updateSortingType(SortingType.Alphabetically) }
        tvCreationDate.setOnClickListener { viewModel.updateSortingType(SortingType.CreationDate) }

        btnConfirm.setOnClickListener {
            dismiss()
        }
    }
}