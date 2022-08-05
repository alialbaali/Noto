package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.databinding.WhatsNewFragmentBinding
import com.noto.app.domain.model.*
import com.noto.app.util.*

class WhatsNewFragment : Fragment() {

    private val releases: List<Release> by lazy {
        context?.let { context ->
            listOf(
                Release_2_1_5(Release.Changelog(context.stringResource(R.string.release_2_1_5))),
                Release_2_1_4(Release.Changelog(context.stringResource(R.string.release_2_1_4))),
                Release_2_1_3(Release.Changelog(context.stringResource(R.string.release_2_1_3))),
                Release_2_1_2(Release.Changelog(context.stringResource(R.string.release_2_1_2))),
                Release_2_1_1(Release.Changelog(context.stringResource(R.string.release_2_1_1))),
                Release_2_1_0(Release.Changelog(context.stringResource(R.string.release_2_1_0))),
                Release_2_0_1(Release.Changelog(context.stringResource(R.string.release_2_0_1))),
                Release_2_0_0(Release.Changelog(context.stringResource(R.string.release_2_0_0))),
                Release_1_8_0(Release.Changelog(context.stringResource(R.string.release_1_8_0))),
            )
        } ?: emptyList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = WhatsNewFragmentBinding.inflate(inflater, container, false).withBinding {
        setupMixedTransitions()
        setupState()
        setupListeners()
    }

    private fun WhatsNewFragmentBinding.setupState() {
        rv.edgeEffectFactory = BounceEdgeEffectFactory()
        rv.itemAnimator = VerticalListItemAnimator()
        rv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        rv.withModels {
            releases.forEach { release ->
                releaseItem {
                    id(release.version.toString())
                    release(release)
                }
            }
        }
    }

    private fun WhatsNewFragmentBinding.setupListeners() {
        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }

        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }
    }
}