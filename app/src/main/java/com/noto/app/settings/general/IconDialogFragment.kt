package com.noto.app.settings.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.noto.app.R
import com.noto.app.components.AndroidIcon
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.SelectableDialogItem
import com.noto.app.domain.model.Icon
import com.noto.app.settings.SettingsViewModel
import com.noto.app.theme.NotoTheme
import com.noto.app.util.toDrawableResourceId
import com.noto.app.util.toStringResourceId
import org.koin.androidx.viewmodel.ext.android.viewModel

private val IconSize = 64.dp
private val IconFontSize = 18.sp

class IconDialogFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                val icons = Icon.entries
                val selectedIcon by viewModel.icon.collectAsState()
                BottomSheetDialog(title = stringResource(id = R.string.icon)) {
                    icons.forEach { icon ->
                        SelectableDialogItem(
                            selected = selectedIcon == icon,
                            onClick = {
                                viewModel.updateIcon(icon)
                                dismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(NotoTheme.dimensions.medium),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AndroidIcon(
                                    id = icon.toDrawableResourceId(),
                                    contentDescription = stringResource(id = icon.toStringResourceId()),
                                    modifier = Modifier.size(IconSize),
                                )
                                Text(
                                    text = stringResource(id = icon.toStringResourceId()),
                                    modifier = Modifier.weight(1F),
                                    fontSize = IconFontSize,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}