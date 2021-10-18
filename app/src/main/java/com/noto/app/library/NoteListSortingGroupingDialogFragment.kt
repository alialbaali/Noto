package com.noto.app.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteListSortingGroupingDialogFragmentBinding
import com.noto.app.domain.model.NoteListSortingType
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListSortingGroupingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteListSortingGroupingDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NoteListSortingGroupingDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        val baseDialog = BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = resources.stringResource(R.string.sorting_and_grouping)
        }

        viewModel.library
            .onEach { library ->
                val color = resources.colorResource(library.color.toResource())
                baseDialog.tvDialogTitle.setTextColor(color)
                baseDialog.vHead.background?.mutate()?.setTint(color)
                val colorStateList = resources.colorStateResource(library.color.toResource())
                listOf(tvGrouping, tvSorting, tvSortingOrder).onEach {
                    TextViewCompat.setCompoundDrawableTintList(it, colorStateList)
                }
                when (library.sortingType) {
                    NoteListSortingType.Manual -> tvSortingOrder.isVisible = false
                    else -> tvSortingOrder.isVisible = true
                }
            }
            .launchIn(lifecycleScope)

        tvGrouping.setOnClickListener {
            findNavController().navigateSafely(
                NoteListSortingGroupingDialogFragmentDirections.actionNoteListSortingGroupingDialogFragmentToNoteListGroupingDialogFragment(
                    args.libraryId
                )
            )
        }

        tvSorting.setOnClickListener {
            findNavController().navigateSafely(
                NoteListSortingGroupingDialogFragmentDirections.actionNoteListSortingGroupingDialogFragmentToNoteListSortingDialogFragment(
                    args.libraryId
                )
            )
        }

        tvSortingOrder.setOnClickListener {
            findNavController().navigateSafely(
                NoteListSortingGroupingDialogFragmentDirections.actionNoteListSortingGroupingDialogFragmentToNoteListSortingOrderDialogFragment(
                    args.libraryId
                )
            )
        }

        btnDone.setOnClickListener {
            dismiss()
        }
    }
}
