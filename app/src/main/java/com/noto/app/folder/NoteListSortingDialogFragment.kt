package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteListSortingDialogFragmentBinding
import com.noto.app.domain.model.NoteListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListSortingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListSortingDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteListSortingDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.sorting)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    tb.tvDialogTitle.setTextColor(color)
                    tb.vHead.background?.mutate()?.setTint(color)
                    rbAlphabetical.background = context.createDialogItemStateListDrawable(folder.color)
                    rbCreationDate.background = context.createDialogItemStateListDrawable(folder.color)
                    rbManual.background = context.createDialogItemStateListDrawable(folder.color)
                    rbSortingAsc.background = context.createDialogItemStateListDrawable(folder.color)
                    rbSortingDesc.background = context.createDialogItemStateListDrawable(folder.color)
                    when (folder.sortingType) {
                        NoteListSortingType.Alphabetical -> rbAlphabetical.isChecked = true
                        NoteListSortingType.CreationDate -> rbCreationDate.isChecked = true
                        NoteListSortingType.Manual -> rbManual.isChecked = true
                        NoteListSortingType.AccessDate -> rbAccessDate.isChecked = true
                    }
                    if (folder.sortingType == NoteListSortingType.Manual) {
                        rbSortingAsc.isClickable = false
                        rbSortingDesc.isClickable = false
                        rgOrder.disable()
                        tvOrder.disable()
                    } else {
                        rbSortingAsc.isClickable = true
                        rbSortingDesc.isClickable = true
                        rgOrder.enable()
                        tvOrder.enable()
                    }
                    when (folder.sortingOrder) {
                        SortingOrder.Ascending -> rbSortingAsc.isChecked = true
                        SortingOrder.Descending -> rbSortingDesc.isChecked = true
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbManual.setOnClickListener {
            viewModel.updateSortingType(NoteListSortingType.Manual)
        }

        rbCreationDate.setOnClickListener {
            viewModel.updateSortingType(NoteListSortingType.CreationDate)
        }

        rbAlphabetical.setOnClickListener {
            viewModel.updateSortingType(NoteListSortingType.Alphabetical)
        }

        rbAccessDate.setOnClickListener {
            viewModel.updateSortingType(NoteListSortingType.AccessDate)
        }

        rbSortingAsc.setOnClickListener {
            viewModel.updateSortingOrder(SortingOrder.Ascending)
        }

        rbSortingDesc.setOnClickListener {
            viewModel.updateSortingOrder(SortingOrder.Descending)
        }

        btnApply.setOnClickListener {
            dismiss()
        }
    }
}