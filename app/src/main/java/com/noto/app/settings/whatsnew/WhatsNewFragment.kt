package com.noto.app.settings.whatsnew

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.HeaderItem
import com.noto.app.components.LazyScreen
import com.noto.app.domain.model.Release
import com.noto.app.settings.SettingsItem
import com.noto.app.settings.SettingsItemType
import com.noto.app.util.*
import kotlinx.datetime.Clock

private const val PreviousReleaseChangelogMaxItems = 5

class WhatsNewFragment : Fragment() {

    private val previousReleasesGroupedByYear by lazy { Release.Previous.groupBy { it.date.year } }

    private val currentDate = Clock.System.now().toLocalDate()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        activity?.onBackPressedDispatcher?.addCallback { navController?.navigateUp() }
        setupMixedTransitions()
        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                val currentRelease = remember(context) { Release.Current }

                LazyScreen(
                    title = stringResource(id = R.string.whats_new),
                    actions = { GitHubAction() },
                ) {
                    item("current") {
                        HeaderItem(title = context.stringResource(R.string.current))
                    }

                    item(key = currentRelease.versionFormatted) {
                        val version = remember(currentRelease) { currentRelease.versionFormatted }
                        val date = remember(currentRelease) { currentRelease.dateFormatted }
                        val changelog = remember(currentRelease) { currentRelease.changelog.format(context) }
                        SettingsItem(
                            title = version,
                            onClick = {
                                navController?.navigateSafely(
                                    WhatsNewFragmentDirections.actionWhatsNewFragmentToReleaseFragment(
                                        currentRelease.toJson()
                                    )
                                )
                            },
                            type = SettingsItemType.Text(date),
                            painter = painterResource(id = R.drawable.ic_round_auto_awesome_24),
                            equalWeights = false,
                            description = changelog,
                        )
                    }

                    previousReleasesGroupedByYear.forEach { (year, previousReleases) ->
                        val isCurrentYear = currentDate.year == year

                        item(key = if (isCurrentYear) "previous" else year.toString()) {
                            HeaderItem(title = if (isCurrentYear) context.stringResource(R.string.previous) else year.toString())
                        }

                        items(previousReleases, key = { it.versionFormatted }) { previousRelease ->
                            val version = remember(previousRelease) { previousRelease.versionFormatted }
                            val date = remember(previousRelease) { previousRelease.dateFormatted }
                            val changelog = remember(previousRelease) { previousRelease.changelog.format(context, PreviousReleaseChangelogMaxItems) }
                            SettingsItem(
                                title = version,
                                onClick = {
                                    navController?.navigateSafely(
                                        WhatsNewFragmentDirections.actionWhatsNewFragmentToReleaseFragment(
                                            previousRelease.toJson()
                                        )
                                    )
                                },
                                type = SettingsItemType.Text(date),
                                painter = painterResource(id = R.drawable.ic_round_tag_24),
                                equalWeights = false,
                                description = changelog,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun GitHubAction(modifier: Modifier = Modifier) {
        IconButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Constants.Noto.GitHubReleasesUrl))
                startActivity(intent)
            },
            modifier = modifier,
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_github_logo),
                contentDescription = stringResource(id = R.string.github)
            )
        }
    }
}