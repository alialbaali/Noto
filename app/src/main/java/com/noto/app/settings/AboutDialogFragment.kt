package com.noto.app.settings

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.AboutDialogFragmentBinding
import com.noto.app.util.removeLinksUnderline
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

class AboutDialogFragment : BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = AboutDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
        setupListeners()
    }

    private fun AboutDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = context?.stringResource(R.string.about)
        tvAbout.removeLinksUnderline()
        tvAbout.movementMethod = LinkMovementMethod.getInstance()
    }

    private fun AboutDialogFragmentBinding.setupListeners() {
        btnOkay.setOnClickListener {
            dismiss()
        }
    }
}