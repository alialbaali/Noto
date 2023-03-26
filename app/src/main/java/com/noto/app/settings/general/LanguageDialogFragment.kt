package com.noto.app.settings.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.SelectableDialogItem
import com.noto.app.domain.model.Language
import com.noto.app.settings.SettingsViewModel
import com.noto.app.util.localizedName
import com.noto.app.util.toLocalizedContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class LanguageDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                BottomSheetDialog(title = stringResource(id = R.string.language)) {
                    val selectedLanguage by viewModel.language.collectAsState()
                    val languages = remember { Language.values().sortedWith(Language.Comparator) }
                    languages.forEach { language ->
                        SelectableDialogItem(
                            selected = selectedLanguage == language,
                            onClick = {
                                viewModel.updateLanguage(language)
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
                                    CompositionLocalProvider(LocalContext provides language.toLocalizedContext()) {
                                        Text(text = language.localizedName)
                                    }
                                    if (language != Language.System) {
                                        Text(
                                            text = language.localizedName,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.secondary
                                        )
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