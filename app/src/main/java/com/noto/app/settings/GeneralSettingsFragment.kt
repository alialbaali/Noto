package com.noto.app.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.Screen
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Icon
import com.noto.app.domain.model.Language
import com.noto.app.domain.model.Theme
import com.noto.app.util.*
import kotlinx.coroutines.flow.first
import org.koin.androidx.viewmodel.ext.android.viewModel

class GeneralSettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        setupMixedTransitions()

        activity?.onBackPressedDispatcher?.addCallback {
            navController?.navigateUp()
        }

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(Constants.FolderId)
            ?.observe(viewLifecycleOwner, viewModel::setMainInterfaceId)

        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                val recentNotesText = stringResource(id = R.string.recent_notes)
                val allNotesText = stringResource(id = R.string.all_notes)
                val allFoldersText = stringResource(id = R.string.all_folders)
                val mainInterfaceId by viewModel.mainInterfaceId.collectAsState()
                var mainInterfaceText by remember { mutableStateOf(allFoldersText) }
                val theme by viewModel.theme.collectAsState()
                val themeText = when (theme) {
                    Theme.System -> stringResource(id = R.string.system_dark_theme)
                    Theme.SystemBlack -> stringResource(id = R.string.system_black_theme)
                    Theme.Light -> stringResource(id = R.string.light_theme)
                    Theme.Dark -> stringResource(id = R.string.dark_theme)
                    Theme.Black -> stringResource(id = R.string.black_theme)
                }
                val language by viewModel.language.collectAsState()
                val languageText = when (language) {
                    Language.System -> stringResource(id = R.string.system_language)
                    Language.English -> stringResource(id = R.string.english)
                    Language.Turkish -> stringResource(id = R.string.turkish)
                    Language.Arabic -> stringResource(id = R.string.arabic)
                    Language.Indonesian -> stringResource(id = R.string.indonesian)
                    Language.Russian -> stringResource(id = R.string.russian)
                    Language.Tamil -> null
                    Language.Spanish -> stringResource(id = R.string.spanish)
                    Language.French -> stringResource(id = R.string.french)
                    Language.German -> stringResource(id = R.string.german)
                }
                val icon by viewModel.icon.collectAsState()
                val iconText = when (icon) {
                    Icon.Futuristic -> stringResource(id = R.string.futuristic)
                    Icon.DarkRain -> stringResource(id = R.string.dark_rain)
                    Icon.Airplane -> stringResource(id = R.string.airplane)
                    Icon.BlossomIce -> stringResource(id = R.string.blossom_ice)
                    Icon.DarkAlpine -> stringResource(id = R.string.dark_alpine)
                    Icon.DarkSide -> stringResource(id = R.string.dark_side)
                    Icon.Earth -> stringResource(id = R.string.earth)
                    Icon.Fire -> stringResource(id = R.string.fire)
                    Icon.Purpleberry -> stringResource(id = R.string.purpleberry)
                    Icon.SanguineSun -> stringResource(id = R.string.sanguine_sun)
                }

                val font by viewModel.font.collectAsState()
                val fontText = when (font) {
                    Font.Nunito -> stringResource(id = R.string.nunito)
                    Font.Monospace -> stringResource(id = R.string.monospace)
                }

                val notesCountEnabled by viewModel.isShowNotesCount.collectAsState()
                val rememberScrollingPositionEnabled by viewModel.isRememberScrollingPosition.collectAsState()

                LaunchedEffect(key1 = mainInterfaceId) {
                    mainInterfaceText = when (mainInterfaceId) {
                        AllNotesItemId -> allNotesText
                        AllFoldersId -> allFoldersText
                        RecentNotesItemId -> recentNotesText
                        else -> viewModel.getFolderById(mainInterfaceId).first().getTitle(context)
                    }
                }

                Screen(title = stringResource(id = R.string.general)) {
                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.main_interface),
                            type = SettingsItemType.Text(mainInterfaceText),
                            onClick = {
                                navController?.navigateSafely(
                                    GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToSelectFolderDialogFragment(
                                        longArrayOf(),
                                        selectedFolderId = mainInterfaceId,
                                        isMainInterface = true
                                    )
                                )
                            },
                        )
                    }
                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.theme),
                            type = SettingsItemType.Text(themeText),
                            onClick = { navController?.navigateSafely(GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToThemeDialogFragment()) }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.language),
                            type = if (languageText != null) SettingsItemType.Text(languageText) else SettingsItemType.None,
                            onClick = { navController?.navigateSafely(GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToLanguageDialogFragment()) }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.icon),
                            type = SettingsItemType.Text(iconText),
                            onClick = { navController?.navigateSafely(GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToIconDialogFragment()) }
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.notes_font),
                            type = SettingsItemType.Text(fontText),
                            onClick = { navController?.navigateSafely(GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToFontDialogFragment()) }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.notes_count),
                            type = SettingsItemType.Switch(notesCountEnabled),
                            onClick = { viewModel.toggleShowNotesCount() }
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.remember_scrolling_position),
                            type = SettingsItemType.Switch(rememberScrollingPositionEnabled),
                            onClick = { viewModel.toggleRememberScrollingPosition() }
                        )
                    }
                }
            }
        }
    }
}