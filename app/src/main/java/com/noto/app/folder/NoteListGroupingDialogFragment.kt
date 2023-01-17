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
import com.noto.app.util.colorResource
import com.noto.app.util.stringResource
import com.noto.app.util.toResource
import com.noto.app.util.withBinding
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
                    when (folder.grouping) {
                        Grouping.None -> rbNone.isChecked = true
                        Grouping.CreationDate -> rbCreationDate.isChecked = true
                        Grouping.Label -> rbLabel.isChecked = true
                        Grouping.AccessDate -> rbAccessDate.isChecked = true
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbNone.setOnClickListener {
            viewModel.updateGroupingType(Grouping.None)
                .invokeOnCompletion { dismiss() }
        }

        rbCreationDate.setOnClickListener {
            viewModel.updateGroupingType(Grouping.CreationDate)
                .invokeOnCompletion { dismiss() }
        }

        rbLabel.setOnClickListener {
            viewModel.updateGroupingType(Grouping.Label)
                .invokeOnCompletion { dismiss() }
        }

        rbAccessDate.setOnClickListener {
            viewModel.updateGroupingType(Grouping.AccessDate)
                .invokeOnCompletion { dismiss() }
        }
    }
}