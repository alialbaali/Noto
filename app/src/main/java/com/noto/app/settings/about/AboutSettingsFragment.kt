package com.noto.app.settings.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.settings.*
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.setupMixedTransitions
import kotlinx.coroutines.launch

class AboutSettingsFragment : Fragment() {

    @Suppress("DEPRECATION")
    private val version by lazy {
        context?.let { context ->
            context.packageManager?.getPackageInfo(context.packageName, 0)?.versionName
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        setupMixedTransitions()

        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }

        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                val snackbarHostState = remember { SnackbarHostState() }
                val scope = rememberCoroutineScope()
                val versionIsCopiedText = stringResource(id = R.string.version_is_copied)

                Screen(
                    title = stringResource(id = R.string.about),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) {
                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.developer),
                            type = SettingsItemType.Text(stringResource(id = R.string.developer_name)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Noto.DeveloperUrl))
                                startActivity(intent)
                            },
                            painter = painterResource(id = R.drawable.ic_round_person_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.support_noto),
                            type = SettingsItemType.None,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SupportNotoUrl))
                                startActivity(intent)
                            },
                            description = stringResource(id = R.string.support_noto_description),
                            painter = painterResource(id = R.drawable.ic_round_favorite_24),
                            painterColor = SupportNotoColor,
                            titleColor = SupportNotoColor,
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.translate_noto),
                            type = SettingsItemType.None,
                            onClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(Constants.EmailType)).apply {
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.Noto.Email))
                                    putExtra(Intent.EXTRA_SUBJECT, Constants.Noto.TranslationEmailSubject)
                                    putExtra(Intent.EXTRA_TEXT, Constants.Noto.TranslationEmailBody)
                                }
                                startActivity(intent)
                            },
                            description = stringResource(id = R.string.translate_noto_description),
                            painter = painterResource(id = R.drawable.ic_round_translate_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.translations),
                            type = SettingsItemType.None,
                            onClick = {
                                navController?.navigateSafely(AboutSettingsFragmentDirections.actionAboutSettingsFragmentToTranslationsSettingsFragment())
                            },
                            description = stringResource(id = R.string.translations_description),
                            painter = painterResource(id = R.drawable.ic_round_language_24),
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.credits),
                            type = SettingsItemType.None,
                            onClick = {
                                navController?.navigateSafely(AboutSettingsFragmentDirections.actionAboutSettingsFragmentToCreditsSettingsFragment())
                            },
                            painter = painterResource(id = R.drawable.ic_round_attribution_24),
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.source_code),
                            type = SettingsItemType.None,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Noto.GithubUrl))
                                startActivity(intent)
                            },
                            description = stringResource(id = R.string.source_code_description),
                            painter = painterResource(id = R.drawable.ic_github_logo),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.telegram_community),
                            type = SettingsItemType.None,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Noto.TelegramUrl))
                                startActivity(intent)
                            },
                            description = stringResource(id = R.string.telegram_community_description),
                            painter = painterResource(id = R.drawable.ic_telegram_logo),
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.privacy_policy),
                            type = SettingsItemType.None,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Noto.PrivacyPolicyUrl))
                                startActivity(intent)
                            },
                            description = stringResource(id = R.string.privacy_policy_description),
                            painter = painterResource(id = R.drawable.ic_round_policy_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.license),
                            type = SettingsItemType.Text(stringResource(id = R.string.license_value)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Noto.LicenseUrl))
                                startActivity(intent)
                            },
                            painter = painterResource(id = R.drawable.ic_round_assignment_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.version),
                            type = version?.let { SettingsItemType.Text(it) } ?: SettingsItemType.None,
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(versionIsCopiedText)
                                }
                            },
                            painter = painterResource(id = R.drawable.ic_round_tag_24),
                        )
                    }

                }
            }
        }
    }
}