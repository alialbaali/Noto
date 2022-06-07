package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.MainDialogFragmentBinding
import com.noto.app.util.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        MainDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupState()
            setupListeners()
        }

    private fun MainDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            tvDialogTitle.text = context.stringResource(R.string.app_name)
        }
    }

    private fun MainDialogFragmentBinding.setupState() {
        context?.let { context ->
            val backgroundRippleColor = context.colorAttributeResource(R.attr.notoSecondaryColor).toColorStateList()
            listOf(tvFoldersVault, tvFoldersArchive, tvSettings).forEach { tv ->
                tv.background.setRippleColor(backgroundRippleColor)
            }
        }
    }

    private fun MainDialogFragmentBinding.setupListeners() {
        tvFoldersArchive.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainArchiveFragment())
        }

        tvFoldersVault.setOnClickListener {
            /**
             * Temporary fix due to Koin creating multiple instances of [MainViewModel].
             * */
            viewModel.settingsVaultPasscode
                .onEach { passcode ->
                    dismiss()
                    if (passcode == null)
                        navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToVaultPasscodeDialogFragment())
                    else
                        navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainVaultFragment())
                }
                .launchIn(lifecycleScope)
//            dismiss()
//            if (viewModel.vaultPasscode.value == null)
//                navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToVaultPasscodeDialogFragment())
//            else
//                navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToMainVaultFragment())
        }

        tvSettings.setOnClickListener {
            dismiss()
            navController?.navigateSafely(MainDialogFragmentDirections.actionMainDialogFragmentToSettingsFragment())
        }
    }
}