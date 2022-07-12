package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.noto.app.BaseDialogFragment
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.NotoFilledButton
import com.noto.app.components.NotoTextField
import com.noto.app.components.TextFieldStatus
import com.noto.app.data.model.remote.ResponseException
import com.noto.app.fold
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangeEmailDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                val email by viewModel.email.collectAsState()
                val emailStatus by viewModel.emailStatus.collectAsState()
                val emailState by viewModel.emailState.collectAsState()
                val invalidEmailText = stringResource(id = R.string.invalid_email)
                val loadingText = stringResource(id = R.string.updating_email)

                BottomSheetDialog(title = stringResource(id = R.string.change_email)) {
                    NotoTextField(
                        value = email,
                        onValueChange = {
                            viewModel.setEmail(it)
                            viewModel.setEmailStatus(TextFieldStatus.Empty)
                        },
                        placeholder = stringResource(id = R.string.email),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_round_email_24),
                                contentDescription = stringResource(id = R.string.email)
                            )
                        },
                        status = emailStatus,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Done,
                        ),
                    )

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.medium))

                    NotoFilledButton(
                        text = stringResource(id = R.string.update_email),
                        onClick = {
                            if (!email.matches(Constants.EmailRegex) || email.any { it.isWhitespace() }) {
                                viewModel.setEmailStatus(TextFieldStatus.Error(invalidEmailText))
                            } else {
                                viewModel.updateEmail()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                emailState.fold(
                    onEmpty = {},
                    onLoading = {
                        navController?.navigateSafely(
                            ChangeEmailDialogFragmentDirections.actionChangeEmailDialogFragmentToProgressIndicatorDialogFragment(
                                loadingText
                            )
                        )
                    },
                    onSuccess = {
                        if (navController?.currentDestination?.id == R.id.progressIndicatorDialogFragment)
                            navController?.navigateUp()

                        navController?.navigateSafely(ChangeEmailDialogFragmentDirections.actionChangeEmailDialogFragmentToVerifyEmailDialogFragment())
                    },
                    onFailure = { exception ->
                        if (navController?.currentDestination?.id == R.id.progressIndicatorDialogFragment)
                            navController?.navigateUp()

                        when (exception) {
                            ResponseException.Auth.UserAlreadyRegistered -> {
                                viewModel.setEmailStatus(TextFieldStatus.Error(stringResource(id = R.string.user_already_registered)))
                            }
                            ResponseException.Auth.InvalidEmail -> {
                                viewModel.setEmailStatus(TextFieldStatus.Error(stringResource(id = R.string.invalid_email)))
                            }
                            else -> {
                                SideEffect {
                                    navController?.navigateUp()
                                }
                                parentFragment?.view?.snackbar(R.string.something_went_wrong)
                            }
                        }
                    }
                )
            }
        }
    }

}