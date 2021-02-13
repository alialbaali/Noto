package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.ConfirmationDialogFragmentBinding
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

class ConfirmationDialogFragment(
    private val dialogTitle: String? = null,
    private val block: (dialogFragment: ConfirmationDialogFragment, dialogBinding: ConfirmationDialogFragmentBinding) -> Unit
) : BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = ConfirmationDialogFragmentBinding.inflate(inflater, container, false).withBinding {

        BaseDialogFragmentBinding.bind(root).apply {
            tvDialogTitle.text = dialogTitle ?: stringResource(R.string.confirmation_dialog)
        }

        block(this@ConfirmationDialogFragment, this)

        if (tvSubtitle.text.isNullOrBlank())
            tvSubtitle.visibility = View.GONE
    }
}