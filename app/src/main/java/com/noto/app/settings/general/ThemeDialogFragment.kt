package com.noto.app.settings.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.SelectableDialogItem
import com.noto.app.domain.model.Theme
import com.noto.app.settings.SettingsViewModel
import com.noto.app.util.toStringResourceId
import org.koin.androidx.viewmodel.ext.android.viewModel

class ThemeDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                val themes = Theme.entries
                val selectedTheme by viewModel.theme.collectAsState()
                BottomSheetDialog(title = stringResource(id = R.string.theme)) {
                    themes.forEach { theme ->
                        SelectableDialogItem(
                            selected = selectedTheme == theme,
                            onClick = {
                                viewModel.updateTheme(theme)
                                dismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(text = stringResource(theme.toStringResourceId()))
                        }
                    }
                }
            }
        }
    }
}