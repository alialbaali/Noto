package com.noto.app.settings.general

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import com.noto.app.R
import com.noto.app.components.EmptyPainter
import com.noto.app.components.Screen
import com.noto.app.domain.model.Font
import com.noto.app.domain.model.Icon
import com.noto.app.domain.model.Theme
import com.noto.app.filtered.FilteredItemModel
import com.noto.app.settings.*
import com.noto.app.util.*
import kotlinx.coroutines.flow.firstOrNull
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
            ?.observe(viewLifecycleOwner) { id ->
                when (viewModel.folderIdType) {
                    FolderIdType.MainInterface -> viewModel.setMainInterfaceId(id)
                    FolderIdType.QuickNote -> viewModel.setQuickNoteFolderId(id)
                }
            }

        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                val allFoldersText = stringResource(id = R.string.all_folders)
                val allNotesText = stringResource(id = R.string.all)
                val archivedText = stringResource(id = R.string.archived)
                val recentNotesText = stringResource(id = R.string.recent)
                val scheduledText = stringResource(id = R.string.scheduled)
                val noneText = stringResource(id = R.string.none)
                val mainInterfaceId by viewModel.mainInterfaceId.collectAsState()
                val mainInterfaceText by produceState(initialValue = allFoldersText, mainInterfaceId) {
                    value = when (mainInterfaceId) {
                        AllFoldersId -> allFoldersText
                        FilteredItemModel.All.id -> allNotesText
                        FilteredItemModel.Recent.id -> recentNotesText
                        FilteredItemModel.Scheduled.id -> scheduledText
                        FilteredItemModel.Archived.id -> archivedText
                        else -> viewModel.getFolderById(mainInterfaceId).firstOrNull()?.getTitle(context) ?: noneText
                    }
                }
                val theme by viewModel.theme.collectAsState()
                val themeText = when (theme) {
                    Theme.System -> stringResource(id = R.string.system_dark_theme)
                    Theme.SystemBlack -> stringResource(id = R.string.system_black_theme)
                    Theme.Light -> stringResource(id = R.string.light_theme)
                    Theme.Dark -> stringResource(id = R.string.dark_theme)
                    Theme.Black -> stringResource(id = R.string.black_theme)
                }
                val language = remember { AppCompatDelegate.getApplicationLocales().toLanguages().first() }
                val languageText = remember(context, language) { context.stringResource(language.toResource()) }
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
                val quickExit by viewModel.quickExit.collectAsState()
                val quickNoteFolderId by viewModel.quickNoteFolderId.collectAsState()
                val quickNoteFolderTitle by produceState(stringResource(id = R.string.general), quickNoteFolderId) {
                    viewModel.getFolderById(quickNoteFolderId)
                        .collect { value = it.getTitle(context) }
                }
                val continuousSearch by viewModel.continuousSearch.collectAsState()
                val previewAutoScroll by viewModel.previewAutoScroll.collectAsState()

                Screen(title = stringResource(id = R.string.general)) {
                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.main_interface),
                            type = SettingsItemType.Text(mainInterfaceText),
                            onClick = {
                                viewModel.setFolderIdType(FolderIdType.MainInterface)
                                navController?.navigateSafely(
                                    GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToSelectFolderDialogFragment(
                                        longArrayOf(),
                                        selectedFolderId = mainInterfaceId,
                                        isMainInterface = true,
                                        title = context.stringResource(R.string.main_interface),
                                    )
                                )
                            },
                            description = stringResource(id = R.string.main_interface_description),
                            painter = painterResource(id = R.drawable.ic_round_home_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.quick_note_folder),
                            type = SettingsItemType.Text(quickNoteFolderTitle),
                            onClick = {
                                viewModel.setFolderIdType(FolderIdType.QuickNote)
                                navController?.navigateSafely(
                                    GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToSelectFolderDialogFragment(
                                        longArrayOf(),
                                        selectedFolderId = quickNoteFolderId,
                                        title = context.stringResource(R.string.quick_note_folder)
                                    )
                                )
                            },
                            description = stringResource(id = R.string.quick_note_folder_description),
                            painter = painterResource(id = R.drawable.ic_round_folder_24),
                        )
                    }
                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.theme),
                            type = SettingsItemType.Text(themeText),
                            onClick = { navController?.navigateSafely(GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToThemeDialogFragment()) },
                            painter = painterResource(id = R.drawable.ic_round_theme_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.language),
                            type = SettingsItemType.Text(languageText),
                            onClick = { navController?.navigateSafely(GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToLanguageDialogFragment()) },
                            painter = painterResource(id = R.drawable.ic_round_language_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.icon),
                            type = SettingsItemType.Text(iconText),
                            onClick = { navController?.navigateSafely(GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToIconDialogFragment()) },
                            painter = painterResource(id = R.drawable.ic_round_noto_24)
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.show_notes_count),
                            type = SettingsItemType.Switch(notesCountEnabled),
                            onClick = { viewModel.toggleShowNotesCount() },
                            description = stringResource(id = R.string.show_notes_count_description),
                            painter = painterResource(id = R.drawable.ic_round_tag_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.remember_scrolling_position),
                            type = SettingsItemType.Switch(rememberScrollingPositionEnabled),
                            onClick = { viewModel.toggleRememberScrollingPosition() },
                            description = stringResource(id = R.string.remember_scrolling_position_description),
                            painter = EmptyPainter,
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.quick_exit),
                            type = SettingsItemType.Switch(quickExit),
                            onClick = { viewModel.toggleQuickExit() },
                            description = stringResource(id = R.string.quick_exit_description),
                            painter = painterResource(id = R.drawable.ic_round_quick_exit_24),
                        )
                    }

                    SettingsSection {
                        SettingsItem(
                            title = stringResource(id = R.string.notes_font),
                            type = SettingsItemType.Text(fontText),
                            onClick = { navController?.navigateSafely(GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToFontDialogFragment()) },
                            painter = painterResource(id = R.drawable.ic_round_font_24),
                        )

                        SettingsItem(
                            title = stringResource(id = R.string.continuous_search),
                            type = SettingsItemType.Switch(continuousSearch),
                            onClick = { viewModel.toggleContinuousSearch() },
                            description = stringResource(id = R.string.continuous_search_description),
                            painter = painterResource(id = R.drawable.ic_round_continuous_search_24),
                        )


                        SettingsItem(
                            title = stringResource(id = R.string.preview_auto_scroll),
                            type = SettingsItemType.Switch(previewAutoScroll),
                            onClick = { viewModel.togglePreviewAutoScroll() },
                            description = stringResource(id = R.string.preview_auto_scroll_description),
                            painter = painterResource(id = R.drawable.ic_round_carousel_24),
                        )
                    }
                }
            }
        }
    }
}