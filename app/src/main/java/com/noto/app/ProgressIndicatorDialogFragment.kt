package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.noto.app.databinding.ProgressIndicatorDialogFragmentBinding
import com.noto.app.util.withBinding

class ProgressIndicatorDialogFragment : BaseDialogFragment() {

    private val args by navArgs<ProgressIndicatorDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ProgressIndicatorDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
        isCancelable = false
    }

    private fun ProgressIndicatorDialogFragmentBinding.setupState() {
        tb.tvDialogTitle.text = args.title
        indicator.contentDescription = args.title
    }
}