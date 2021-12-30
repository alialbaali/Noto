package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.VaultTimeoutDialogFragmentBinding
import com.noto.app.domain.model.VaultTimeout
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class VaultTimeoutDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = VaultTimeoutDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupState()
        setupListeners()
    }

    private fun VaultTimeoutDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            tvDialogTitle.text = context.stringResource(R.string.vault_timeout)
        }
    }

    private fun VaultTimeoutDialogFragmentBinding.setupState() {
        viewModel.vaultTimeout
            .onEach { timeout ->
                when (timeout) {
                    VaultTimeout.Immediately -> rbImmediately.isChecked = true
                    VaultTimeout.OnAppClose -> rbOnAppClose.isChecked = true
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun VaultTimeoutDialogFragmentBinding.setupListeners() {
        rbImmediately.setOnClickListener {
            viewModel.setVaultTimeout(VaultTimeout.Immediately)
            dismiss()
        }

        rbOnAppClose.setOnClickListener {
            viewModel.setVaultTimeout(VaultTimeout.OnAppClose)
            dismiss()
        }
    }
}
