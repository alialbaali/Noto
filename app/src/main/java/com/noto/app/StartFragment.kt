package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.components.NotoFilledButton
import com.noto.app.components.NotoOutlinedButton
import com.noto.app.components.NotoTextButton
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
                val theme by viewModel.theme.collectAsState()
                val scrollState = rememberScrollState()
                NotoTheme(theme) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(NotoTheme.dimensions.medium)
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(text = stringResource(id = R.string.welcome), style = MaterialTheme.typography.titleLarge)

                        Column(
                            verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium)
                        ) {
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
                            modifier = Modifier.fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }

}