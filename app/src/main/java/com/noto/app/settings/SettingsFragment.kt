package com.noto.app.settings

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.noto.app.R
import com.noto.app.databinding.SettingsFragmentBinding
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

private const val PlayStoreUrl = "https://play.google.com/store/apps/details?id=com.noto"
private const val GithubUrl = "https://github.com/alialbaali/Noto"

class SettingsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        SettingsFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    @SuppressLint("SetTextI18n")
    private fun SettingsFragmentBinding.setupState() {
        val version = requireContext()
            .packageManager
            ?.getPackageInfo(requireContext().packageName, 0)
            ?.versionName

        tvVersion.text = resources.stringResource(R.string.version) + " $version"
    }

    private fun SettingsFragmentBinding.setupListeners() {
        tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        tvChangeTheme.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToThemeDialogFragment())
        }

        tvChangeNotesFont.setOnClickListener {
            findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToFontDialogFragment())
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
    }
}