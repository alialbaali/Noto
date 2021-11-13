package com.noto.app.label

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.noto.app.BaseDialogFragment
import com.noto.app.ConfirmationDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LabelDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class LabelDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LabelViewModel> { parametersOf(args.libraryId, args.labelId) }

    private val args by navArgs<LabelDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
        viewModel.library
            .onEach { library ->
                context?.let { context ->
                    val color = context.colorResource(library.color.toResource())
                    val colorState = context.colorStateResource(library.color.toResource())
                    baseDialogFragment.vHead.background?.mutate()?.setTint(color)
                    baseDialogFragment.tvDialogTitle.setTextColor(color)
                    listOf(tvEditLabel, tvReorderLabel, tvDeleteLabel)
                        .forEach { TextViewCompat.setCompoundDrawableTintList(it, colorState) }
                }
            }
            .launchIn(lifecycleScope)

    }

    private fun LabelDialogFragmentBinding.setupListeners() {
        tvEditLabel.setOnClickListener {
            dismiss()
            navController
                ?.navigateSafely(LabelDialogFragmentDirections.actionLabelDialogFragmentToNewLabelDialogFragment(args.libraryId, args.labelId))
        }

        tvReorderLabel.setOnClickListener {
            dismiss()
            navController
                ?.navigateSafely(LabelDialogFragmentDirections.actionLabelDialogFragmentToReorderLabelDialogFragment(args.libraryId, args.labelId))
        }

        tvDeleteLabel.setOnClickListener {
            context?.let { context ->
                val confirmationText = context.stringResource(R.string.delete_label_confirmation)
                val descriptionText = context.stringResource(R.string.delete_label_description)
                val btnText = context.stringResource(R.string.delete_label)
                val clickListener = ConfirmationDialogFragment.ConfirmationDialogClickListener {
                    val parentView = parentFragment?.view
                    val parentAnchorView = parentView?.findViewById<FloatingActionButton>(R.id.fab)
                    parentView?.snackbar(context.stringResource(R.string.label_is_deleted), anchorView = parentAnchorView)
                    viewModel.deleteLabel().invokeOnCompletion { dismiss() }
                }

                navController?.navigateSafely(
                    LabelDialogFragmentDirections.actionLabelDialogFragmentToConfirmationDialogFragment(
                        confirmationText,
                        descriptionText,
                        btnText,
                        clickListener,
                    )
                )
            }
        }
    }
}