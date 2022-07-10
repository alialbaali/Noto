package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.components.NotoFilledButton
import com.noto.app.components.NotoOutlinedButton
import com.noto.app.components.NotoTextButton
import com.noto.app.components.Screen
import com.noto.app.domain.model.Folder
import com.noto.app.login.LoginViewModel
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.setupMixedTransitions
import org.koin.androidx.viewmodel.ext.android.viewModel

class StartFragment : Fragment() {

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
                Screen(
                    title = stringResource(id = R.string.welcome),
                    verticalArrangement = Arrangement.SpaceBetween,
                    onNavigationIconClick = null,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium)) {
                        NotoFilledButton(
                            text = stringResource(id = R.string.register_for_free),
                            onClick = {
                                navController?.navigateSafely(StartFragmentDirections.actionStartFragmentToRegisterFragment())
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        NotoOutlinedButton(
                            text = stringResource(id = R.string.login),
                            onClick = {

                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    NotoTextButton(
                        text = stringResource(id = R.string.skip_for_now),
                        onClick = {
                            viewModel.skipLogin()
                            navController?.navigateSafely(
                                StartFragmentDirections.actionStartFragmentToFolderFragment(Folder.GeneralFolderId)
                            )
                            navController?.navigate(R.id.mainFragment)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}