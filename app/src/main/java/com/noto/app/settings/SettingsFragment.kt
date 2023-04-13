package com.noto.app.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.toFontFamily
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.setupMixedTransitions
import org.koin.androidx.viewmodel.ext.android.viewModel

const val SupportNotoUrl = "https://github.com/alialbaali/Noto#support"
val SupportNotoColor = Color(0xFFE57373)

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }
        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean?>(Constants.IsPasscodeValid)
            ?.run {
                observe(viewLifecycleOwner) { isPasscodeValid ->
                    if (isPasscodeValid == true) {
                        val currentDestinationId = navController?.currentDestination?.id
                        val isValidateDialog = currentDestinationId == R.id.validateVaultPasscodeDialogFragment
                        val isVaultDialog = currentDestinationId == R.id.vaultPasscodeDialogFragment
                        if (isValidateDialog || isVaultDialog) navController?.navigateUp()
                        navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToVaultSettingsFragment())
                        value = null
                    }
                }
            }
        setupMixedTransitions()
        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                Screen(title = stringResource(id = R.string.settings)) {
                    MainSection()
                    ExportImportSection()
                    ShareAndRateSection()
                    AboutSection()
                    Spacer(modifier = Modifier.weight(1F))
                    SupportNotoItem()
                }
            }
        }
    }

    @Composable
    private fun MainSection(modifier: Modifier = Modifier) {
        SettingsSection(modifier) {
            SettingsItem(
                title = stringResource(id = R.string.general),
                type = SettingsItemType.None,
                onClick = {
                    navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToGeneralSettingsFragment())
                },
                painter = painterResource(id = R.drawable.ic_round_settings_24),
            )

            SettingsItem(
                title = stringResource(id = R.string.reading_mode),
                type = SettingsItemType.None,
                onClick = {
                    navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToReadingModeSettingsFragment())
                },
                painter = painterResource(id = R.drawable.ic_round_reading_mode_24),
            )

            SettingsItem(
                title = stringResource(id = R.string.vault),
                type = SettingsItemType.None,
                onClick = {
                    if (viewModel.vaultPasscode.value == null) {
                        navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToVaultPasscodeDialogFragment())
                    } else {
                        navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToValidateVaultPasscodeDialogFragment())
                    }
                },
                painter = painterResource(id = R.drawable.ic_round_shield_24),
            )
        }
    }


    @Composable
    private fun ShareAndRateSection(modifier: Modifier = Modifier) {
        val shareContentText = stringResource(id = R.string.invite_text)
        val shareText = stringResource(id = R.string.share_with)
        val rateText = stringResource(id = R.string.open_with)
        SettingsSection(modifier) {
            SettingsItem(
                title = stringResource(id = R.string.share_app_with_others),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "$shareContentText ${Constants.Noto.PlayStoreUrl}"
                        )
                    }

                    val chooser = Intent.createChooser(intent, shareText)
                    startActivity(chooser)
                },
                painter = painterResource(id = R.drawable.ic_round_share_24),
            )

            SettingsItem(
                title = stringResource(id = R.string.rate_app_on_play_store),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Noto.PlayStoreUrl))
                    val chooser = Intent.createChooser(intent, rateText)
                    startActivity(chooser)
                },
                painter = painterResource(id = R.drawable.ic_round_rate_review_24),
            )
        }
    }

    @Composable
    private fun AboutSection(modifier: Modifier = Modifier) {
        SettingsSection(modifier) {
            SettingsItem(
                title = stringResource(id = R.string.whats_new),
                type = SettingsItemType.None,
                onClick = {
                    navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToWhatsNewFragment())
                },
                painter = painterResource(id = R.drawable.ic_round_updates_24),
            )

            SettingsItem(
                title = stringResource(id = R.string.report_issue),
                type = SettingsItemType.None,
                onClick = {
                    navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToReportIssueDialogFragment())
                },
                painter = painterResource(id = R.drawable.ic_round_report_problem_24),
            )

            SettingsItem(
                title = stringResource(id = R.string.about),
                type = SettingsItemType.None,
                onClick = {
                    navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToAboutSettingsFragment())
                },
                painter = painterResource(id = R.drawable.ic_round_info_24),
            )
        }
    }

    @Composable
    private fun ExportImportSection(modifier: Modifier = Modifier) {
        SettingsSection(modifier) {
            SettingsItem(
                title = stringResource(id = R.string.export_import_data),
                type = SettingsItemType.None,
                onClick = {
                    navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToExportImportDialogFragment())
                },
                painter = painterResource(id = R.drawable.ic_round_import_export_24),
            )
        }
    }

    @Composable
    private fun SupportNotoItem(modifier: Modifier = Modifier) {
        val interactionSource = remember { MutableInteractionSource() }
        val indication = rememberRipple(color = SupportNotoColor)
        val fontFamily = remember { Font(R.font.pacifico).toFontFamily() }
        val colorFilter = remember { ColorFilter.tint(SupportNotoColor) }

        Row(
            modifier
                .clip(MaterialTheme.shapes.small)
                .clickable(interactionSource, indication) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(SupportNotoUrl))
                    startActivity(intent)
                }
                .padding(NotoTheme.dimensions.medium)
                .wrapContentHeight()
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_round_favorite_24),
                contentDescription = stringResource(id = R.string.support_noto),
                colorFilter = colorFilter,
                modifier = Modifier.size(32.dp),
            )

            Spacer(modifier = Modifier.width(NotoTheme.dimensions.small))

            Text(
                text = stringResource(id = R.string.support_noto),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Thin),
                color = SupportNotoColor,
                fontFamily = fontFamily,
            )
        }
    }
}