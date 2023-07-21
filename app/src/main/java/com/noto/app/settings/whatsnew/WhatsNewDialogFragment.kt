package com.noto.app.settings.whatsnew

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.TelegramBanner
import com.noto.app.domain.model.Release
import com.noto.app.settings.SettingsItem
import com.noto.app.settings.SettingsItemType
import com.noto.app.settings.SettingsSection
import com.noto.app.settings.SettingsViewModel
import com.noto.app.util.Current
import org.koin.androidx.viewmodel.ext.android.viewModel

class WhatsNewDialogFragment : BaseDialogFragment(isCollapsable = true) {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        ComposeView(context).apply {
            setContent {
                val currentRelease = remember(context) { Release.Current(context) }
                val version = remember(currentRelease) { currentRelease.versionFormatted }
                val changelog = remember(currentRelease) { currentRelease.changelog.changesIds }
                BottomSheetDialog(
                    title = stringResource(id = R.string.whats_new_in, version),
                    painter = painterResource(id = R.drawable.ic_round_auto_awesome_24),
                ) {
                    TelegramBanner()

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.extraLarge))

                    SettingsSection {
                        changelog.forEach { id ->
                            SettingsItem(
                                title = stringResource(id = id),
                                type = SettingsItemType.None,
                                painter = painterResource(id = R.drawable.ic_round_check_24),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.extraLarge))

                    ElevatedButton(
                        onClick = {
                            viewModel.updateLastVersion().invokeOnCompletion {
                                dismiss()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        contentPadding = PaddingValues(NotoTheme.dimensions.medium),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        )
                    ) {
                        Text(text = stringResource(id = R.string.okay), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.updateLastVersion()
        super.onDismiss(dialog)
    }
}