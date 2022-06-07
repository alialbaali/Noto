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
import com.noto.app.databinding.NoteListGroupingDialogFragmentBinding
import com.noto.app.domain.model.Grouping
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

        val baseDialog = BaseDialogFragmentBinding.bind(root)
            .apply {
                context?.let { context ->
                    tvDialogTitle.text = context.stringResource(R.string.grouping_type)
                }
            }

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    baseDialog.tvDialogTitle.setTextColor(color)
                    baseDialog.vHead.background?.mutate()?.setTint(color)
                    rbDefault.background = context.createDialogItemStateListDrawable(folder.color)
                    rbCreationDate.background = context.createDialogItemStateListDrawable(folder.color)
                    rbLabel.background = context.createDialogItemStateListDrawable(folder.color)
                    when (folder.grouping) {
                        Grouping.Default -> rbDefault.isChecked = true
                        Grouping.CreationDate -> rbCreationDate.isChecked = true
                        Grouping.Label -> rbLabel.isChecked = true
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbDefault.setOnClickListener {
            viewModel.updateGrouping(Grouping.Default).invokeOnCompletion { dismiss() }
        }

        rbCreationDate.setOnClickListener {
            viewModel.updateGrouping(Grouping.CreationDate).invokeOnCompletion { dismiss() }
        }

        rbLabel.setOnClickListener {
            viewModel.updateGrouping(Grouping.Label).invokeOnCompletion { dismiss() }
        }
    }
}