package com.noto.app.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.fragment.app.Fragment
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.*
import com.noto.app.data.model.remote.ResponseException
import com.noto.app.fold
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.setupMixedTransitions
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private val viewModel by viewModel<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        setupMixedTransitions()
        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                val state by viewModel.state.collectAsState()
                val email by viewModel.email.collectAsState()
                val password by viewModel.password.collectAsState()
                val emailStatus by viewModel.emailStatus.collectAsState()
                val passwordStatus by viewModel.passwordStatus.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val invalidEmailText = stringResource(id = R.string.invalid_email)
                val invalidPasswordText = stringResource(id = R.string.invalid_password)
                val invalidCredentialsText = stringResource(id = R.string.invalid_credentials)
                val snackbarMessage = stringResource(id = R.string.something_went_wrong)
                val snackbarActionLabelText = stringResource(id = R.string.show_info)
                val loggingInText = stringResource(id = R.string.logging_in)
                val emailNotVerifiedText = stringResource(id = R.string.email_not_verified)

                Screen(
                    title = stringResource(id = R.string.login),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column {
                        NotoTextField(
                            value = email,
                            onValueChange = {
                                viewModel.setEmail(it)
                                viewModel.setEmailStatus(TextFieldStatus.Empty)
                            },
                            placeholder = stringResource(id = R.string.email),
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_round_email_24),
                                    contentDescription = stringResource(id = R.string.email),
                                )
                            },
                            status = emailStatus,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next,
                            ),
                        )

                        Spacer(Modifier.height(NotoTheme.dimensions.medium))

                        NotoPasswordTextField(
                            value = password,
                            onValueChange = {
                                viewModel.setPassword(it)
                                viewModel.setPasswordStatus(TextFieldStatus.Empty)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            status = passwordStatus,
                        )

                        Spacer(Modifier.height(NotoTheme.dimensions.medium))

                        NotoFilledButton(
                            text = stringResource(id = R.string.login),
                            onClick = {
                                val isEmailInvalid = !email.matches(Constants.EmailRegex) || email.any { it.isWhitespace() }
                                val isPasswordInvalid = password.isBlank()
                                if (isEmailInvalid) {
                                    viewModel.setEmailStatus(TextFieldStatus.Error(invalidEmailText))
                                }
                                if (isPasswordInvalid) {
                                    viewModel.setPasswordStatus(TextFieldStatus.Error(invalidPasswordText))
                                }
                                if (!isEmailInvalid && !isPasswordInvalid) {
                                    viewModel.loginUser(email, password)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1F),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Text(text = stringResource(id = R.string.dont_have_account))
                    }
                }

                LaunchedEffect(key1 = state) {
                    state.fold(
                        onLoading = {
                            navController?.navigateSafely(
                                LoginFragmentDirections.actionLoginFragmentToProgressIndicatorDialogFragment(
                                    loggingInText
                                )
                            )
                        },
                        onSuccess = {
                            if (navController?.currentDestination?.id == R.id.progressIndicatorDialogFragment)
                                navController?.navigateUp()
                        },
                        onFailure = { exception ->
                            if (navController?.currentDestination?.id == R.id.progressIndicatorDialogFragment)
                                navController?.navigateUp()
                            when (exception) {
                                ResponseException.Auth.InvalidLoginCredentials -> {
                                    val result = snackbarHostState.showSnackbar(
                                        message = invalidCredentialsText,
                                        actionLabel = snackbarActionLabelText,
                                    )
                                    when (result) {
                                        SnackbarResult.Dismissed -> {}
                                        SnackbarResult.ActionPerformed -> {
                                            // TODO Navigate to info dialog and show the exception.
                                        }
                                    }
                                }
                                ResponseException.Auth.EmailNotVerified -> {
                                    snackbarHostState.showSnackbar(
                                        message = emailNotVerifiedText,
                                        actionLabel = snackbarActionLabelText,
                                    )
                                }
                                else -> {
                                    val result = snackbarHostState.showSnackbar(
                                        message = snackbarMessage,
                                        actionLabel = snackbarActionLabelText,
                                    )
                                    when (result) {
                                        SnackbarResult.Dismissed -> {}
                                        SnackbarResult.ActionPerformed -> {
                                            // TODO Navigate to info dialog and show the exception.
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}
