package com.noto.app.folder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.fragment.navArgs
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.BottomSheetDialogItem
import com.noto.app.components.Group
import com.noto.app.domain.model.Grouping
import com.noto.app.domain.model.NoteListSortingType
import com.noto.app.toColor
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.toResource
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
        ComposeView(context).apply {
            setContent {
                val folder by viewModel.folder.collectAsState()

                BottomSheetDialog(title = stringResource(R.string.notes_view), headerColor = folder.color.toColor()) {
                    BottomSheetDialogItem(
                        text = stringResource(id = R.string.filtering),
                        onClick = {
                            navController?.navigateSafely(
                                NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListFilteringDialogFragment(
                                    args.folderId
                                )
                            )
                        },
                        painter = painterResource(id = R.drawable.ic_round_filtering_24),
                        value = stringResource(id = folder.filteringType.toResource()),
                        rippleColor = folder.color.toColor(),
                    )

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.medium))

                    Group {
                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.sorting),
                            onClick = {
                                navController?.navigateSafely(
                                    NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListSortingDialogFragment(
                                        args.folderId
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.ic_round_sorting_24),
                            value = stringResource(id = folder.sortingType.toResource()),
                            rippleColor = folder.color.toColor(),
                        )

                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.ordering),
                            onClick = {
                                navController?.navigateSafely(
                                    NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListOrderingDialogFragment(
                                        args.folderId,
                                        isSorting = true,
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.ic_round_ordering_24),
                            value = stringResource(id = folder.sortingOrder.toResource()),
                            enabled = folder.sortingType != NoteListSortingType.Manual,
                            rippleColor = folder.color.toColor(),
                        )
                    }

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.medium))

                    Group {
                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.grouping),
                            onClick = {
                                navController?.navigateSafely(
                                    NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListGroupingDialogFragment(
                                        args.folderId
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.ic_round_table_view_24),
                            value = stringResource(id = folder.grouping.toResource()),
                            rippleColor = folder.color.toColor(),
                        )

                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.ordering),
                            onClick = {
                                navController?.navigateSafely(
                                    NoteListViewDialogFragmentDirections.actionNoteListViewDialogFragmentToNoteListOrderingDialogFragment(
                                        args.folderId,
                                        isSorting = false,
                                    )
                                )
                            },
                            painter = painterResource(id = R.drawable.ic_round_ordering_24),
                            value = stringResource(id = folder.groupingOrder.toResource()),
                            enabled = folder.grouping != Grouping.None,
                            rippleColor = folder.color.toColor(),
                        )
                    }

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.extraLarge))

                    Button(
                        onClick = { dismiss() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = folder.color.toColor(),
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = PaddingValues(NotoTheme.dimensions.medium),
                    ) {
                        Text(text = stringResource(id = R.string.apply), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}