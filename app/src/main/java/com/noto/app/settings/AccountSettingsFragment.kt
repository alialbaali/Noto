package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
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
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.NotoProgressIndicator
import com.noto.app.components.NotoTopAppbar
import com.noto.app.fold
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.setupMixedTransitions
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountSettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
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
                val userState by viewModel.userState.collectAsState()
                val scrollState = rememberScrollState()
                val theme by viewModel.theme.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                NotoTheme(theme = theme) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            NotoTopAppbar(
                                title = stringResource(id = R.string.account),
                                onNavigationIconClick = { navController?.navigateUp() },
                            )
                        },
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                    ) { contentPadding ->
                        userState.fold(
                            onEmpty = {},
                            onLoading = { NotoProgressIndicator() },
                            onSuccess = { user ->
                                Column(
                                    modifier = Modifier
                                        .verticalScroll(scrollState)
                                        .fillMaxSize()
                                        .padding(contentPadding),
                                ) {
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
                                }
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
    }
}