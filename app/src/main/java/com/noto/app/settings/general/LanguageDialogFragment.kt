package com.noto.app.settings.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.os.LocaleListCompat
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.MediumSubtitle
import com.noto.app.components.SelectableDialogItem
import com.noto.app.domain.model.Language
import com.noto.app.settings.SettingsViewModel
import com.noto.app.theme.NotoTheme
import com.noto.app.util.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LanguageDialogFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                BottomSheetDialog(title = stringResource(id = R.string.language)) {
                    val selectedLanguage = remember { AppCompatDelegate.getApplicationLocales().toLanguages().first() }
                    val languages = remember(context) {
                        Language.entries
                            .sortedWith(Language.Comparator(context))
                            .map { it to context.localize(it) }
                    }
                    languages.forEach { (language, localizedContext) ->
                        SelectableDialogItem(
                            selected = selectedLanguage == language,
                            onClick = {
                                viewModel.updateLanguage(language)
                                if (language == Language.System) {
                                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                                } else {
                                    arrayOf(language.toLocale())
                                        .let(LocaleListCompat::create)
                                        .also(AppCompatDelegate::setApplicationLocales)
                                }
                                dismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = language !in Language.Deprecated
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.weight(1F), verticalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.extraSmall)) {
                                    Text(text = localizedContext.stringResource(language.toStringResourceId()))
                                    if (language != Language.System) {
                                        MediumSubtitle(text = context.stringResource(language.toStringResourceId()))
                                    }
                                }
                                if (language in Language.Deprecated) {
                                    Spacer(modifier = Modifier.width(NotoTheme.dimensions.medium))
                                    Text(
                                        text = stringResource(id = R.string.not_finished),
                                        color = MaterialTheme.colorScheme.secondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}