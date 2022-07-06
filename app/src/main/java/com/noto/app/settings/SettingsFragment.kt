package com.noto.app.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.NotoTopAppbar
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.setupMixedTransitions
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val PlayStoreUrl = "https://play.google.com/store/apps/details?id=com.noto"
private const val GithubIssueUrl = "https://github.com/alialbaali/Noto/issues/new"

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }
        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>(Constants.IsPasscodeValid)
            ?.observe(viewLifecycleOwner) { isPasscodeValid ->
                if (isPasscodeValid) {
                    if (navController?.currentDestination?.id == R.id.validateVaultPasscodeDialogFragment) navController?.navigateUp()
                    navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToVaultSettingsFragment())
                }
            }
        setupMixedTransitions()
        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                val scrollState = rememberScrollState()
                val theme by viewModel.theme.collectAsState()
                val version = context.packageManager?.getPackageInfo(context.packageName, 0)?.versionName
                NotoTheme(theme = theme) {
                    Scaffold(
                        Modifier.fillMaxSize(),
                        topBar = {
                            NotoTopAppbar(
                                title = stringResource(id = R.string.settings),
                                onNavigationIconClick = { navController?.navigateUp() },
                            )
                        }
                    ) { contentPadding ->
                        Column(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(contentPadding),
                        ) {
                            FirstSection(
                                paddingValues = PaddingValues(
                                    start = NotoTheme.dimensions.medium,
                                    top = NotoTheme.dimensions.medium,
                                    end = NotoTheme.dimensions.medium,
                                    bottom = NotoTheme.dimensions.small,
                                )
                            )
                            SecondSection(
                                paddingValues = PaddingValues(
                                    start = NotoTheme.dimensions.medium,
                                    top = NotoTheme.dimensions.small,
                                    end = NotoTheme.dimensions.medium,
                                    bottom = NotoTheme.dimensions.small,
                                )
                            )
                            ThirdSection(
                                paddingValues = PaddingValues(
                                    start = NotoTheme.dimensions.medium,
                                    top = NotoTheme.dimensions.small,
                                    end = NotoTheme.dimensions.medium,
                                    bottom = NotoTheme.dimensions.small,
                                )
                            )
                            ForthSection(
                                paddingValues = PaddingValues(
                                    start = NotoTheme.dimensions.medium,
                                    top = NotoTheme.dimensions.small,
                                    end = NotoTheme.dimensions.medium,
                                    bottom = NotoTheme.dimensions.medium,
                                )
                            )

                            version?.let {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.CenterHorizontally)
                                        .padding(NotoTheme.dimensions.medium)
                                        .weight(1F),
                                    contentAlignment = Alignment.BottomCenter
                                ) {
                                    Text(
                                        text = stringResource(R.string.version, version),
                                        style = MaterialTheme.typography.labelLarge,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun FirstSection(paddingValues: PaddingValues, modifier: Modifier = Modifier) {
        SettingsSection(modifier, paddingValues) {
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
                painter = painterResource(id = R.drawable.ic_round_inventory_24),
            )
        }
    }

    @Composable
    private fun ThirdSection(paddingValues: PaddingValues,modifier: Modifier = Modifier) {
        val shareContentText = stringResource(id = R.string.invite_text)
        val shareText = stringResource(id = R.string.share_with)
        val rateText = stringResource(id = R.string.open_with)
        SettingsSection(modifier, paddingValues) {
            SettingsItem(
                title = stringResource(id = R.string.share_app_with_others),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "$shareContentText $PlayStoreUrl"
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
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PlayStoreUrl))
                    val chooser = Intent.createChooser(intent, rateText)
                    startActivity(chooser)
                },
                painter = painterResource(id = R.drawable.ic_round_star_rate_24),
            )
        }
    }

    @Composable
    private fun ForthSection(paddingValues: PaddingValues, modifier: Modifier = Modifier) {
        SettingsSection(modifier, paddingValues) {
            SettingsItem(
                title = stringResource(id = R.string.report_issue),
                type = SettingsItemType.None,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GithubIssueUrl))
                    startActivity(intent)
                },
                painter = painterResource(id = R.drawable.ic_round_report_problem_24),
            )

            SettingsItem(
                title = stringResource(id = R.string.whats_new),
                type = SettingsItemType.None,
                onClick = {
                    navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToWhatsNewDialogFragment())
                },
                painter = painterResource(id = R.drawable.ic_round_new_releases_24),
            )

            SettingsItem(
                title = stringResource(id = R.string.about),
                type = SettingsItemType.None,
                onClick = {
                    navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToAboutDialogFragment())
                },
                painter = painterResource(id = R.drawable.ic_round_info_24),
            )
        }
    }

    @Composable
    private fun SecondSection(paddingValues: PaddingValues, modifier: Modifier = Modifier) {
        SettingsSection(modifier, paddingValues) {
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
}