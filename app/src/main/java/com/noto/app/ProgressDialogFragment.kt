package com.noto.app

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.noto.app.databinding.BaseDialogFragmentBinding
import com.noto.app.databinding.ProgressDialogFragmentBinding
import com.noto.app.util.withBinding

class ProgressDialogFragment : BaseDialogFragment() {

    private val args by navArgs<ProgressDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ProgressDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupBaseDialogFragment()
        setupState()
        isCancelable = false
    }

    private fun ProgressDialogFragmentBinding.setupBaseDialogFragment() = BaseDialogFragmentBinding.bind(root)
        .apply { tvDialogTitle.text = args.title }

    private fun ProgressDialogFragmentBinding.setupState() {
        progress.contentDescription = args.title
    }
}