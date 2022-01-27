package com.noto.app.label

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
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
        val baseDialogFragment = setupBaseDialogFragment()
        setupState(baseDialogFragment)
        setupListeners()
    }

    private fun LabelDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)
        .apply {
            context?.let { context ->
                tvDialogTitle.text = context.stringResource(R.string.label_options)
            }
        }

    private fun LabelDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        viewModel.folder
            .onEach { folder ->
                context?.let { context ->
                    val color = context.colorResource(folder.color.toResource())
                    val colorStateList = color.toColorStateList()
                    baseDialogFragment.vHead.background?.mutate()?.setTint(color)
                    baseDialogFragment.tvDialogTitle.setTextColor(color)
                    divider1.root.background?.mutate()?.setTint(color.withDefaultAlpha())
                    listOf(tvEditLabel, tvReorderLabel, tvDeleteLabel)
                        .forEach { tv ->
                            TextViewCompat.setCompoundDrawableTintList(tv, color.toColorStateList())
                            tv.background.setRippleColor(colorStateList)
                        }
                }
            }
            .launchIn(lifecycleScope)

    }

    private fun LabelDialogFragmentBinding.setupListeners() {
        tvEditLabel.setOnClickListener {
            context?.updateAllWidgetsData()
            dismiss()
            navController
                ?.navigateSafely(LabelDialogFragmentDirections.actionLabelDialogFragmentToNewLabelDialogFragment(args.folderId, args.labelId))
        }

        tvReorderLabel.setOnClickListener {
            context?.updateAllWidgetsData()
            dismiss()
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
                        parentView?.snackbar(context.stringResource(R.string.label_is_deleted), viewModel.folder.value)
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