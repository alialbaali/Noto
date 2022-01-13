package com.noto.app.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.biometric.BiometricManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.noto.app.R
import com.noto.app.databinding.SettingsFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val PlayStoreUrl = "https://play.google.com/store/apps/details?id=com.noto"
private const val GithubUrl = "https://github.com/alialbaali/Noto"
private const val RedditUrl = "https://reddit.com/r/notoapp"
private const val GithubIssueUrl = "https://github.com/alialbaali/Noto/issues/new"

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        SettingsFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun SettingsFragmentBinding.setupState() {
        context?.let { context ->
            val version = context.packageManager
                ?.getPackageInfo(context.packageName, 0)
                ?.versionName
            tvVersion.text = context.stringResource(R.string.version, version)
            swShowNotesCount.setupColors()
            swBioAuth.setupColors()

            when (BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
                BiometricManager.BIOMETRIC_SUCCESS -> swBioAuth.isVisible = true
                else -> swBioAuth.isVisible = false
            }
        }

        viewModel.isShowNotesCount
            .onEach { isShowNotesCount -> swShowNotesCount.isChecked = isShowNotesCount }
            .launchIn(lifecycleScope)

        viewModel.isBioAuthEnabled
            .onEach { isBioAuthEnabled -> swBioAuth.isChecked = isBioAuthEnabled }
            .launchIn(lifecycleScope)

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(Constants.LibraryId)
            ?.observe(viewLifecycleOwner, viewModel::setHomeScreenId)
    }

    private fun SettingsFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }

        tvChangeMainLibrary .setOnClickListener {
            navController?.navigateSafely(
                SettingsFragmentDirections.actionSettingsFragmentToSelectLibraryDialogFragment(
                    libraryId = 0,
                    selectedLibraryId = viewModel.mainLibraryId.value
                )
            )
        }

        tvChangeTheme.setOnClickListener {
            navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToThemeDialogFragment())
        }

        tvChangeNotesFont.setOnClickListener {
            navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToFontDialogFragment())
        }

        tvChangeLanguage.setOnClickListener {
            navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToLanguageDialogFragment())
        }

        tvChangeVaultPasscode.setOnClickListener {
            navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToVaultPasscodeDialogFragment())
        }

        tvChangeVaultTimeout.setOnClickListener {
            navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToVaultTimeoutDialogFragment())
        }

        swBioAuth.setOnClickListener {
            viewModel.toggleIsBioAuthEnabled()
        }

        tvExportImport.setOnClickListener {
            navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToExportImportDialogFragment())
        }

        tvAbout.setOnClickListener {
            navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToAboutDialogFragment())
        }

        swShowNotesCount.setOnClickListener {
            viewModel.toggleShowNotesCount()
        }

        tvShareWithOthers.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "${context?.stringResource(R.string.invite_text)} $PlayStoreUrl"
                )
            }

            val chooser = Intent.createChooser(intent, context?.stringResource(R.string.share_with))
            startActivity(chooser)
        }

        tvViewCode.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GithubUrl))
            startActivity(intent)
        }

        tvJoinCommunity.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(RedditUrl))
            startActivity(intent)
        }

        tvRateApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PlayStoreUrl))
            val chooser = Intent.createChooser(intent, context?.stringResource(R.string.open_with))
            startActivity(chooser)
        }

        tvReportIssue.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GithubIssueUrl))
            startActivity(intent)
        }

        tvWhatsNew.setOnClickListener {
            navController?.navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToWhatsNewDialogFragment())
        }
    }
}