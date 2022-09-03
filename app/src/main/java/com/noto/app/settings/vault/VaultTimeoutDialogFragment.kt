package com.noto.app.settings.vault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.components.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.VaultTimeoutDialogFragmentBinding
import com.noto.app.domain.model.VaultTimeout
import com.noto.app.settings.SettingsViewModel
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
        setupState()
        setupListeners()
    }

    private fun VaultTimeoutDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.vault_timeout)

        viewModel.vaultTimeout
            .onEach { timeout ->
                when (timeout) {
                    VaultTimeout.Immediately -> rbImmediately.isChecked = true
                    VaultTimeout.OnAppClose -> rbOnAppClose.isChecked = true
                    VaultTimeout.After1Hour -> rbAfter1Hour.isChecked = true
                    VaultTimeout.After4Hours -> rbAfter4Hours.isChecked = true
                    VaultTimeout.After12Hours -> rbAfter12Hours.isChecked = true
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

        rbAfter1Hour.setOnClickListener {
            viewModel.setVaultTimeout(VaultTimeout.After1Hour)
            dismiss()
        }

        rbAfter4Hours.setOnClickListener {
            viewModel.setVaultTimeout(VaultTimeout.After4Hours)
            dismiss()
        }

        rbAfter12Hours.setOnClickListener {
            viewModel.setVaultTimeout(VaultTimeout.After12Hours)
            dismiss()
        }
    }
}
