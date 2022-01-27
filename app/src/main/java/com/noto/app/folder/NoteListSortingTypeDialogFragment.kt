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
import com.noto.app.databinding.NoteListSortingTypeDialogFragmentBinding
import com.noto.app.domain.model.NoteListSortingType
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListSortingTypeDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListSortingTypeDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = NoteListSortingTypeDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        val baseDialog = BaseDialogFragmentBinding.bind(root)
            .apply {
                context?.let { context ->
                    tvDialogTitle.text = context.stringResource(R.string.sorting_type)
                }
            }

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    val colorStateList = color.toColorStateList()
                    val backgroundColorStateList = context.attributeColoResource(R.attr.notoBackgroundColor).toColorStateList()
                    baseDialog.tvDialogTitle.setTextColor(color)
                    baseDialog.vHead.background?.mutate()?.setTint(color)
                    when (folder.sortingType) {
                        NoteListSortingType.Alphabetical -> {
                            rbAlphabetical.isChecked = true
                            rbAlphabetical.backgroundTintList = colorStateList.withAlpha(32)
                            rbCreationDate.backgroundTintList = backgroundColorStateList
                            rbManual.backgroundTintList = backgroundColorStateList
                        }
                        NoteListSortingType.CreationDate -> {
                            rbCreationDate.isChecked = true
                            rbCreationDate.backgroundTintList = colorStateList.withAlpha(32)
                            rbAlphabetical.backgroundTintList = backgroundColorStateList
                            rbManual.backgroundTintList = backgroundColorStateList
                        }
                        NoteListSortingType.Manual -> {
                            rbManual.isChecked = true
                            rbManual.backgroundTintList = colorStateList.withAlpha(32)
                            rbCreationDate.backgroundTintList = backgroundColorStateList
                            rbAlphabetical.backgroundTintList = backgroundColorStateList
                        }
                    }
                }
            }
            .launchIn(lifecycleScope)

        rbManual.setOnClickListener {
            viewModel.updateSortingType(NoteListSortingType.Manual).invokeOnCompletion { dismiss() }
        }

        rbCreationDate.setOnClickListener {
            viewModel.updateSortingType(NoteListSortingType.CreationDate).invokeOnCompletion { dismiss() }
        }

        rbAlphabetical.setOnClickListener {
            viewModel.updateSortingType(NoteListSortingType.Alphabetical).invokeOnCompletion { dismiss() }
        }
    }
}