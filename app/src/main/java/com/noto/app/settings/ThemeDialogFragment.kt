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
import com.noto.app.databinding.ThemeDialogFragmentBinding
import com.noto.app.domain.model.Theme
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class ThemeDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<AppViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ThemeDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupState()
        setupListeners()
    }

    private fun ThemeDialogFragmentBinding.setupState() {
        viewModel.theme
            .onEach { theme -> setupTheme(theme) }
            .launchIn(lifecycleScope)
    }

    private fun ThemeDialogFragmentBinding.setupListeners() {
        rbSystemTheme.setOnClickListener {
            dismiss()
            viewModel.updateTheme(Theme.System)
        }

        rbLightTheme.setOnClickListener {
            dismiss()
            viewModel.updateTheme(Theme.Light)
        }

        rbDarkTheme.setOnClickListener {
            dismiss()
            viewModel.updateTheme(Theme.Dark)
        }
    }

    private fun ThemeDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            tvDialogTitle.text = context.stringResource(R.string.theme)
        }
    }

    private fun ThemeDialogFragmentBinding.setupTheme(theme: Theme) {
        when (theme) {
            Theme.System -> rbSystemTheme.isChecked = true
            Theme.Light -> rbLightTheme.isChecked = true
            Theme.Dark -> rbDarkTheme.isChecked = true
        }
    }
}