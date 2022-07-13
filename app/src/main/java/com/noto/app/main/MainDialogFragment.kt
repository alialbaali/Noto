package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.MainDialogFragmentBinding
import com.noto.app.util.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

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

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Boolean>(Constants.IsPasscodeValid)
            ?.observe(viewLifecycleOwner) { isPasscodeValid ->
                if (isPasscodeValid) {
                    viewModel.openVault()
                    if (navController?.currentDestination?.id == R.id.validateVaultPasscodeDialogFragment) navController?.navigateUp()
                    navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainVaultFragment())
                }
            }
    }

    private fun MainDialogFragmentBinding.setupListeners() {
        tvFoldersArchive.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainArchiveFragment())
        }

        tvFoldersVault.setOnClickListener {
            when {
                viewModel.vaultPasscode.value == null -> navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToVaultPasscodeDialogFragment())
                viewModel.isVaultOpen.value -> navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainVaultFragment())
                else -> navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToValidateVaultPasscodeDialogFragment())
            }
        }

        tvSettings.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToSettingsFragment())
        }
    }
}