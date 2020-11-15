package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.databinding.ConfirmationDialogFragmentBinding

class ConfirmationDialogFragment(private val block: (dialogFragment: ConfirmationDialogFragment, dialogBinding: ConfirmationDialogFragmentBinding) -> Unit) : BaseBottomSheetDialogFragment() {

    private lateinit var binding: ConfirmationDialogFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ConfirmationDialogFragmentBinding.inflate(inflater, container, false)

        block(this, binding)
        if (binding.tvSubtitle.text.isNullOrBlank()) binding.tvSubtitle.visibility = View.GONE
        return binding.root
    }

}