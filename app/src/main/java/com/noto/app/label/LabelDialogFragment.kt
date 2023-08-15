package com.noto.app.label

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.LabelDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LabelDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LabelViewModel> { parametersOf(args.folderId, args.labelId) }

    private val args by navArgs<LabelDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = LabelDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
        setupListeners()
    }

    private fun LabelDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.label_options)

        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toColorResourceId())
                    val colorStateList = color.toColorStateList()
                    tb.vHead.background?.mutate()?.setTint(color)
                    tb.tvDialogTitle.setTextColor(color)
                    listOf(tvEditLabel, tvReorderLabel, tvDeleteLabel)
                        .forEach { tv ->
                            tv.background.setRippleColor(colorStateList)
                        }
                }
            }
            .launchIn(lifecycleScope)

        viewModel.label
            .onEach { label ->
                vLabel.tvLabel.text = label.title
                vLabel.tvLabel.backgroundTintList = context?.colorAttributeResource(R.attr.notoBackgroundColor)?.toColorStateList()
            }
            .launchIn(lifecycleScope)
    }

    private fun LabelDialogFragmentBinding.setupListeners() {
        tvEditLabel.setOnClickListener {
            context?.updateAllWidgetsData()
            navController
                ?.navigateSafely(LabelDialogFragmentDirections.actionLabelDialogFragmentToNewLabelDialogFragment(args.folderId, args.labelId))
        }

        tvReorderLabel.setOnClickListener {
            context?.updateAllWidgetsData()
            navController
                ?.navigateSafely(LabelDialogFragmentDirections.actionLabelDialogFragmentToReorderLabelDialogFragment(args.folderId, args.labelId))
        }

        tvDeleteLabel.setOnClickListener {
            context?.let { context ->
                context.updateAllWidgetsData()
                val confirmationText = context.stringResource(R.string.delete_label_confirmation)
                val descriptionText = context.stringResource(R.string.delete_label_description)
                val btnText = context.stringResource(R.string.delete_label)
                navController?.currentBackStackEntry
                    ?.savedStateHandle
                    ?.getLiveData<Int>(Constants.ClickListener)
                    ?.observe(viewLifecycleOwner) {
                        val parentView = parentFragment?.view
                        val stringId = R.string.label_is_deleted
                        val drawableId = R.drawable.ic_round_delete_24
                        val anchorViewId = R.id.bab
                        val folderColor = viewModel.folder.value.color
                        parentView?.snackbar(context.stringResource(stringId), drawableId, anchorViewId, folderColor)
                        viewModel.deleteLabel().invokeOnCompletion { dismiss() }
                    }
                navController?.navigateSafely(
                    LabelDialogFragmentDirections.actionLabelDialogFragmentToConfirmationDialogFragment(
                        confirmationText,
                        descriptionText,
                        btnText,
                    )
                )
            }
        }
    }
}