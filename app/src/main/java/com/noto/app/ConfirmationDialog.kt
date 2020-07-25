package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.databinding.FragmentDialogConfirmationBinding

class ConfirmationDialogFragment(private val block: (dialogFragment: ConfirmationDialogFragment, dialogBinding: FragmentDialogConfirmationBinding) -> Unit) : BaseBottomSheetDialogFragment() {

    private val binding by lazy { FragmentDialogConfirmationBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        block(this, binding)
        if (binding.tvSubtitle.text.isNullOrBlank()) binding.tvSubtitle.visibility = View.GONE
        return binding.root
    }

}