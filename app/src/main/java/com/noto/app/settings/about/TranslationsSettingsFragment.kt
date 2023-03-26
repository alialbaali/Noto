package com.noto.app.settings.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.components.MediumSubtitle
import com.noto.app.R
import com.noto.app.components.EmptyPainter
import com.noto.app.components.Screen
import com.noto.app.domain.model.Language
import com.noto.app.settings.SettingsItem
import com.noto.app.settings.SettingsItemType
import com.noto.app.settings.SettingsSection
import com.noto.app.util.navController
import com.noto.app.util.setupMixedTransitions
import com.noto.app.util.toLocalizedContext

private const val TurkishTranslatorWebsite = "https://linkedin.com/in/nuraysabri"
private const val TurkishProofreaderWebsite = "https://sakci.me"
private const val SpanishTranslatorWebsite = "https://github.com/faus32"
private const val ItalianTranslatorWebsite = "https://github.com/matteolomba"
private const val ItalianTranslator2Website = "https://github.com/SimoneG97"
private const val FrenchTranslatorWebsite = "https://github.com/kernoeb"
private const val FrenchTranslator2Website = "https://geoffreycrofte.com"

//private const val ArabicTranslatorWebsite = "https://twitter.com/trjman_en"
private const val ArabicProofreaderWebsite = "https://www.alialbaali.com"
private const val CzechTranslatorWebsite = "https://github.com/vikdevelop"
private const val LithuanianTranslatorWebsite = "http://github.com/ErnestasKaralius"
private const val SimplifiedChineseTranslatorWebsite = "https://steamcommunity.com/id/oliverberry"

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
                    val languages = remember { Language.values().sortedWith(Language.Comparator) }
                    languages.forEach { language ->
                        CompositionLocalProvider(LocalContext provides language.toLocalizedContext()) {
                            when (language) {
                                Language.System -> {}
                                Language.English -> {}
                                Language.Turkish -> Turkish()
                                Language.Arabic -> Arabic()
                                Language.Indonesian -> {}
                                Language.Russian -> {}
                                Language.Tamil -> {}
                                Language.Spanish -> Spanish()
                                Language.French -> French()
                                Language.German -> German()
                                Language.Italian -> Italian()
                                Language.Czech -> Czech()
                                Language.Lithuanian -> Lithuanian()
                                Language.SimplifiedChinese -> SimplifiedChinese()
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun Arabic() {
        SettingsSection(
            title = stringResource(id = R.string.arabic),
            subtitle = {
                context?.let { context ->
                    CompositionLocalProvider(LocalContext provides context) {
                        MediumSubtitle(text = stringResource(id = R.string.arabic))
                    }
                }
            },
            painter = painterResource(id = R.drawable.ic_saudi_arabia),
        ) {
//                            SettingsItem(
//                                title = stringResource(id = R.string.arabic_translator),
//                                type = SettingsItemType.None,
//                                onClick = {
//                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ArabicTranslatorWebsite))
//                                    startActivity(intent)
//                                },
//                                painter = EmptyPainter,
//                            )

            SettingsItem(
                title = stringResource(id = R.string.arabic_proofreader),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ArabicProofreaderWebsite))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )
        }
    }

    @Composable
    private fun Turkish() {
        SettingsSection(
            title = stringResource(id = R.string.turkish),
            subtitle = {
                context?.let { context ->
                    CompositionLocalProvider(LocalContext provides context) {
                        MediumSubtitle(text = stringResource(id = R.string.turkish))
                    }
                }
            },
            painter = painterResource(id = R.drawable.ic_turkey)
        ) {
            SettingsItem(
                title = stringResource(id = R.string.turkish_translator),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TurkishTranslatorWebsite))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )

            SettingsItem(
                title = stringResource(id = R.string.turkish_proofreader),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(TurkishProofreaderWebsite))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )
        }
    }

    @Composable
    private fun SimplifiedChinese() {
        SettingsSection(
            title = stringResource(id = R.string.simplified_chinese),
            subtitle = {
                context?.let { context ->
                    CompositionLocalProvider(LocalContext provides context) {
                        MediumSubtitle(text = stringResource(id = R.string.simplified_chinese))
                    }
                }
            },
            painter = painterResource(id = R.drawable.ic_china),
        ) {
            SettingsItem(
                title = stringResource(id = R.string.simplified_chinese_translator),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SimplifiedChineseTranslatorWebsite))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )
        }
    }

    @Composable
    private fun Lithuanian() {
        SettingsSection(
            title = stringResource(id = R.string.lithuanian),
            subtitle = {
                context?.let { context ->
                    CompositionLocalProvider(LocalContext provides context) {
                        MediumSubtitle(text = stringResource(id = R.string.lithuanian))
                    }
                }
            },
            painter = painterResource(id = R.drawable.ic_lithuania)
        ) {
            SettingsItem(
                title = stringResource(id = R.string.lithuanian_translator),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(LithuanianTranslatorWebsite))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )
        }
    }

    @Composable
    private fun Czech() {
        SettingsSection(
            title = stringResource(id = R.string.czech),
            subtitle = {
                context?.let { context ->
                    CompositionLocalProvider(LocalContext provides context) {
                        MediumSubtitle(text = stringResource(id = R.string.czech))
                    }
                }
            },
            painter = painterResource(id = R.drawable.ic_czech)
        ) {
            SettingsItem(
                title = stringResource(id = R.string.czech_translator),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CzechTranslatorWebsite))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )
        }
    }

    @Composable
    private fun Italian() {
        SettingsSection(
            title = stringResource(id = R.string.italian),
            subtitle = {
                context?.let { context ->
                    CompositionLocalProvider(LocalContext provides context) {
                        MediumSubtitle(text = stringResource(id = R.string.italian))
                    }
                }
            },
            painter = painterResource(id = R.drawable.ic_italy)
        ) {
            SettingsItem(
                title = stringResource(id = R.string.italian_translator),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ItalianTranslatorWebsite))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )

            SettingsItem(
                title = stringResource(id = R.string.italian_translator2),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(ItalianTranslator2Website))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )
        }
    }

    @Composable
    private fun French() {
        SettingsSection(
            title = stringResource(id = R.string.french),
            subtitle = {
                context?.let { context ->
                    CompositionLocalProvider(LocalContext provides context) {
                        MediumSubtitle(text = stringResource(id = R.string.french))
                    }
                }
            },
            painter = painterResource(id = R.drawable.ic_france)
        ) {
            SettingsItem(
                title = stringResource(id = R.string.french_translator),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(FrenchTranslatorWebsite))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )

            SettingsItem(
                title = stringResource(id = R.string.french_translator2),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(FrenchTranslator2Website))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )
        }
    }

    @Composable
    private fun Spanish() {
        SettingsSection(
            title = stringResource(id = R.string.spanish),
            subtitle = {
                context?.let { context ->
                    CompositionLocalProvider(LocalContext provides context) {
                        MediumSubtitle(text = stringResource(id = R.string.spanish))
                    }
                }
            },
            painter = painterResource(id = R.drawable.ic_spain)
        ) {
            SettingsItem(
                title = stringResource(id = R.string.spanish_translator),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SpanishTranslatorWebsite))
                    startActivity(intent)
                },
                painter = EmptyPainter,
            )
        }
    }

    @Composable
    private fun German() {
        SettingsSection(
            title = stringResource(id = R.string.german),
            subtitle = {
                context?.let { context ->
                    CompositionLocalProvider(LocalContext provides context) {
                        MediumSubtitle(text = stringResource(id = R.string.german))
                    }
                }
            },
            painter = painterResource(id = R.drawable.ic_germany)
        ) {
            SettingsItem(
                title = stringResource(id = R.string.german_translator),
                type = SettingsItemType.None,
                onClick = null,
                painter = EmptyPainter,
            )
        }
    }
}