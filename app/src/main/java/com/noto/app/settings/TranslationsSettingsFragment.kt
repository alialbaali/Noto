package com.noto.app.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.util.navController
import com.noto.app.util.setupMixedTransitions

private const val TurkishTranslatorWebsite = "https://linkedin.com/in/nuraysabri"
private const val TurkishProofreaderWebsite = "https://sakci.me"
private const val SpanishTranslatorWebsite = "https://github.com/faus32"
private const val FrenchTranslatorWebsite = "https://github.com/kernoeb"
private const val FrenchTranslator2Website = "https://geoffreycrofte.com"
private const val CrowdinWebsite = "https://crowdin.com/project/notoapp"
private const val ArabicTranslatorWebsite = "https://twitter.com/trjman_en"
private const val ArabicProofreaderWebsite = "https://www.alialbaali.com"

class TranslationsSettingsFragment : Fragment() {

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
                Screen(title = stringResource(id = R.string.translations)) {
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

                        SettingsItem(
                            title = stringResource(id = R.string.proofreader),
                            type = SettingsItemType.Text(stringResource(id = R.string.arabic_proofreader)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ArabicProofreaderWebsite))
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