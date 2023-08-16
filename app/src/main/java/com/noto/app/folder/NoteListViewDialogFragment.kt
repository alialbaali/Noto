package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.fragment.navArgs
import com.noto.app.R
import com.noto.app.components.*
import com.noto.app.domain.model.*
import com.noto.app.theme.NotoTheme
import com.noto.app.theme.toColor
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.toStringResourceId
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class NoteListViewDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<FolderViewModel> { parametersOf(args.folderId) }

    private val args by navArgs<NoteListViewDialogFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        val navController = navController
        val savedStateHandle = navController?.currentBackStackEntry?.savedStateHandle

        ComposeView(context).apply {
            if (navController == null || savedStateHandle == null) return@apply

            setContent {
                val folder by viewModel.folder.collectAsState()
                val filteringType by savedStateHandle.getStateFlow<FilteringType?>(key = Constants.FilteringType, initialValue = null)
                    .collectAsState()
                val sortingType by savedStateHandle.getStateFlow<NoteListSortingType?>(key = Constants.SortingType, initialValue = null)
                    .collectAsState()
                val sortingOrder by savedStateHandle.getStateFlow<SortingOrder?>(key = Constants.SortingOrder, initialValue = null)
                    .collectAsState()
                val groupingType by savedStateHandle.getStateFlow<Grouping?>(key = Constants.GroupingType, initialValue = null)
                    .collectAsState()
                val groupingOrder by savedStateHandle.getStateFlow<GroupingOrder?>(key = Constants.GroupingOrder, initialValue = null)
                    .collectAsState()

                BottomSheetDialog(title = stringResource(R.string.notes_view), headerColor = folder.color.toColor()) {
                    BottomSheetDialogItem(
                        text = stringResource(id = R.string.filtering),
                        onClick = {
                            navController.navigateSafely(
                                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListFilteringDialogFragment(
                                    args.folderId
                                )
                            )
                        },
                        painter = painterResource(id = R.drawable.ic_round_filtering_24),
                        value = stringResource(id = filteringType?.toStringResourceId() ?: folder.filteringType.toStringResourceId()),
                        rippleColor = folder.color.toColor(),
                    )

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.medium))

                    Group {
                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.sorting),
                            onClick = {
                                navController.navigateSafely(
                                    NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListSortingDialogFragment(
                                        args.folderId
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.ic_round_sorting_24),
                            value = stringResource(id = sortingType?.toStringResourceId() ?: folder.sortingType.toStringResourceId()),
                            rippleColor = folder.color.toColor(),
                        )

                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.ordering),
                            onClick = {
                                navController.navigateSafely(
                                    NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListOrderingDialogFragment(
                                        args.folderId,
                                        isSorting = true,
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.ic_round_ordering_24),
                            value = stringResource(id = sortingOrder?.toStringResourceId() ?: folder.sortingOrder.toStringResourceId()),
                            enabled = (sortingType ?: folder.sortingType) != NoteListSortingType.Manual,
                            rippleColor = folder.color.toColor(),
                        )
                    }

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.medium))

                    Group {
                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.grouping),
                            onClick = {
                                navController.navigateSafely(
                                    NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListGroupingDialogFragment(
                                        args.folderId
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.ic_round_table_view_24),
                            value = stringResource(id = groupingType?.toStringResourceId() ?: folder.grouping.toStringResourceId()),
                            rippleColor = folder.color.toColor(),
                        )

                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.ordering),
                            onClick = {
                                navController.navigateSafely(
                                    NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListOrderingDialogFragment(
                                        args.folderId,
                                        isSorting = false,
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.ic_round_ordering_24),
                            value = stringResource(id = groupingOrder?.toStringResourceId() ?: folder.groupingOrder.toStringResourceId()),
                            enabled = (groupingType ?: folder.grouping) != Grouping.None,
                            rippleColor = folder.color.toColor(),
                        )
                    }

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.extraLarge))

                    Button(
                        text = stringResource(id = R.string.apply),
                        onClick = {
                            viewModel.updateFolderNotesView(
                                filteringType = filteringType ?: folder.filteringType,
                                sortingType = sortingType ?: folder.sortingType,
                                sortingOrder = sortingOrder ?: folder.sortingOrder,
                                groupingType = groupingType ?: folder.grouping,
                                groupingOrder = groupingOrder ?: folder.groupingOrder,
                            ).invokeOnCompletion { dismiss() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = folder.color.toColor(),
                    )
                }
            }
        }
    }
}