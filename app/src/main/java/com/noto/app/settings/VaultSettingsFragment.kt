package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.biometric.BiometricManager
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.domain.model.VaultTimeout
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.setupMixedTransitions
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
            setContent {
                val timeout by viewModel.vaultTimeout.collectAsState()
                val timeoutText = when (timeout) {
                    VaultTimeout.Immediately -> stringResource(id = R.string.immediately)
                    VaultTimeout.OnAppClose -> stringResource(id = R.string.on_app_close)
                    VaultTimeout.After1Hour -> stringResource(id = R.string.after_1_hour)
                    VaultTimeout.After4Hours -> stringResource(id = R.string.after_4_hours)
                    VaultTimeout.After12Hours -> stringResource(id = R.string.after_12_hours)
                }
                val isBioAuthSupported = BiometricManager.from(context)
                    .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
                val isBioAuthEnabled by viewModel.isBioAuthEnabled.collectAsState()
                Screen(title = stringResource(id = R.string.vault)) {
                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.change_passcode),
                            type = SettingsItemType.None,
                            onClick = { navController?.navigateSafely(VaultSettingsFragmentDirections.actionVaultSettingsFragmentToVaultPasscodeDialogFragment()) }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.timeout),
                            type = SettingsItemType.Text(timeoutText),
                            onClick = { navController?.navigateSafely(VaultSettingsFragmentDirections.actionVaultSettingsFragmentToVaultTimeoutDialogFragment()) }
                        )

                        if (isBioAuthSupported) {
                            SettingsItem(
                                title = stringResource(id = R.string.bio_auth),
                                type = SettingsItemType.Switch(isBioAuthEnabled),
                                onClick = { viewModel.toggleIsBioAuthEnabled() }
                            )
                        }
                    }
                }
            }
        }
    }
}