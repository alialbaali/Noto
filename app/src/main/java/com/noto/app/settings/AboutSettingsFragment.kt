package com.noto.app.settings

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
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.setupMixedTransitions
import kotlinx.coroutines.launch

private const val DeveloperWebsite = "https://www.alialbaali.com"
private const val LicenseWebsite = "https://www.apache.org/licenses/LICENSE-2.0"
private const val GithubUrl = "https://github.com/alialbaali/Noto"
private const val RedditUrl = "https://reddit.com/r/notoapp"
private const val TranslationInviteUrl = "https://crwd.in/notoapp"
private const val BuyMeACoffeeUrl = "https://www.buymeacoffee.com/alialbaali"
private const val BecomeAPatronUrl = "https://www.patreon.com/alialbaali"

class AboutSettingsFragment : Fragment() {

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
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DeveloperWebsite))
                                startActivity(intent)
                            },
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.buy_me_a_coffee),
                            type = SettingsItemType.Icon(painterResource(id = R.drawable.ic_bmc_logo)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BuyMeACoffeeUrl))
                                startActivity(intent)
                            },
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.become_a_patron),
                            type = SettingsItemType.Icon(painterResource(id = R.drawable.patreon_logo)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BecomeAPatronUrl))
                                startActivity(intent)
                            },
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.translate_noto),
                            type = SettingsItemType.None,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TranslationInviteUrl))
                                startActivity(intent)
                            }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.translations),
                            type = SettingsItemType.None,
                            onClick = {
                                navController?.navigateSafely(AboutSettingsFragmentDirections.actionAboutSettingsFragmentToTranslationsSettingsFragment())
                            },
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.credits),
                            type = SettingsItemType.None,
                            onClick = {
                                navController?.navigateSafely(AboutSettingsFragmentDirections.actionAboutSettingsFragmentToCreditsSettingsFragment())
                            },
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.source_code),
                            type = SettingsItemType.Icon(painterResource(id = R.drawable.ic_github_logo)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GithubUrl))
                                startActivity(intent)
                            }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.reddit_community),
                            type = SettingsItemType.Icon(painterResource(id = R.drawable.ic_reddit_logo)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(RedditUrl))
                                startActivity(intent)
                            }
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.license),
                            type = SettingsItemType.Text(stringResource(id = R.string.license_value)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(LicenseWebsite))
                                startActivity(intent)
                            }
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.version),
                            type = version?.let { SettingsItemType.Text(it) } ?: SettingsItemType.None,
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(versionIsCopiedText)
                                }
                            },
                        )
                    }

                }
            }
        }
    }
}