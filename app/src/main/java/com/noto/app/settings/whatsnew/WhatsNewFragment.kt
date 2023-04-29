package com.noto.app.settings.whatsnew

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.noto.app.R
import com.noto.app.databinding.WhatsNewFragmentBinding
import com.noto.app.domain.model.Release
import com.noto.app.domain.model.Release_1_8_0
import com.noto.app.domain.model.Release_2_0_0
import com.noto.app.domain.model.Release_2_0_1
import com.noto.app.domain.model.Release_2_1_0
import com.noto.app.domain.model.Release_2_1_1
import com.noto.app.domain.model.Release_2_1_2
import com.noto.app.domain.model.Release_2_1_3
import com.noto.app.domain.model.Release_2_1_4
import com.noto.app.domain.model.Release_2_1_5
import com.noto.app.domain.model.Release_2_1_6
import com.noto.app.domain.model.Release_2_2_0
import com.noto.app.domain.model.Release_2_2_1
import com.noto.app.domain.model.Release_2_2_2
import com.noto.app.domain.model.Release_2_2_3
import com.noto.app.util.BounceEdgeEffectFactory
import com.noto.app.util.VerticalListItemAnimator
import com.noto.app.util.navController
import com.noto.app.util.setupMixedTransitions
import com.noto.app.util.stringResource
import com.noto.app.util.withBinding

private const val GitHubReleasesUrl = "https://github.com/alialbaali/Noto/releases"

class WhatsNewFragment : Fragment() {

    private val releases: List<Release> by lazy {
        context?.let { context ->
            listOf(
                Release_2_2_3(Release.Changelog(context.stringResource(R.string.release_2_2_3))),
                Release_2_2_2(Release.Changelog(context.stringResource(R.string.release_2_2_2))),
                Release_2_2_1(Release.Changelog(context.stringResource(R.string.release_2_2_1))),
                Release_2_2_0(Release.Changelog(context.stringResource(R.string.release_2_2_0))),
                Release_2_1_6(Release.Changelog(context.stringResource(R.string.release_2_1_6))),
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

        tvMoreDetails.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(GitHubReleasesUrl))
            startActivity(intent)
        }

        tb.setOnClickListener {
            rv.smoothScrollToPosition(0)
        }

        tb.setNavigationOnClickListener {
            navController?.navigateUp()
        }
    }
}