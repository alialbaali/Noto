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
import com.noto.app.util.*
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

        val baseDialog = BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = resources.stringResource(R.string.sorting_type)
        }

        viewModel.library
            .onEach { library ->
                val color = resources.colorResource(library.color.toResource())
                val colorStateList = resources.colorStateResource(library.color.toResource())
                baseDialog.tvDialogTitle.setTextColor(color)
                baseDialog.vHead.background?.mutate()?.setTint(color)
                rbManual.buttonTintList = colorStateList
                rbAlphabetical.buttonTintList = colorStateList
                rbCreationDate.buttonTintList = colorStateList
                when (library.sorting) {
                    NoteListSorting.Alphabetical -> rbAlphabetical.isChecked = true
                    NoteListSorting.CreationDate -> rbCreationDate.isChecked = true
                    NoteListSorting.Manual -> rbManual.isChecked = true
                }
            }
            .launchIn(lifecycleScope)

        rbManual.setOnClickListener {
            dismiss()
            viewModel.updateSorting(NoteListSorting.Manual)
        }

        rbCreationDate.setOnClickListener {
            dismiss()
            viewModel.updateSorting(NoteListSorting.CreationDate)
        }

        rbAlphabetical.setOnClickListener {
            dismiss()
            viewModel.updateSorting(NoteListSorting.Alphabetical)
        }
    }
}