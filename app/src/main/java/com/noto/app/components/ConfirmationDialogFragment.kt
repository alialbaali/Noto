package com.noto.app.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.noto.app.databinding.ConfirmationDialogFragmentBinding
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.withBinding

class ConfirmationDialogFragment : BaseDialogFragment() {

    private val args by navArgs<ConfirmationDialogFragmentArgs>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
        ConfirmationDialogFragmentBinding.inflate(inflater, container, false).withBinding {
            setupState()
            setupListeners()
        }

    private fun ConfirmationDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = args.btnText
        tvConfirmation.text = args.confirmation
        tvDescription.text = args.description
        btnConfirm.text = args.btnText
    }

    private fun ConfirmationDialogFragmentBinding.setupListeners() {
        btnConfirm.setOnClickListener {
            navController?.previousBackStackEntry?.savedStateHandle?.set(Constants.ClickListener, 0)
            dismiss()
        }
    }
}