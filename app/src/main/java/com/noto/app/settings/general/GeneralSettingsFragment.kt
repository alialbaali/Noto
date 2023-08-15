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
import com.noto.app.filtered.FilteredItemModel
import com.noto.app.settings.SettingsItem
import com.noto.app.settings.SettingsItemType
import com.noto.app.settings.SettingsSection
import com.noto.app.settings.SettingsViewModel
import com.noto.app.util.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import org.koin.androidx.viewmodel.ext.android.viewModel

class GeneralSettingsFragment : Fragment() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        setupMixedTransitions()

        activity?.onBackPressedDispatcher?.addCallback { navController?.navigateUp() }

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(Constants.MainInterfaceId)
            ?.observe(viewLifecycleOwner) { id -> viewModel.setMainInterfaceId(id) }

        navController?.currentBackStackEntry?.savedStateHandle
            ?.getLiveData<Long>(Constants.QuickNoteFolderId)
            ?.observe(viewLifecycleOwner) { id -> viewModel.setQuickNoteFolderId(id) }

        ComposeView(context).apply {
            isTransitionGroup = true
            setContent {
                val noneText = stringResource(id = R.string.none)
                val mainInterfaceId by viewModel.mainInterfaceId.collectAsState()
                val filteredItemModelId = remember(mainInterfaceId) {
                    FilteredItemModel.entries.firstOrNull { it.id == mainInterfaceId }?.toStringResourceId() ?: R.string.all_folders
                }
                val filteredItemModelText = stringResource(id = filteredItemModelId)
                val mainInterfaceText by produceState(initialValue = filteredItemModelText, mainInterfaceId) {
                    value = when (mainInterfaceId) {
                        in FilteredItemModel.Ids.plus(AllFoldersId) -> filteredItemModelText
                        else -> viewModel.getFolderById(mainInterfaceId).filterNotNull().firstOrNull()?.getTitle(context) ?: noneText
                    }
                }
                val theme by viewModel.theme.collectAsState()
                val themeId = remember(theme) { theme.toStringResourceId() }
                val themeText = stringResource(id = themeId)
                val language = remember { AppCompatDelegate.getApplicationLocales().toLanguages().first() }
                val languageId = remember(language) { language.toStringResourceId() }
                val languageText = stringResource(id = languageId)
                val icon by viewModel.icon.collectAsState()
                val iconId = remember(icon) { icon.toStringResourceId() }
                val iconText = stringResource(id = iconId)
                val font by viewModel.font.collectAsState()
                val fontId = remember(font) { font.toStringResourceId() }
                val fontText = stringResource(id = fontId)
                val notesCountEnabled by viewModel.isShowNotesCount.collectAsState()
                val rememberScrollingPositionEnabled by viewModel.isRememberScrollingPosition.collectAsState()
                val quickExit by viewModel.quickExit.collectAsState()
                val quickNoteFolderId by viewModel.quickNoteFolderId.collectAsState()
                val quickNoteFolderTitle by remember(quickNoteFolderId) {
                    viewModel.getFolderById(quickNoteFolderId)
                        .filterNotNull()
                        .map { it.getTitle(context) }
                }.collectAsState(initial = stringResource(id = R.string.general))
                val continuousSearch by viewModel.continuousSearch.collectAsState()
                val previewAutoScroll by viewModel.previewAutoScroll.collectAsState()

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
                                        isMainInterface = true,
                                        title = context.stringResource(R.string.main_interface),
                                        key = Constants.MainInterfaceId,
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
                                navController?.navigateSafely(
                                    GeneralSettingsFragmentDirections.actionGeneralSettingsFragmentToSelectFolderDialogFragment(
                                        longArrayOf(),
                                        selectedFolderId = quickNoteFolderId,
                                        title = context.stringResource(R.string.quick_note_folder),
                                        key = Constants.QuickNoteFolderId,
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