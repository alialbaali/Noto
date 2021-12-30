package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.VaultPasscodeDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class VaultPasscodeDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = VaultPasscodeDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupState()
        setupListeners()
    }

    private fun VaultPasscodeDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            tvDialogTitle.text = context.stringResource(R.string.vault_passcode)
        }
    }

    private fun VaultPasscodeDialogFragmentBinding.setupState() {
        viewModel.vaultPasscode
            .onEach { passcode ->
                if (passcode == null) {
                    tvCurrentPasscode.isVisible = false
                    tilCurrentPasscode.isVisible = false
                    etNewPasscode.requestFocus()
                    activity?.showKeyboard(etNewPasscode)
                } else {
                    tvCurrentPasscode.isVisible = true
                    tilCurrentPasscode.isVisible = true
                    etCurrentPasscode.requestFocus()
                    activity?.showKeyboard(etCurrentPasscode)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun VaultPasscodeDialogFragmentBinding.setupListeners() {
        btnDone.setOnClickListener {
            val newPasscode = etNewPasscode.text.toString()
            when {
                newPasscode.isBlank() -> tilNewPasscode.error = context?.stringResource(R.string.passcode_empty_message)
                newPasscode.length < 6 -> tilNewPasscode.error = context?.stringResource(R.string.passcode_length_message)
                else -> {
                    tilNewPasscode.error = null
                    if (viewModel.vaultPasscode.value == null) {
                        setVaultPasscodeAndDismiss(newPasscode)
                    } else {
                        val currentPasscode = etCurrentPasscode.text.toString()
                        when {
                            currentPasscode.isBlank() -> tilCurrentPasscode.error = context?.stringResource(R.string.passcode_empty_message)
                            currentPasscode != viewModel.vaultPasscode.value -> tilCurrentPasscode.error = context?.stringResource(R.string.passcode_doesnt_match)
                            else -> setVaultPasscodeAndDismiss(newPasscode)
                        }
                    }
                }
            }
        }
    }

    private fun VaultPasscodeDialogFragmentBinding.setVaultPasscodeAndDismiss(passcode: String) {
        viewModel.setVaultPasscode(passcode)
            .invokeOnCompletion {
                context?.let { context -> parentFragment?.view?.snackbar(context.stringResource(R.string.vault_passcode_has_changed)) }
                activity?.hideKeyboard(etNewPasscode)
                activity?.hideKeyboard(etCurrentPasscode)
                dismiss()
            }
    }
}