package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.MainDialogFragmentBinding
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

class MainDialogFragment : BaseDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        MainDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupListeners()
        }

    private fun MainDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            tvDialogTitle.text = context.stringResource(R.string.app_name)
        }
    }

    private fun MainDialogFragmentBinding.setupListeners() {
        tvLibrariesArchive.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainArchiveFragment())
        }

        tvLibrariesVault.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainVaultFragment())
        }

        tvChangeTheme.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToThemeDialogFragment())
        }

        tvSettings.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToSettingsFragment())
        }
    }
}