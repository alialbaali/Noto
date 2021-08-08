package com.noto.app.librarylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.LibraryListDialogFragmentBinding
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

class LibraryListDialogFragment : BaseDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LibraryListDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupBaseDialogFragment()
            setupListeners()
        }

    private fun LibraryListDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = resources.stringResource(R.string.app_name)
    }

    private fun LibraryListDialogFragmentBinding.setupListeners() {
        tvChangeTheme.setOnClickListener {
            dismiss()
            findNavController().navigate(LibraryListDialogFragmentDirections.actionLibraryListDialogFragmentToThemeDialogFragment())
        }

        tvSettings.setOnClickListener {
            dismiss()
            findNavController().navigate(LibraryListDialogFragmentDirections.actionLibraryListDialogFragmentToSettingsFragment())
        }
    }
}