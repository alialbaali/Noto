package com.noto.app.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import com.noto.app.R
import com.noto.app.components.BaseDialogFragment
import com.noto.app.components.BottomSheetDialog
import com.noto.app.components.SelectableDialogItem
import com.noto.app.domain.model.SortingOrder
import com.noto.app.util.Constants
import com.noto.app.util.navController
import com.noto.app.util.toStringResourceId
import org.koin.androidx.viewmodel.ext.android.viewModel

class FolderListOrderingDialogFragment : BaseDialogFragment() {

    private val viewModel by viewModel<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? = context?.let { context ->
        val navController = navController
        val savedStateHandle = navController?.previousBackStackEntry?.savedStateHandle

        ComposeView(context).apply {
            if (navController == null || savedStateHandle == null) return@apply

            setContent {
                val sortingOrder by viewModel.sortingOrder.collectAsState()
                val types = SortingOrder.entries
                val updatedSortingOrder by savedStateHandle.getStateFlow<SortingOrder?>(key = Constants.SortingOrder, initialValue = null)
                    .collectAsState()

                BottomSheetDialog(title = stringResource(R.string.ordering)) {
                    types.forEach { type ->
                        SelectableDialogItem(
                            selected = type == (updatedSortingOrder ?: sortingOrder),
                            onClick = { navController.previousBackStackEntry?.savedStateHandle?.set(Constants.SortingOrder, type); dismiss() },
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(text = stringResource(id = type.toStringResourceId()))
                        }
                    }
                }
            }
        }
    }
}