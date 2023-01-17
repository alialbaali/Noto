package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.NoteListFilteringDialogFragmentBinding
import com.noto.app.domain.model.FilteringType
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListFilteringDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListFilteringDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteListFilteringDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        tb.tvDialogTitle.text = context?.stringResource(R.string.filtering)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    tb.tvDialogTitle.setTextColor(color)
                    tb.vHead.background?.mutate()?.setTint(color)
                    rbInclusive.background = context.createDialogItemStateListDrawable()
                    rbExclusive.background = context.createDialogItemStateListDrawable()
                    rbStrict.background = context.createDialogItemStateListDrawable()
                    when (folder.filteringType) {
                        FilteringType.Inclusive -> rbInclusive.isChecked = true
                        FilteringType.Exclusive -> rbExclusive.isChecked = true
                        FilteringType.Strict -> rbStrict.isChecked = true
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbInclusive.setOnClickListener {
            viewModel.updateFiltering(FilteringType.Inclusive)
                .invokeOnCompletion { dismiss() }
        }

        rbExclusive.setOnClickListener {
            viewModel.updateFiltering(FilteringType.Exclusive)
                .invokeOnCompletion { dismiss() }
        }

        rbStrict.setOnClickListener {
            viewModel.updateFiltering(FilteringType.Strict)
                .invokeOnCompletion { dismiss() }
        }
    }
}