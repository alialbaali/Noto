package com.noto.app.settings.vault

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.biometric.BiometricManager
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.domain.model.VaultTimeout
import com.noto.app.settings.SettingsItem
import com.noto.app.settings.SettingsItemType
import com.noto.app.settings.SettingsSection
import com.noto.app.settings.SettingsViewModel
import com.noto.app.theme.warning
import com.noto.app.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class VaultSettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }
        setupMixedTransitions()
        ComposeView(context).apply {
            isTransitionGroup = true

            navController?.currentBackStackEntry?.savedStateHandle
                ?.getLiveData<Int>(Constants.ClickListener)
                ?.observe(viewLifecycleOwner) {
                    viewModel.disableVault()
                        .invokeOnCompletion {
                            snackbar(context.stringResource(R.string.vault_is_disabled), R.drawable.ic_round_vault_off_24)
                            navController?.navigateUp()
                        }
                }

            val isBioAuthSupported = BiometricManager.from(context)
                .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS

            setContent {
                val timeout by viewModel.vaultTimeout.collectAsState()
                val timeoutText = when (timeout) {
                    VaultTimeout.Immediately -> stringResource(id = R.string.immediately)
                    VaultTimeout.OnAppClose -> stringResource(id = R.string.on_app_close)
                    VaultTimeout.After1Hour -> stringResource(id = R.string.after_1_hour)
                    VaultTimeout.After4Hours -> stringResource(id = R.string.after_4_hours)
                    VaultTimeout.After12Hours -> stringResource(id = R.string.after_12_hours)
                }
                val isBioAuthEnabled by viewModel.isBioAuthEnabled.collectAsState()
                Screen(title = stringResource(id = R.string.vault)) {
                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.change_passcode),
                            type = SettingsItemType.None,
                            onClick = { navController?.navigateSafely(VaultSettingsFragmentDirections.actionVaultSettingsFragmentToVaultPasscodeDialogFragment()) },
                            painter = painterResource(id = R.drawable.ic_round_key_24)
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.timeout),
                            type = SettingsItemType.Text(timeoutText),
                            onClick = { navController?.navigateSafely(VaultSettingsFragmentDirections.actionVaultSettingsFragmentToVaultTimeoutDialogFragment()) },
                            description = stringResource(id = R.string.timeout_description),
                            painter = painterResource(id = R.drawable.ic_round_timer_24)
                        )

                        if (isBioAuthSupported) {
                            SettingsItem(
                                title = stringResource(id = R.string.bio_auth),
                                type = SettingsItemType.Switch(isBioAuthEnabled),
                                onClick = { viewModel.toggleIsBioAuthEnabled() },
                                description = stringResource(id = R.string.bio_auth_description),
                                painter = painterResource(id = R.drawable.ic_round_fingerprint_24)
                            )
                        }
                    }

                    SettingsSection {
                        val disableVaultConfirmation = stringResource(id = R.string.disable_vault_confirmation)
                        val disableVaultDescription = stringResource(id = R.string.disable_vault_description)
                        val disableVaultText = stringResource(id = R.string.disable_vault)

                        SettingsItem(
                            title = stringResource(id = R.string.disable),
                            type = SettingsItemType.None,
                            onClick = {
                                navController?.navigateSafely(
                                    VaultSettingsFragmentDirections.actionVaultSettingsFragmentToConfirmationDialogFragment(
                                        confirmation = disableVaultConfirmation,
                                        description = disableVaultDescription,
                                        btnText = disableVaultText,
                                    )
                                )
                            },
                            titleColor = MaterialTheme.colorScheme.warning,
                            painter = painterResource(id = R.drawable.ic_round_vault_off_24),
                            painterColor = MaterialTheme.colorScheme.warning,
                        )
                    }
                }
            }
        }
    }
}