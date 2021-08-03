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

private const val PlayStoreUrl = "https://play.google.com/store/apps/details?id=com.noto"
private const val GithubUrl = "https://github.com/alialbaali/Noto"

class SettingsFragment : Fragment() {

    private lateinit var binding: SettingsFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = SettingsFragmentBinding.inflate(inflater, container, false)

        setupUI()
        setupListeners()

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        val version = requireContext()
            .packageManager
            ?.getPackageInfo(requireContext().packageName, 0)
            ?.versionName

        binding.tvVersion.text = resources.stringResource(R.string.version) + " $version"
    }

    private fun setupListeners() {
        binding.tb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvShareWithOthers.setOnClickListener {
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

        binding.tvViewCode.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GithubUrl))
            startActivity(intent)
        }

        binding.tvRateApp.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PlayStoreUrl))
            val chooser = Intent.createChooser(intent, resources.stringResource(R.string.open_with))
            startActivity(chooser)
        }
    }

}