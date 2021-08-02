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

class LibraryListDialogFragment : BaseDialogFragment() {

    private lateinit var binding: LibraryListDialogFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = LibraryListDialogFragmentBinding.inflate(inflater, container, false)
        BaseDialogFragmentBinding.bind(binding.root).apply {
            tvDialogTitle.text = resources.stringResource(R.string.app_name)
        }

        binding.tvChangeTheme.setOnClickListener {
            dismiss()
            findNavController().navigate(LibraryListDialogFragmentDirections.actionLibraryListDialogFragmentToThemeDialogFragment())
        }

        binding.tvSettings.setOnClickListener {
            dismiss()
        }

        return binding.root
    }
}