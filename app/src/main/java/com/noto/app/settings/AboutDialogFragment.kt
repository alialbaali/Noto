package com.noto.app.settings

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.noto.app.BaseDialogFragment
import com.noto.app.R
import com.noto.app.databinding.AboutDialogFragmentBinding
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.util.removeLinksUnderline
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

class AboutDialogFragment : BaseDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = AboutDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupState()
        setupListeners()
    }

    fun AboutDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root).apply {
        tvDialogTitle.text = resources.stringResource(R.string.about)
    }

    fun AboutDialogFragmentBinding.setupState() {
        tvAbout.removeLinksUnderline()
        tvAbout.movementMethod = LinkMovementMethod.getInstance()
    }

    fun AboutDialogFragmentBinding.setupListeners() {
        btnOkay.setOnClickListener {
            dismiss()
        }
    }
}