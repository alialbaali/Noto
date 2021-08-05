package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.ConfirmationDialogFragmentBinding
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import java.io.Serializable

class ConfirmationDialogFragment : BaseDialogFragment() {

    private val args by navArgs<ConfirmationDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ConfirmationDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = resources.stringResource(R.string.confirmation_dialog)
        }

        tvTitle.text = args.title
        btnConfirm.text = args.btnText
        btnConfirm.setOnClickListener {
            dismiss()
            args.clickListener.onClick()
        }

        if (args.description.isNullOrBlank())
            tvDescription.visibility = View.GONE
        else
            tvDescription.text = args.description
    }

    fun interface ConfirmationDialogClickListener : Serializable {
        fun onClick()
    }
}