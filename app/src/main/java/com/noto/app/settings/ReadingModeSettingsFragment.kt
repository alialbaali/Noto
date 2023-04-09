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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.util.*
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
                context?.let { context ->
                    view?.snackbar(context.stringResource(R.string.permission_not_granted), R.drawable.ic_round_warning_24)
                }
            }
        }
    }

    private val isDoNotDisturbSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

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
                val doNotDisturbEnabled by viewModel.isDoNotDisturb.collectAsState()
                val keepScreenOnEnabled by viewModel.isScreenOn.collectAsState()
                val fullScreenEnabled by viewModel.isFullScreen.collectAsState()
                val screenBrightnessLevel by viewModel.screenBrightnessLevel.collectAsState()
                Screen(title = stringResource(id = R.string.reading_mode)) {
                    SettingsSection {
                        if (isDoNotDisturbSupported) {
                            SettingsItem(
                                title = stringResource(id = R.string.do_not_disturb),
                                type = SettingsItemType.Switch(isChecked = doNotDisturbEnabled),
                                onClick = { toggleDoNotDisturb() },
                                description = stringResource(id = R.string.do_not_disturb_description),
                                painter = painterResource(id = R.drawable.ic_round_do_not_disturb_24)
                            )
                        }

                        SettingsItem(
                            title = stringResource(id = R.string.keep_screen_on),
                            type = SettingsItemType.Switch(isChecked = keepScreenOnEnabled),
                            onClick = { viewModel.toggleScreenOn() },
                            description = stringResource(id = R.string.keep_screen_on_description),
                            painter = painterResource(id = R.drawable.ic_round_visibility_24)
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.full_screen_mode),
                            type = SettingsItemType.Switch(isChecked = fullScreenEnabled),
                            onClick = { viewModel.toggleFullScreen() },
                            description = stringResource(id = R.string.full_screen_description),
                            painter = painterResource(id = R.drawable.ic_round_fullscreen_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.screen_brightness_level),
                            type = SettingsItemType.Text(stringResource(id = screenBrightnessLevel.toResource())),
                            onClick = { navController?.navigateSafely(ReadingModeSettingsFragmentDirections.actionReadingModeSettingsFragmentToScreenBrightnessLevelDialogFragment()) },
                            description = stringResource(id = R.string.screen_brightness_level_description),
                            painter = painterResource(id = R.drawable.ic_round_brightness_24)
                        )
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