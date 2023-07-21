package com.noto.app.main

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
import com.noto.app.NotoTheme
import com.noto.app.R
import com.noto.app.components.*
import com.noto.app.domain.model.FolderListSortingType
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.navigateSafely
import com.noto.app.util.toResource
import org.koin.androidx.viewmodel.ext.android.viewModel

class FolderListViewDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

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
                val sortingType by viewModel.sortingType.collectAsState()
                val sortingOrder by viewModel.sortingOrder.collectAsState()
                val updatedSortingType by savedStateHandle.getStateFlow<FolderListSortingType?>(key = Constants.SortingType, initialValue = null)
                    .collectAsState()
                val updatedSortingOrder by savedStateHandle.getStateFlow<SortingOrder?>(key = Constants.SortingOrder, initialValue = null)
                    .collectAsState()

                BottomSheetDialog(title = stringResource(R.string.folders_view)) {
                    Group {
                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.sorting),
                            onClick = {
                                navController.navigateSafely(FolderListViewDialogFragmentDirections.actionFolderListViewDialogFragmentToFolderListSortingDialogFragment())
                            },
                            painter = painterResource(id = R.drawable.ic_round_sorting_24),
                            value = stringResource(id = updatedSortingType?.toResource() ?: sortingType.toResource()),
                        )

                        BottomSheetDialogItem(
                            text = stringResource(id = R.string.ordering),
                            onClick = {
                                navController.navigateSafely(FolderListViewDialogFragmentDirections.actionFolderListViewDialogFragmentToFolderListOrderingDialogFragment())
                            },
                            painter = painterResource(id = R.drawable.ic_round_ordering_24),
                            value = stringResource(id = updatedSortingOrder?.toResource() ?: sortingOrder.toResource()),
                            enabled = (updatedSortingType ?: sortingType) != FolderListSortingType.Manual,
                        )
                    }

                    Spacer(modifier = Modifier.height(NotoTheme.dimensions.extraLarge))

                    Button(
                        text = stringResource(id = R.string.apply),
                        onClick = {
                            viewModel.updateFoldersView(
                                sortingType = updatedSortingType ?: sortingType,
                                sortingOrder = updatedSortingOrder ?: sortingOrder,
                            ).invokeOnCompletion { dismiss() }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}