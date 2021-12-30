package com.noto.app.settings

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.WhatsNewDialogFragmentBinding
import com.noto.app.util.removeLinksUnderline
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

class WhatsNewDialogFragment : BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = WhatsNewDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupState()
        setupListeners()
    }

    private fun WhatsNewDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        context?.let { context ->
            tvDialogTitle.text = context.stringResource(R.string.whats_new)
        }
    }

    private fun WhatsNewDialogFragmentBinding.setupState() {
        tvAbout.removeLinksUnderline()
        tvAbout.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun WhatsNewDialogFragmentBinding.setupListeners() {
        btnOkay.setOnClickListener {
            dismiss()
        }
    }
}