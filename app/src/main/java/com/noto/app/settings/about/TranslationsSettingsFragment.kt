package com.noto.app.settings.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.domain.model.Translation
import com.noto.app.settings.SettingsItem
import com.noto.app.settings.SettingsItemType
import com.noto.app.settings.SettingsSection
import com.noto.app.util.*

class TranslationsSettingsFragment : Fragment() {

    private val translations by lazy {
        context?.let { context ->
            Translation.Default
                .map {
                    val sortedTranslators = it.translators.sortedWith(Translation.Translator.Comparator(context))
                    it.copy(translators = sortedTranslators)
                }
                .sortedWith(Translation.Comparator(context))
        } ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        activity?.onBackPressedDispatcher?.addCallback { navController?.navigateUp() }
        setupMixedTransitions()
        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                Screen(title = stringResource(id = R.string.translations)) {
                    SettingsItem(title = stringResource(id = R.string.translations_description), type = SettingsItemType.None)
                    translations.forEach { translation -> Translation(translation, context) }
                }
            }
        }
    }

    @Composable
    private fun Translation(translation: Translation, context: Context) {
        val localizedContext = context.localize(translation.language)
        SettingsSection {
            SettingsItem(
                title = localizedContext.stringResource(translation.language.toStringResourceId()),
                type = SettingsItemType.Text(context.stringResource(translation.language.toStringResourceId())),
                painter = painterResource(id = translation.iconId),
                equalWeights = false,
                contentScale = ContentScale.Crop,
            )

            translation.translators.forEach { translator ->
                if (translator.urlId != null) {
                    SettingsItem(
                        title = stringResource(id = translator.nameId),
                        type = SettingsItemType.None,
                        painter = painterResource(id = R.drawable.ic_round_person_24),
                        onClick = {
                            val url = context.stringResource(id = translator.urlId)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        },
                    )
                } else {
                    SettingsItem(
                        title = stringResource(id = translator.nameId),
                        type = SettingsItemType.None,
                        painter = painterResource(id = R.drawable.ic_round_person_24),
                    )
                }
            }
        }
    }
}