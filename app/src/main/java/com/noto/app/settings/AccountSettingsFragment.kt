package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.NotoFilledButton
import com.noto.app.components.NotoProgressIndicator
import com.noto.app.components.Screen
import com.noto.app.fold
import com.noto.app.util.*
import com.noto.app.warning
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val LogOutCode = 0
private const val DeleteAccountCode = 1

class AccountSettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Int>(Constants.ClickListener)
            ?.observe(viewLifecycleOwner) { code ->
                when (code) {
                    LogOutCode -> logOut()
                    DeleteAccountCode -> deleteUser()
                }
            }

        setupMixedTransitions()
        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                val userState by viewModel.userState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                Screen(
                    title = stringResource(id = R.string.account),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) {
                    userState.fold(
                        onEmpty = {},
                        onLoading = { NotoProgressIndicator() },
                        onSuccess = { user ->
                            SettingsSection {
                                SettingsItem(
                                    title = stringResource(id = R.string.name),
                                    type = SettingsItemType.Text(user.name),
                                    onClick = { navController?.navigateSafely(AccountSettingsFragmentDirections.actionAccountSettingsFragmentToEditNameDialogFragment()) },
                                )
                                SettingsItem(
                                    title = stringResource(id = R.string.email),
                                    type = SettingsItemType.Text(user.email),
                                    onClick = { /*TODO*/ },
                                )
                            }

                            SettingsSection {
                                val confirmationText = stringResource(id = R.string.logout_confirmation)
                                val descriptionText = stringResource(id = R.string.logout_description)
                                val logOutText = stringResource(id = R.string.logout)
                                SettingsItem(
                                    title = logOutText,
                                    type = SettingsItemType.None,
                                    onClick = {
                                        navController?.navigateSafely(
                                            AccountSettingsFragmentDirections.actionAccountSettingsFragmentToConfirmationDialogFragment(
                                                confirmation = confirmationText,
                                                description = descriptionText,
                                                btnText = logOutText,
                                                code = LogOutCode,
                                            )
                                        )
                                    },
                                    titleColor = MaterialTheme.colorScheme.warning,
                                )
                            }

                            Spacer(Modifier.weight(1F))

                            val confirmationText = stringResource(id = R.string.delete_account_confirmation)
                            val descriptionText = stringResource(id = R.string.delete_account_description)
                            val deleteText = stringResource(id = R.string.delete_account)
                            NotoFilledButton(
                                text = deleteText,
                                onClick = {
                                    navController?.navigateSafely(
                                        AccountSettingsFragmentDirections.actionAccountSettingsFragmentToConfirmationDialogFragment(
                                            confirmation = confirmationText,
                                            description = descriptionText,
                                            btnText = deleteText,
                                            code = DeleteAccountCode,
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.errorContainer,
                                    contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                )
                            )
                        },
                        onFailure = {
                            val text = stringResource(id = R.string.something_went_wrong)
                            scope.launch { snackbarHostState.showSnackbar(text) }
                        },
                    )
                }
            }
        }
    }

    private fun logOut() {
        navController?.popBackStack()
        context?.let { context ->
            navController?.navigateSafely(
                AccountSettingsFragmentDirections.actionAccountSettingsFragmentToProgressIndicatorDialogFragment(
                    context.stringResource(R.string.loggin_out)
                )
            )
        }

        viewModel.logOutUser().invokeOnCompletion {
            navController?.popBackStack()
            navController?.navigateSafely(AccountSettingsFragmentDirections.actionAccountSettingsFragmentToStartFragment())
        }
    }

    private fun deleteUser() {
        navController?.popBackStack()
        context?.let { context ->
            navController?.navigateSafely(
                AccountSettingsFragmentDirections.actionAccountSettingsFragmentToProgressIndicatorDialogFragment(
                    context.stringResource(R.string.deleting_account)
                )
            )
        }
        viewModel.deleteUser().invokeOnCompletion {
            navController?.popBackStack()
            navController?.navigateSafely(AccountSettingsFragmentDirections.actionAccountSettingsFragmentToStartFragment())
        }
    }
}