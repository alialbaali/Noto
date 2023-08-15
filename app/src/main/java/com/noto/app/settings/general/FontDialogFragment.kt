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
import com.noto.app.domain.model.Font
import com.noto.app.settings.SettingsViewModel
import com.noto.app.util.toStringResourceId
import org.koin.androidx.viewmodel.ext.android.viewModel

class FontDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                val fonts = Font.entries
                val selectedFont by viewModel.font.collectAsState()
                BottomSheetDialog(title = stringResource(id = R.string.notes_font)) {
                    fonts.forEach { font ->
                        SelectableDialogItem(
                            selected = selectedFont == font,
                            onClick = {
                                viewModel.updateFont(font)
                                dismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(text = stringResource(id = font.toStringResourceId()))
                        }
                    }
                }
            }
        }
    }
}