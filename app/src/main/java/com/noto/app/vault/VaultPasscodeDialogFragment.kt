package com.noto.app.vault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.VaultPasscodeDialogFragmentBinding
import com.noto.app.settings.SettingsViewModel
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
        setupState()
        setupListeners()
    }

    private fun VaultPasscodeDialogFragmentBinding.setupState() {
        viewModel.vaultPasscode
            .onEach { passcode ->
                val enableVaultMessage = context?.stringResource(R.string.enable_vault_message)
                val vaultPasscodeMessage = context?.stringResource(R.string.vault_passcode_message)
                if (passcode == null) {
                    tvCurrentPasscode.isVisible = false
                    tilCurrentPasscode.isVisible = false
                    etNewPasscode.requestFocus()
                    activity?.showKeyboard(root)
                    tb.tvDialogTitle.text = context?.stringResource(R.string.vault_setup)
                    btnEnable.text = context?.stringResource(R.string.enable_vault)
                    tvConfirmation.text = enableVaultMessage?.plus("\n\n")?.plus(vaultPasscodeMessage)
                    etNewPasscode.imeOptions = EditorInfo.IME_ACTION_DONE
                } else {
                    tvCurrentPasscode.isVisible = true
                    tilCurrentPasscode.isVisible = true
                    etCurrentPasscode.requestFocus()
                    activity?.showKeyboard(etCurrentPasscode)
                    tb.tvDialogTitle.text = context?.stringResource(R.string.change_passcode)
                    btnEnable.text = context?.stringResource(R.string.update_passcode)
                    tvConfirmation.text = vaultPasscodeMessage
                    etCurrentPasscode.imeOptions = EditorInfo.IME_ACTION_NEXT
                    etNewPasscode.imeOptions = EditorInfo.IME_ACTION_DONE
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun VaultPasscodeDialogFragmentBinding.setupListeners() {
        btnEnable.setOnClickListener {
            val newPasscode = etNewPasscode.text.toString()
            when {
                newPasscode.isBlank() -> tilNewPasscode.error = context?.stringResource(R.string.passcode_empty_message)
                newPasscode.length < 6 -> tilNewPasscode.error = context?.stringResource(R.string.passcode_length_message)
                else -> {
                    tilNewPasscode.error = null
                    if (viewModel.vaultPasscode.value == null) {
                        setVaultPasscodeAndDismiss(newPasscode, isNew = true)
                    } else {
                        val currentPasscode = etCurrentPasscode.text.toString()
                        when {
                            currentPasscode.isBlank() -> tilCurrentPasscode.error = context?.stringResource(R.string.passcode_empty_message)
                            currentPasscode.hash() != viewModel.vaultPasscode.value -> tilCurrentPasscode.error =
                                context?.stringResource(R.string.passcode_doesnt_match)
                            else -> setVaultPasscodeAndDismiss(newPasscode, isNew = false)
                        }
                    }
                }
            }
        }
    }

    private fun VaultPasscodeDialogFragmentBinding.setVaultPasscodeAndDismiss(passcode: String, isNew: Boolean) {
        viewModel.setVaultPasscode(passcode)
            .invokeOnCompletion {
                val parentView = parentFragment?.view
                context?.let { context ->
                    val stringId = if (isNew) R.string.vault_is_enabled else R.string.vault_passcode_has_changed
                    val drawableId = if (isNew) R.drawable.ic_round_vault_enabled_24 else R.drawable.ic_round_key_24
                    parentView?.snackbar(context.stringResource(stringId), drawableId)
                }
                activity?.hideKeyboard(etNewPasscode)
                activity?.hideKeyboard(etCurrentPasscode)
                if (isNew) navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.IsPasscodeValid, true)
                dismiss()
            }
    }
}