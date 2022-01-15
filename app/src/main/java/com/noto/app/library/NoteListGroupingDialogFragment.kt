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
import com.noto.app.databinding.NoteListGroupingDialogFragmentBinding
import com.noto.app.domain.model.Grouping
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListGroupingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LibraryViewModel> { parametersOf(args.libraryId) }

    private val args by navArgs<NoteListGroupingDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteListGroupingDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        val baseDialog = BaseDialogFragmentBinding.bind(root)
            .apply {
                context?.let { context ->
                    tvDialogTitle.text = context.stringResource(R.string.grouping)
                }
            }

        viewModel.library
            .onEach { library ->
                context?.let { context ->
                    val color = context.colorResource(library.color.toResource())
                    val colorStateList = color.toColorStateList()
                    val backgroundColorStateList = context.attributeColoResource(R.attr.notoBackgroundColor).toColorStateList()
                    baseDialog.tvDialogTitle.setTextColor(color)
                    baseDialog.vHead.background?.mutate()?.setTint(color)
                    when (library.grouping) {
                        Grouping.Default -> {
                            rbDefault.isChecked = true
                            rbDefault.backgroundTintList = colorStateList.withAlpha(32)
                            rbCreationDate.backgroundTintList = backgroundColorStateList
                            rbLabel.backgroundTintList = backgroundColorStateList
                        }
                        Grouping.CreationDate -> {
                            rbCreationDate.isChecked = true
                            rbCreationDate.backgroundTintList = colorStateList.withAlpha(32)
                            rbDefault.backgroundTintList = backgroundColorStateList
                            rbLabel.backgroundTintList = backgroundColorStateList
                        }
                        Grouping.Label -> {
                            rbLabel.isChecked = true
                            rbLabel.backgroundTintList = colorStateList.withAlpha(32)
                            rbCreationDate.backgroundTintList = backgroundColorStateList
                            rbDefault.backgroundTintList = backgroundColorStateList
                        }
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