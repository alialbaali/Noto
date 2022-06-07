package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NoteListGroupingOrderDialogFragmentBinding
import com.noto.app.domain.model.GroupingOrder
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListGroupingOrderDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListGroupingOrderDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteListGroupingOrderDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        val baseDialog = BaseDialogFragmentBinding.bind(root).apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.grouping_order)
            }
        }

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    baseDialog.tvDialogTitle.setTextColor(color)
                    baseDialog.vHead.background?.mutate()?.setTint(color)
                    rbGroupingAsc.background = context.createDialogItemStateListDrawable(folder.color)
                    rbGroupingDesc.background = context.createDialogItemStateListDrawable(folder.color)
                    when (folder.groupingOrder) {
                        GroupingOrder.Ascending -> rbGroupingAsc.isChecked = true
                        GroupingOrder.Descending -> rbGroupingDesc.isChecked = true
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbGroupingAsc.setOnClickListener {
            viewModel.updateGroupingOrder(GroupingOrder.Ascending).invokeOnCompletion { dismiss() }
        }

        rbGroupingDesc.setOnClickListener {
            viewModel.updateGroupingOrder(GroupingOrder.Descending).invokeOnCompletion { dismiss() }
        }
    }
}