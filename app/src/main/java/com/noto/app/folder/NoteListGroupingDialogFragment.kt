package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteListGroupingDialogFragmentBinding
import com.noto.app.domain.model.Grouping
import com.noto.app.domain.model.GroupingOrder
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListGroupingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListGroupingDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteListGroupingDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.grouping)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    tb.tvDialogTitle.setTextColor(color)
                    tb.vHead.background?.mutate()?.setTint(color)
                    rbNone.background = context.createDialogItemStateListDrawable(folder.color)
                    rbCreationDate.background = context.createDialogItemStateListDrawable(folder.color)
                    rbLabel.background = context.createDialogItemStateListDrawable(folder.color)
                    rbGroupingAsc.background = context.createDialogItemStateListDrawable(folder.color)
                    rbGroupingDesc.background = context.createDialogItemStateListDrawable(folder.color)
                    when (folder.grouping) {
                        Grouping.None -> rbNone.isChecked = true
                        Grouping.CreationDate -> rbCreationDate.isChecked = true
                        Grouping.Label -> rbLabel.isChecked = true
                        Grouping.AccessDate -> rbAccessDate.isChecked = true
                    }
                    if (folder.grouping == Grouping.None) {
                        rbGroupingAsc.isClickable = false
                        rbGroupingDesc.isClickable = false
                        rgOrder.disable()
                        tvOrder.disable()
                    } else {
                        rbGroupingAsc.isClickable = true
                        rbGroupingDesc.isClickable = true
                        rgOrder.enable()
                        tvOrder.enable()
                    }
                    when (folder.groupingOrder) {
                        GroupingOrder.Ascending -> rbGroupingAsc.isChecked = true
                        GroupingOrder.Descending -> rbGroupingDesc.isChecked = true
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbNone.setOnClickListener {
            viewModel.updateGrouping(Grouping.None)
        }

        rbCreationDate.setOnClickListener {
            viewModel.updateGrouping(Grouping.CreationDate)
        }

        rbLabel.setOnClickListener {
            viewModel.updateGrouping(Grouping.Label)
        }

        rbAccessDate.setOnClickListener {
            viewModel.updateGrouping(Grouping.AccessDate)
        }

        rbGroupingAsc.setOnClickListener {
            viewModel.updateGroupingOrder(GroupingOrder.Ascending)
        }

        rbGroupingDesc.setOnClickListener {
            viewModel.updateGroupingOrder(GroupingOrder.Descending)
        }

        btnApply.setOnClickListener {
            dismiss()
        }
    }
}