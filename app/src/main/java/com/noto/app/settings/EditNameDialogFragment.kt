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
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.noto.app.BaseDialogFragment
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.NotoFilledButton
import com.noto.app.components.NotoTextField
import com.noto.app.components.TextFieldStatus
import com.noto.app.fold
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditNameDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                val name by viewModel.name.collectAsState()
                val nameStatus by viewModel.nameStatus.collectAsState()
                val nameState by viewModel.nameState.collectAsState()
                val invalidNameText = stringResource(id = R.string.invalid_name)
                val loadingText = stringResource(id = R.string.updating_name)

                BottomSheetDialog(title = stringResource(id = R.string.edit_name)) {
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

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.medium))

                    NotoFilledButton(
                        text = stringResource(id = R.string.update_name),
                        onClick = {
                            if (name.isBlank()) {
                                viewModel.setNameStatus(TextFieldStatus.Error(invalidNameText))
                            } else {
                                viewModel.updateName()
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                nameState.fold(
                    onEmpty = {},
                    onLoading = {
                        navController?.navigateSafely(
                            EditNameDialogFragmentDirections.actionEditNameDialogFragmentToProgressIndicatorDialogFragment(
                                loadingText
                            )
                        )
                    },
                    onSuccess = {
                        parentFragment?.view?.snackbar(R.string.name_is_updated)
                        SideEffect {
                            navController?.navigateUp()
                            navController?.navigateUp()
                        }
                    },
                    onFailure = {
                        parentFragment?.view?.snackbar(R.string.something_went_wrong)
                        SideEffect {
                            navController?.navigateUp()
                            navController?.navigateUp()
                        }
                    }
                )
            }
        }
    }
}