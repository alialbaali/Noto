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
import com.noto.app.databinding.NoteListSortingTypeDialogFragmentBinding
import com.noto.app.domain.model.NoteListSortingType
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListSortingTypeDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteListSortingTypeDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteListSortingTypeDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        val baseDialog = BaseDialogFragmentBinding.bind(root)
            .apply {
                context?.let { context ->
                    tvDialogTitle.text = context.stringResource(R.string.sorting_type)
                }
            }

        viewModel.library
            .onEach { library ->
                context?.let { context ->
                    val color = context.colorResource(library.color.toResource())
                    val colorStateList = context.colorStateResource(library.color.toResource())
                    baseDialog.tvDialogTitle.setTextColor(color)
                    baseDialog.vHead.background?.mutate()?.setTint(color)
                    rbManual.buttonTintList = colorStateList
                    rbAlphabetical.buttonTintList = colorStateList
                    rbCreationDate.buttonTintList = colorStateList
                }
                when (library.sortingType) {
                    NoteListSortingType.Alphabetical -> rbAlphabetical.isChecked = true
                    NoteListSortingType.CreationDate -> rbCreationDate.isChecked = true
                    NoteListSortingType.Manual -> rbManual.isChecked = true
                }
            }
            .launchIn(lifecycleScope)

        rbManual.setOnClickListener {
            dismiss()
            viewModel.updateSortingType(NoteListSortingType.Manual)
        }

        rbCreationDate.setOnClickListener {
            dismiss()
            viewModel.updateSortingType(NoteListSortingType.CreationDate)
        }

        rbAlphabetical.setOnClickListener {
            dismiss()
            viewModel.updateSortingType(NoteListSortingType.Alphabetical)
        }
    }
}