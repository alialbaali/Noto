package com.noto.app.settings.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.components.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.FontDialogFragmentBinding
import com.noto.app.domain.model.Font
import com.noto.app.settings.SettingsViewModel
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class FontDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        FontDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun FontDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.notes_font)

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
            viewModel.updateFont(Font.Nunito)
            dismiss()
        }

        rbMonospace.setOnClickListener {
            viewModel.updateFont(Font.Monospace)
            dismiss()
        }
    }
}