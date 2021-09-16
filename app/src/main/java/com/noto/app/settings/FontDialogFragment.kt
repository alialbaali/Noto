package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.AppViewModel
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.FontDialogFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class FontDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<AppViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FontDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
            setupListeners()
        }

    private fun FontDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = resources.stringResource(R.string.font)
    }

    private fun FontDialogFragmentBinding.setupState() {
        viewModel.font
            .onEach { font ->
                when (font) {
                    Font.Nunito -> rbNunito.isChecked = true
                    Font.Monospace -> rbMonospace.isChecked = true
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun FontDialogFragmentBinding.setupListeners() {
        rbNunito.setOnClickListener {
            dismiss()
            viewModel.updateFont(Font.Nunito)
        }

        rbMonospace.setOnClickListener {
            dismiss()
            viewModel.updateFont(Font.Monospace)
        }
    }
}