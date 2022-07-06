package com.noto.app.settings

import android.content.Intent
import android.net.Uri
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

private const val GithubUrl = "https://github.com/alialbaali/Noto"
private const val RedditUrl = "https://reddit.com/r/notoapp"

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
        tvSourceCode.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GithubUrl))
            startActivity(intent)
        }

        tvRedditCommunity.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(RedditUrl))
            startActivity(intent)
        }

        btnOkay.setOnClickListener {
            dismiss()
        }
    }
}