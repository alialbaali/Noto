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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
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

class RegisterFragment : Fragment() {

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
                val name by viewModel.name.collectAsState()
                val email by viewModel.email.collectAsState()
                val password by viewModel.password.collectAsState()
                val confirmPassword by viewModel.confirmPassword.collectAsState()
                val nameStatus by viewModel.nameStatus.collectAsState()
                val emailStatus by viewModel.emailStatus.collectAsState()
                val passwordStatus by viewModel.passwordStatus.collectAsState()
                val confirmPasswordStatus by viewModel.confirmPasswordStatus.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val passwordInfoText = stringResource(id = R.string.password_info)
                val invalidEmailText = stringResource(id = R.string.invalid_email)
                val invalidPasswordText = "${stringResource(id = R.string.invalid_password)} $passwordInfoText"
                val invalidNameText = stringResource(id = R.string.invalid_name)
                val invalidConfirmPasswordText = stringResource(id = R.string.invalid_confirm_password)
                val userAlreadyRegisteredText = stringResource(id = R.string.user_already_registered)
                val snackbarMessage = stringResource(id = R.string.something_went_wrong)
                val snackbarActionLabelText = stringResource(id = R.string.show_info)
                val creatingAccountText = stringResource(id = R.string.creating_account)

                Screen(
                    title = stringResource(id = R.string.register),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(id = R.string.register_intro),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    )

                    Column {
                        NotoTextField(
                            value = name,
                            onValueChange = {
                                if (it.length <= Constants.NameMaxLength) {
                                    viewModel.setName(it)
                                }
                                viewModel.setNameStatus(TextFieldStatus.Empty)
                            },
                            placeholder = stringResource(id = R.string.name),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_round_person_24),
                                    contentDescription = stringResource(id = R.string.name)
                                )
                            },
                            status = nameStatus,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Words,
                                imeAction = ImeAction.Next,
                            ),
                        )

                        Spacer(Modifier.height(NotoTheme.dimensions.medium))

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
                                viewModel.setPasswordStatus(TextFieldStatus.Info(passwordInfoText))
                            },
                            modifier = Modifier.fillMaxWidth(),
                            status = passwordStatus,
                        )

                        Spacer(Modifier.height(NotoTheme.dimensions.medium))

                        NotoPasswordTextField(
                            value = confirmPassword,
                            onValueChange = {
                                viewModel.setConfirmPassword(it)
                                viewModel.setConfirmPasswordStatus(TextFieldStatus.Empty)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = stringResource(id = R.string.confirm_password),
                            status = confirmPasswordStatus,
                            keyboardOptions = KeyboardOptions.Password.copy(imeAction = ImeAction.Done)
                        )

                        Spacer(modifier = Modifier.height(NotoTheme.dimensions.extraLarge))

                        NotoFilledButton(
                            text = stringResource(id = R.string.register),
                            onClick = {
                                val isNameInvalid = name.isBlank()
                                val isEmailInvalid = !email.matches(Constants.EmailRegex) || email.any { it.isWhitespace() }
                                val isPasswordInvalid = !password.matches(Constants.PasswordRegex)
                                val isConfirmPasswordInvalid = confirmPassword != password
                                if (isNameInvalid) {
                                    viewModel.setNameStatus(TextFieldStatus.Error(invalidNameText))
                                }
                                if (isEmailInvalid) {
                                    viewModel.setEmailStatus(TextFieldStatus.Error(invalidEmailText))
                                }
                                if (isPasswordInvalid) {
                                    viewModel.setPasswordStatus(TextFieldStatus.Error(invalidPasswordText))
                                }
                                if (isConfirmPasswordInvalid) {
                                    viewModel.setConfirmPasswordStatus(TextFieldStatus.Error(invalidConfirmPasswordText))
                                }
                                if (!isNameInvalid && !isEmailInvalid && !isPasswordInvalid && !isConfirmPasswordInvalid) {
                                    viewModel.registerUser(name, email, password)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    Box(
                        modifier = Modifier.weight(1F),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Text(text = stringResource(id = R.string.already_have_an_account))
                    }
                }

                LaunchedEffect(key1 = state) {
                    state.fold(
                        onLoading = {
                            navController?.navigateSafely(
                                RegisterFragmentDirections.actionRegisterFragmentToProgressIndicatorDialogFragment(
                                    creatingAccountText
                                )
                            )
                        },
                        onSuccess = {
                            if (navController?.currentDestination?.id == R.id.progressIndicatorDialogFragment)
                                navController?.navigateUp()

                            navController?.navigateSafely(RegisterFragmentDirections.actionRegisterFragmentToVerifyEmailDialogFragment())
                        },
                        onFailure = { exception ->
                            if (navController?.currentDestination?.id == R.id.progressIndicatorDialogFragment)
                                navController?.navigateUp()
                            when (exception) {
                                ResponseException.Auth.UserAlreadyRegistered -> {
                                    viewModel.setEmailStatus(TextFieldStatus.Error(
                                        userAlreadyRegisteredText))
                                }
                                ResponseException.Auth.InvalidEmail -> {
                                    viewModel.setEmailStatus(TextFieldStatus.Error(invalidEmailText))
                                }
                                ResponseException.Auth.InvalidPassword -> {
                                    viewModel.setPasswordStatus(TextFieldStatus.Error(invalidPasswordText))
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