package com.noto.app.label

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.NewLabelDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NewLabelDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<LabelViewModel> { parametersOf(args.libraryId, args.labelId) }

    private val args by navArgs<NewLabelDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = NewLabelDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        val baseDialogFragment = setupBaseDialogFragment()
        setupState(baseDialogFragment)
        setupListeners()
    }

    private fun NewLabelDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        if (args.labelId == 0L) {
            tvDialogTitle.text = resources.stringResource(R.string.new_label)
        } else {
            tvDialogTitle.text = resources.stringResource(R.string.edit_label)
            btnCreate.text = resources.stringResource(R.string.done)
        }
    }

    private fun NewLabelDialogFragmentBinding.setupState(baseDialogFragment: BaseDialogFragmentBinding) {
        et.requestFocus()
        activity?.showKeyboard(root)

        viewModel.library
            .onEach { library ->
                val color = resources.colorResource(library.color.toResource())
                baseDialogFragment.tvDialogTitle.setTextColor(color)
                baseDialogFragment.vHead.background?.mutate()?.setTint(color)
            }
            .launchIn(lifecycleScope)

        viewModel.label
            .onEach { label ->
                et.setText(label.title)
                et.setSelection(label.title.length)
            }
            .launchIn(lifecycleScope)
    }

    private fun NewLabelDialogFragmentBinding.setupListeners() {
        btnCreate.setOnClickListener {
            val title = et.text.toString()
            if (title.isBlank()) {
                til.isErrorEnabled = true
                til.error = resources.stringResource(R.string.empty_title)
            } else {
                activity?.hideKeyboard(root)
                dismiss()
                viewModel.createOrUpdateLabel(title)
            }
        }
    }
}