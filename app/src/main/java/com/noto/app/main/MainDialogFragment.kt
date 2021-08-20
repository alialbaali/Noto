package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.MainDialogFragmentBinding
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

class MainDialogFragment : BaseDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        MainDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupListeners()
        }

    private fun MainDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = resources.stringResource(R.string.app_name)
    }

    private fun MainDialogFragmentBinding.setupListeners() {
        tvChangeLibrarySorting.setOnClickListener {
            dismiss()
            findNavController().navigate(MainDialogFragmentDirections.actionMainDialogFragmentToLibrarySortingDialogFragment())
        }

        tvChangeTheme.setOnClickListener {
            dismiss()
            findNavController().navigate(MainDialogFragmentDirections.actionMainDialogFragmentToThemeDialogFragment())
        }

        tvSettings.setOnClickListener {
            dismiss()
            findNavController().navigate(MainDialogFragmentDirections.actionMainDialogFragmentToSettingsFragment())
        }
    }
}