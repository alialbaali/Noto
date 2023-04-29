package com.noto.app.settings.whatsnew

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.databinding.WhatsNewDialogFragmentBinding
import com.noto.app.domain.model.Release
import com.noto.app.domain.model.Release.Changelog
import com.noto.app.domain.model.Release_2_2_3
import com.noto.app.settings.SettingsViewModel
import com.noto.app.util.BounceEdgeEffectFactory
import com.noto.app.util.VerticalListItemAnimator
import com.noto.app.util.isScrollingAsFlow
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.viewmodel.ext.android.viewModel

class WhatsNewDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    private val currentRelease: Release? by lazy {
        context?.let { context ->
            val changelog = Changelog(context.stringResource(R.string.release_2_2_3))
            Release_2_2_3(changelog)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = WhatsNewDialogFragmentBinding.inflate(inflater, container, false).withBinding {
        setupState()
        setupListeners()
    }

    private fun WhatsNewDialogFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        tb.tvDialogTitle.text = context?.stringResource(R.string.whats_new)

        rv.withModels {
            currentRelease?.let { currentRelease ->
                releaseItem {
                    id(currentRelease.version.toString())
                    release(currentRelease)
                }
            }
        }

        rv.isScrollingAsFlow()
            .onEach { isScrolling -> tb.root.isSelected = isScrolling }
            .launchIn(lifecycleScope)
    }

    private fun WhatsNewDialogFragmentBinding.setupListeners() {
        btnOkay.setOnClickListener {
            viewModel.updateLastVersion().invokeOnCompletion {
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.updateLastVersion()
        super.onDismiss(dialog)
    }
}