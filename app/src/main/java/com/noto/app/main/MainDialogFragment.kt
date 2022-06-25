package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.MainDialogFragmentBinding
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainDialogFragment : BaseDialogFragment() {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = MainDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
        setupListeners()
    }

    private fun MainDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.app_name)
    }

    private fun MainDialogFragmentBinding.setupListeners() {
        tvFoldersArchive.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainArchiveFragment())
        }

        tvFoldersVault.setOnClickListener {
            dismiss()
            if (viewModel.vaultPasscode.value == null)
                navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToVaultPasscodeDialogFragment())
            else
                navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainVaultFragment())
        }

        tvSettings.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToSettingsFragment())
        }
    }
}