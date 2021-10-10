package com.noto.app.settings

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.noto.app.R
import com.noto.app.databinding.SettingsFragmentBinding
import com.noto.app.util.colorResource
import com.noto.app.util.navigateSafely
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

private const val PlayStoreUrl = "https://play.google.com/store/apps/details?id=com.noto"
private const val GithubUrl = "https://github.com/alialbaali/Noto"
private const val GithubIssueUrl = "https://github.com/alialbaali/Noto/issues/new"

class SettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        SettingsFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun SettingsFragmentBinding.setupState() {
        val version = requireContext()
            .packageManager
            ?.getPackageInfo(requireContext().packageName, 0)
            ?.versionName

        tvVersion.text = resources.stringResource(R.string.version, version)

        val switchOffColor = ColorUtils.setAlphaComponent(resources.colorResource(R.color.colorSecondary), 128)
        val state = arrayOf(
            intArrayOf(android.R.attr.state_checked),
            intArrayOf(-android.R.attr.state_checked),
        )
        val switchThumbTintList = ColorStateList(
            state,
            intArrayOf(resources.colorResource(R.color.colorPrimary), resources.colorResource(R.color.colorSurface))
        )
        val switchTrackTintList = ColorStateList(
            state,
            intArrayOf(ColorUtils.setAlphaComponent(resources.colorResource(R.color.colorPrimary), 128), switchOffColor)
        )
        swShowNotesCount.thumbTintList = switchThumbTintList
        swShowNotesCount.trackTintList = switchTrackTintList

        viewModel.isShowNotesCount
            .onEach { isShowNotesCount -> swShowNotesCount.isChecked = isShowNotesCount }
            .launchIn(lifecycleScope)
    }

    private fun SettingsFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        tvChangeTheme.setOnClickListener {
            findNavController().navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToThemeDialogFragment())
        }

        tvChangeNotesFont.setOnClickListener {
            findNavController().navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToFontDialogFragment())
        }

        tvChangeLanguage.setOnClickListener {
            findNavController().navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToLanguageDialogFragment())
        }

        tvAbout.setOnClickListener {
            findNavController().navigateSafely(SettingsFragmentDirections.actionSettingsFragmentToAboutDialogFragment())
        }

        swShowNotesCount.setOnClickListener {
            viewModel.toggleShowNotesCount()
        }

        tvShareWithOthers.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(
                    Intent.EXTRA_TEXT,
                    "${resources.stringResource(R.string.invite_text)} $PlayStoreUrl"
                )
            }

            val chooser = Intent.createChooser(intent, resources.stringResource(R.string.share_with))
            startActivity(chooser)
        }

        tvViewCode.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GithubUrl))
            startActivity(intent)
        }

        tvRateApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PlayStoreUrl))
            val chooser = Intent.createChooser(intent, resources.stringResource(R.string.open_with))
            startActivity(chooser)
        }

        tvReportIssue.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GithubIssueUrl))
            startActivity(intent)
        }
    }
}