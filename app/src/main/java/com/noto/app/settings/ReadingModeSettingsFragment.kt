package com.noto.app.settings

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.NotoTopAppbar
import com.noto.app.util.navController
import com.noto.app.util.setupMixedTransitions
import com.noto.app.util.snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReadingModeSettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private val notificationManager by lazy {
        context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
    }

    private val doNotDisturbResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (isDoNotDisturbSupported) {
            if (notificationManager?.isNotificationPolicyAccessGranted == true) {
                viewModel.toggleDoNotDisturb()
            } else {
                view?.snackbar(R.string.permission_not_granted, R.drawable.ic_round_warning_24)
            }
        }
    }

    private val isDoNotDisturbSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

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
                val scrollState = rememberScrollState()
                val theme by viewModel.theme.collectAsState()
                val doNotDisturbEnabled by viewModel.isDoNotDisturb.collectAsState()
                val keepScreenOnEnabled by viewModel.isScreenOn.collectAsState()
                val fullScreenEnabled by viewModel.isFullScreen.collectAsState()

                NotoTheme(theme = theme) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            NotoTopAppbar(
                                title = stringResource(id = R.string.reading_mode),
                                onNavigationIconClick = { navController?.navigateUp() }
                            )
                        },
                    ) { contentPadding ->
                        Column(
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .fillMaxSize()
                                .padding(contentPadding)
                        ) {
                            SettingsSection {
                                if (isDoNotDisturbSupported) {
                                    SettingsItem(
                                        title = stringResource(id = R.string.do_not_disturb),
                                        type = SettingsItemType.Switch(isChecked = doNotDisturbEnabled),
                                        onClick = { toggleDoNotDisturb() }
                                    )
                                }

                                SettingsItem(
                                    title = stringResource(id = R.string.keep_screen_on),
                                    type = SettingsItemType.Switch(isChecked = keepScreenOnEnabled),
                                    onClick = { viewModel.toggleScreenOn() }
                                )

                                SettingsItem(
                                    title = stringResource(id = R.string.full_screen),
                                    type = SettingsItemType.Switch(isChecked = fullScreenEnabled),
                                    onClick = { viewModel.toggleFullScreen() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun toggleDoNotDisturb() {
        if (viewModel.isDoNotDisturb.value) {
            viewModel.toggleDoNotDisturb()
        } else {
            if (isDoNotDisturbSupported) {
                if (notificationManager?.isNotificationPolicyAccessGranted == true) {
                    viewModel.toggleDoNotDisturb()
                } else {
                    val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                    doNotDisturbResult.launch(intent)
                }
            }
        }
    }
}