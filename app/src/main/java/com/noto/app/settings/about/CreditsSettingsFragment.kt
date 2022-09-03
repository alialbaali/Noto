package com.noto.app.settings.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.settings.SettingsItem
import com.noto.app.settings.SettingsItemType
import com.noto.app.settings.SettingsSection
import com.noto.app.util.navController
import com.noto.app.util.setupMixedTransitions

private const val CollectionOfGradientsWebsite = "https://www.figma.com/community/file/830405806109119447"
private const val CreatorWebsite = "https://geoffreycrofte.com"
private const val AppIconsLicenseWebsite = "https://creativecommons.org/licenses/by/4.0/"

class CreditsSettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        setupMixedTransitions()

        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }

        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                Screen(title = stringResource(id = R.string.credits)) {
                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.app_icons),
                            type = SettingsItemType.Text(stringResource(id = R.string.app_icons_value)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CollectionOfGradientsWebsite))
                                startActivity(intent)
                            },
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.creator),
                            type = SettingsItemType.Text(stringResource(id = R.string.creator_name)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(CreatorWebsite))
                                startActivity(intent)
                            },
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.license),
                            type = SettingsItemType.Text(stringResource(id = R.string.app_icons_license_value)),
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(AppIconsLicenseWebsite))
                                startActivity(intent)
                            },
                        )
                    }
                }
            }
        }
    }
}