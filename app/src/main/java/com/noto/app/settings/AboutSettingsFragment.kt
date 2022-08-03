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
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.util.navController
import com.noto.app.util.setupMixedTransitions
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val DeveloperWebsite = "https://www.alialbaali.com"
private const val LicenseWebsite = "https://www.apache.org/licenses/LICENSE-2.0"
private const val CollectionOfGradientsWebsite = "https://www.figma.com/community/file/830405806109119447"
private const val CreatorWebsite = "https://geoffreycrofte.com"
private const val AppIconsLicenseWebsite = "https://creativecommons.org/licenses/by/4.0/"
private const val TurkishTranslatorWebsite = "https://linkedin.com/in/nuraysabri"
private const val TurkishProofreaderWebsite = "https://sakci.me"
private const val SpanishTranslatorWebsite = "https://github.com/faus32"
private const val FrenchTranslatorWebsite = "https://github.com/kernoeb"
private const val FrenchTranslator2Website = "https://geoffreycrofte.com"
private const val CrowdinWebsite = "https://crowdin.com/project/notoapp"
private const val ArabicTranslatorWebsite = "https://www.alialbaali.com"

class AboutSettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

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
                            title = stringResource(id = R.string.version),
                            type = version?.let { SettingsItemType.Text(it) } ?: SettingsItemType.None,
                            onClick = {
                                scope.launch {
                                    snackbarHostState.showSnackbar(versionIsCopiedText)
                                }
                            },
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.developer),
                            type = SettingsItemType.Text(stringResource(id = R.string.developer_name)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DeveloperWebsite))
                                startActivity(intent)
                            },
                        )

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
                            title = stringResource(id = R.string.app_icons),
                            type = SettingsItemType.Text(stringResource(id = R.string.app_icons_value)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CollectionOfGradientsWebsite))
                                startActivity(intent)
                            },
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.creator),
                            type = SettingsItemType.Text(stringResource(id = R.string.creator_name)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CreatorWebsite))
                                startActivity(intent)
                            },
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.license),
                            type = SettingsItemType.Text(stringResource(id = R.string.app_icons_license_value)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppIconsLicenseWebsite))
                                startActivity(intent)
                            },
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.language),
                            type = SettingsItemType.Text(stringResource(id = R.string.arabic)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CrowdinWebsite))
                                startActivity(intent)
                            }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.translator),
                            type = SettingsItemType.Text(stringResource(id = R.string.arabic_translator)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ArabicTranslatorWebsite))
                                startActivity(intent)
                            }
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.language),
                            type = SettingsItemType.Text(stringResource(id = R.string.turkish)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CrowdinWebsite))
                                startActivity(intent)
                            }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.translator),
                            type = SettingsItemType.Text(stringResource(id = R.string.turkish_translator)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TurkishTranslatorWebsite))
                                startActivity(intent)
                            }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.proofreader),
                            type = SettingsItemType.Text(stringResource(id = R.string.turkish_proofreader)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TurkishProofreaderWebsite))
                                startActivity(intent)
                            }
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.language),
                            type = SettingsItemType.Text(stringResource(id = R.string.german)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CrowdinWebsite))
                                startActivity(intent)
                            }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.translator),
                            type = SettingsItemType.Text(stringResource(id = R.string.german_translator)),
                            onClick = {}
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.language),
                            type = SettingsItemType.Text(stringResource(id = R.string.spanish)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CrowdinWebsite))
                                startActivity(intent)
                            }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.translator),
                            type = SettingsItemType.Text(stringResource(id = R.string.spanish_translator)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SpanishTranslatorWebsite))
                                startActivity(intent)
                            }
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.language),
                            type = SettingsItemType.Text(stringResource(id = R.string.french)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CrowdinWebsite))
                                startActivity(intent)
                            }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.translator),
                            type = SettingsItemType.Text(stringResource(id = R.string.french_translator)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(FrenchTranslatorWebsite))
                                startActivity(intent)
                            }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.translator),
                            type = SettingsItemType.Text(stringResource(id = R.string.french_translator2)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(FrenchTranslator2Website))
                                startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}