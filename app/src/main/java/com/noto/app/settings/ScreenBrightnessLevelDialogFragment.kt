package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.SelectableDialogItem
import com.noto.app.domain.model.ScreenBrightnessLevel
import com.noto.app.util.asString
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScreenBrightnessLevelDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                val levels = remember { ScreenBrightnessLevel.values() }
                val selectedLevel by viewModel.screenBrightnessLevel.collectAsState()
                BottomSheetDialog(title = stringResource(id = R.string.screen_brightness_level)) {
                    levels.forEach { level ->
                        SelectableDialogItem(
                            selected = selectedLevel == level,
                            onClick = {
                                viewModel.updateScreenBrightnessLevel(level)
                                dismiss()
                            },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(text = level.asString())
                                if (level != ScreenBrightnessLevel.System) {
                                    val percentage = remember(level) { level.value.times(100F).toInt().toString().plus("%") }
                                    Text(text = percentage, color = MaterialTheme.colorScheme.secondary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}